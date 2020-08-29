package com.seongekim.tcc


//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.client.RestTemplate

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.fasterxml.jackson.databind.ObjectMapper
import com.seongekim.tcc.logic.*
import com.seongekim.tcc.shared.BlackWhiteImageProcessor
import com.seongekim.tcc.shared.ImageProcessor
import com.seongekim.tcc.shared.ImageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    val processorMap = mapOf<String, ImageProcessor>(
        "bw" to BlackWhiteImageProcessor(),
        "bw_gpu" to BlackWhiteGpuImageProcessor(this),
        "blur_gpu" to BlurGpuImageProcessor(this, 0.02f, 15.0f))

    val defaultMethod = "blur_gpu"

    suspend fun processImage(img: Bitmap, method: String, clocks: Clocks, dry: Boolean) {
        val wrappedImg = AndroidBitmapWrapper(img)
        clocks.overallClock.addSuspendBlock {
            clocks.cpuClock.startCpu()
            val processor = processorMap.getValue(method)
            val wrappedResult = processor.process(wrappedImg)
            if (!dry) {
                val imgView: ImageView = findViewById(R.id.image_view)
                withContext(Dispatchers.Main) {
                    imgView.setImageBitmap((wrappedResult as AndroidBitmapWrapper).bitmap)
                }
            }
            clocks.cpuClock.endCpu()
        }
    }

    fun multipartFile(bytes: ByteArray): MultiValueMap<String, Any> {
        val resource = object : ByteArrayResource(bytes) {
            override fun getFilename(): String {
                return "dummy";
            }
        }
        val map: MultiValueMap<String, Any> = LinkedMultiValueMap()
        map.add("name", "dummy");
        map.add("filename", "dummy");
        map.add("file", resource);
        return map
    }

    suspend fun processImageRemotely(img: Bitmap, host: String, method: String, clocks: Clocks, dry: Boolean) {
        val resImg = withContext(Dispatchers.IO) {
            var bytes: ByteArray? = null
            val restTemplate = RestTemplate()
            restTemplate.messageConverters.add(FormHttpMessageConverter());
            restTemplate.messageConverters.add(ByteArrayHttpMessageConverter());
            val multipart = multipartFile(AndroidBitmapWrapper(img).bytes())
            clocks.cpuClock.addCpuBlock {
                clocks.overallClock.addBlock {
                    bytes = restTemplate.postForObject(
                        "http://" + host + ":8080/image/" + method,
                        multipart,
                        ByteArray::class.java
                    )
                }
            }
            val response = ImageResponse.Deserialize(bytes!!)
            clocks.serverClock.add(response!!.requestMs.toDouble())
            AndroidBitmapWrapper.fromBytes(response!!.image)
        }
        if(!dry) {
            val imgView: ImageView = findViewById(R.id.image_view)
            withContext(Dispatchers.Main) {
                imgView.setImageBitmap((resImg as AndroidBitmapWrapper).bitmap)
            }
        }
    }

    fun getBitmap(size: Int?): Bitmap {
        val imgView: ImageView = findViewById(R.id.image_view)
        var img = imgView.drawable.toBitmap()
        if (size != null) {
            img = Bitmap.createScaledBitmap(img, 16*size/9, size, false)
        }
        return img
    }

    fun setText(text: String) {
        findViewById<TextView>(R.id.editText).setText(text)
    }

    fun setProgress(progress: Double) {
        findViewById<ProgressBar>(R.id.progressBar).setProgress(progress.roundToInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val localButton: Button = findViewById(R.id.localFilterButton)
        localButton.setOnClickListener{
            var clocks = Clocks("Local")
            GlobalScope.launch {
                processImage(getBitmap(null), defaultMethod, clocks, false)
                println(clocks.overallClock.string())
            }
        }
        val remoteButton: Button = findViewById(R.id.remoteFilterButton)
        remoteButton.setOnClickListener{
            var clocks = Clocks("Remote")
            GlobalScope.launch {
                processImageRemotely(getBitmap(null), BuildConfig.host, defaultMethod, clocks, false)
                println(clocks.overallClock.string())
                println(clocks.serverClock.string())
                println(clocks.getNetworkClock().string())
            }
        }

        val om = ObjectMapper()
        val experiments = om.readValue(
            resources.openRawResource(R.raw.experiments), Experiments::class.java)

        val experimentButton: Button = findViewById(R.id.experimentButton)
        experimentButton.setOnClickListener{
            GlobalScope.launch {
                val restTemplate = RestTemplate()
                restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter());
                for (experiment in experiments.experiments) {
                    println("Starting experiment ${experiment.name}.")
                    val newExperiment = Experiment()
                    val newLayers = ArrayList<ExperimentLayer>()
                    newExperiment.name = experiment.name
                    newExperiment.method = experiment.method
                    newExperiment.imageSizes = experiment.imageSizes
                    for (layer in experiment.layers) {
                        if (!layer.isEnabled)
                            continue;
                        val newLayer = ExperimentLayer()
                        newLayer.iterations = layer.iterations
                        newLayer.ip = layer.ip
                        newLayer.name = layer.name
                        newLayer.isEnabled = layer.isEnabled
                        newLayer.results = HashMap<Int, List<RollingClock>>()
                        for (size in experiment.imageSizes) {
                            val img = getBitmap(size)
                            var clocks = Clocks("${experiment.name}/${layer.name}/${size}")
                            var isLocal = layer.ip == "localhost";
                            var ip = layer.ip
                            if (layer.ip == "host") {
                                ip = BuildConfig.host
                            }
                            setText("${experiment.name} / ${layer.name}")
                            println("Starting layer ${layer.name}, size=${size}, ip=${ip}, running ${layer.iterations} iterations...")
                            setProgress(0.0)
                            for (i in 1..layer.iterations) {
                                if (isLocal) {
                                    processImage(img, experiment.method, clocks, true)
                                } else {
                                    processImageRemotely(
                                        img,
                                        ip,
                                        experiment.method,
                                        clocks,
                                        true
                                    )
                                }
                                setProgress(i.toDouble() / layer.iterations * 100.0)
                            }

                            val results = ArrayList<RollingClock>()
                            results.add(clocks.overallClock)
                            results.add(clocks.cpuClock)
                            if (!isLocal) {
                                results.add(clocks.serverClock)
                                results.add(clocks.getNetworkClock())
                            }
                            newLayer.results.set(size, results)
                            for (clock in results) {
                                println(clock.string())
                            }
                        }
                        newLayers.add(newLayer)
                    }
                    newExperiment.layers = newLayers
                    if(newLayers.isEmpty())
                        continue
                    restTemplate.postForLocation(
                        "http://" + BuildConfig.host + ":8080/experiment/" + URLEncoder.encode(newExperiment.name, StandardCharsets.UTF_8.toString()),
                        newExperiment)
                    println("Wrapping up experiment ${experiment.name}.")
                }
            }
        }
        setText("Idle")
        setProgress(0.0)
    }

    override fun onStart() {
        super.onStart()
        val img = ResourcesCompat.getDrawable(resources, R.drawable.pokee, null)
        findViewById<ImageView>(R.id.image_view).setImageDrawable(img)
    }
}
