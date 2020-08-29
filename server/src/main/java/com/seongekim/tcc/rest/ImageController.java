package com.seongekim.tcc.rest;

import com.seongekim.tcc.server.BlackWhiteGpuImageProcessor;
import com.seongekim.tcc.server.BlurGpuImageProcessor;
import com.seongekim.tcc.server.JavaImageWrapper;
import com.seongekim.tcc.shared.BlackWhiteImageProcessor;
import com.seongekim.tcc.shared.ImageProcessor;
import com.seongekim.tcc.shared.ImageResponse;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ImageController {

    public static Map<String, ImageProcessor> processorMap = new HashMap<String, ImageProcessor>() {
        {
            put("bw", new BlackWhiteImageProcessor());
            try {
                put("bw_gpu", new BlackWhiteGpuImageProcessor());
                put("blur_gpu", new BlurGpuImageProcessor(0.02f, 15.0f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static String experimentsFolder = "/opt/experiments";

    private static ImageResponse processAndTime(
            ImageProcessor processor,
            JavaImageWrapper wrappedImg) throws IOException {
        long startTime = System.nanoTime();
        byte[] data = ((JavaImageWrapper) processor.process(wrappedImg)).bytes();
        long endTime = System.nanoTime();
        return new ImageResponse((endTime - startTime) / (1000 * 1000), data);
    }

    @RequestMapping(
        value = "/image/{method}",
        method = RequestMethod.POST,
        consumes = {"multipart/mixed", "multipart/form-data"},
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseBody
    public byte[] postImageBlackWhite(
            @PathVariable("method") String method,
            @RequestPart("file") MultipartFile file) throws IOException {
        long startTime = System.nanoTime();
        JavaImageWrapper wrappedImg = JavaImageWrapper.fromStream(file.getInputStream());
        ImageProcessor processor = processorMap.get(method);
        return processAndTime(processor, wrappedImg).Serialize();
    }

    @RequestMapping(
            value = "/experiment/{name}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void postExperiment(
            @PathVariable("name") String experimentName,
            HttpServletRequest request)
            throws IOException {
        final String json = StreamUtils.copyToString(
                request.getInputStream(), StandardCharsets.UTF_8);
        File dir = new File(experimentsFolder);
        dir.mkdirs();
        PrintWriter writer = new PrintWriter(
                experimentsFolder + "/" + experimentName + ".json",
                "UTF-8");
        writer.println(json);
        writer.close();
    }
}
