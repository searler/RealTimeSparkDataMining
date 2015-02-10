import scala.util.Try
import scala.annotation.tailrec

object CommandLine {
  @tailrec
  def run(block: String => Unit):Unit = {
    def read = scala.io.StdIn.readLine(">:")
    var command = read
    if (command != null && !command.isEmpty()){
      Try(block(command)).recover {
        case e => println(e.getMessage)
      }
      run(block)
  }
 
  }

  def print[T](block: String => T) = run(s => println(block(s)))

  def echo[T](block: String => T) = run(s => {
    println(s)
    println(block(s))
  })
}