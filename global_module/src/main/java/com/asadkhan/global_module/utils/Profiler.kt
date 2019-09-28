package com.asadkhan.global_module.utils

import com.asadkhan.global_module.now
import timber.log.Timber.e

object Profiler {

  private var startTime = 0L

  fun start() {
    if (startTime != 0L) {
      e("Start called before stopping previous timer!")
      return
    }
    startTime = now()
  }

  fun stop(name: String = "Method") {
    if (startTime == 0L) {
      e("Could not profile method: $name")
      e("Stop called before starting a timer!")
      return
    }
    val stopTime = now()
    val diff = stopTime - startTime
    startTime = 0L
    e("")
    e("\t\t<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>")
    e("")
    e("\t\tProfiling method: $name")
    e("\t\tExecution time: ${diff}ms")
    e("")
    e("\t\t<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>")
    e("")
  }

}
