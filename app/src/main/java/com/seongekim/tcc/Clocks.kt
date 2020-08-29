package com.seongekim.tcc

import com.seongekim.tcc.logic.RollingClock

class Clocks(name: String) {
    val prefix = name
    val overallClock = RollingClock(name + "_Overall")
    val serverClock = RollingClock(name + "_Server")
    val cpuClock = RollingClock(name + "_CPU")

    fun getNetworkClock(): RollingClock {
        return overallClock.subtractWith(serverClock, prefix + "_Network")
    }
}