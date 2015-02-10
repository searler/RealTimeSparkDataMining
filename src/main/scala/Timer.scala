object Timer {
 def time[T](name: String)(block: => T): T = {
    val startTime = System.currentTimeMillis
    val result = block // call-by-name
    println(s"$name: ${System.currentTimeMillis - startTime}ms")
    result
  }
}