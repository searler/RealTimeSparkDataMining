
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

object SQL extends App {

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

  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  import sqlContext.createSchemaRDD
  query.registerTempTable("posts")

  import Timer._

  time("load") {
    query.count()
  }

  time("count") {
    sqlContext.sql("SELECT count(1) FROM posts").foreach { println }
  }

  time("embedded") {
    import sqlContext._
    val cms = query.where('tags)((s: Seq[String]) => s.contains("cms")) //.select('id)
    cms.count()
  }

  CommandLine.echo(s => sqlContext.sql(s).collect().mkString("\n"))

}


