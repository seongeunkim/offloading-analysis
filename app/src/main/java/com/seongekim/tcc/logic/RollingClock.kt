package com.seongekim.tcc.logic

import android.os.Debug
import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.runBlocking
import java.lang.Math.sqrt
import kotlin.system.measureTimeMillis

class RollingClock(val name: String) {
    val items: ArrayList<Double> = ArrayList<Double>()
    @JsonIgnore
    var acc: Double = 0.0
    @JsonIgnore
    var startTime: Double = 0.0

    fun add(delta: Double) {
        items.add(delta)
    }

    fun sum(): Double {
        var res = 0.0
        for(x in items) {
            res += x
        }
        return res
    }

    fun sumSquared(): Double {
        var res = 0.0
        for(x in items) {
            res += x*x
        }
        return res
    }

    fun iterations(): Int {
        return items.size
    }

    fun average(): Double {
        return sum() / iterations()
    }

    fun variance(): Double {
        var res = 0.0
        var avg = average()
        for(x in items) {
            res += (x - avg) * (x - avg)
        }
        return res / (iterations() - 1)
    }

    fun stddev(): Double {
        return sqrt(variance())
    }

    fun addBlock(block: () -> Unit) {
        add(measureTimeMillis(block).toDouble())
    }

    fun addSuspendBlock(block: suspend () -> Unit) {
        val delta = measureTimeMillis {
            runBlocking {
                block()
            }
        }
        add(delta.toDouble())
    }

    fun addCpuBlock(block: () -> Unit) {
        startCpu()
        block();
        endCpu()
    }

    fun threadTime(): Double {
        return Debug.threadCpuTimeNanos().toDouble() / (1000 * 1000);
    }

    fun startCpu() {
       startTime = threadTime();
    }

    fun pauseCpu() {
        val endTime = threadTime();
        acc += endTime - startTime;
        startTime = 0.0;
    }

    fun endCpu() {
        if (startTime == 0.0) {
            throw Exception("deu ruim")
        }
        pauseCpu();
        add(acc)
        acc = 0.0;
    }

    fun addTo(rhs: RollingClock, newName: String): RollingClock {
        var res = RollingClock(newName)
        assert(items.size == rhs.items.size)
        for(i in 0 until items.size) {
            res.add(items.get(i) + rhs.items.get(i))
        }
        return res
    }

    fun subtractWith(rhs: RollingClock, newName: String): RollingClock {
        var res = RollingClock(newName)
        assert(items.size == rhs.items.size)
        for(i in 0 until items.size) {
            res.add(items.get(i) - rhs.items.get(i))
        }
        return res
    }

    fun string(): String {
        val z = 1.96 * stddev() / sqrt(iterations().toDouble());
        return "${name} clock stats {avg: ${average()}, stddev: ${stddev()}, C95: [${average() - z}, ${average()  + z}], iterations: ${iterations()}}"
    }
}