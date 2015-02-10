
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object Main extends App {
  //LogManager.getRootLogger().setLevel(Level.WARN)

  val conf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("RealtimeMining")
    .set("spark.local.dir", "tmp")
  val sc = new SparkContext(conf)
  val minSplits = 1
  val jsonData = sc.textFile(Post.file.getAbsolutePath, minSplits)
  val objData = jsonData.flatMap(Post.parse)
  objData.persist(StorageLevel.MEMORY_AND_DISK)

  var query: RDD[Post] = objData

  println("Enter new command:")
  do {
  } while (readCommand)
  println("Exit")

  def readCommand: Boolean = {
    import Timer._
    val command = scala.io.StdIn.readLine(">:")
    if (command == null ||
      command.isEmpty) false
    else {
      //...match commands
      command match {
        case c if c.startsWith("t:") => {
          //filter for posts that contain any of the comma separated list of tags.
          val tags = c.drop(2).split(",").toSet
          query = query.filter(_.tags.exists(tags.contains))
        }
        case c if c.startsWith("d:") => {
          //filter for posts that are within the date range
          val d = c.drop(2).split(",").map(i => Post.parseDate(i + "T00:00:00.000"))
          query = query.filter(n => n.creationDate >= d(0) && n.creationDate < d(1))
        }
        case "!" => time("Count") {
          println(query.count)
        }
        case "!t" => time("Tags") {
          val tags = query.flatMap(_.tags).countByValue
          println(tags.toSeq.sortBy(_._2 * -1).take(10).mkString(","))
        }
        case "~" => {
          //reset all filters applied to query
          query = objData
        }
      }
      true
    }
  }
 
}


