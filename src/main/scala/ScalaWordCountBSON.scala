/*
 * ScalaWordCount.scala
 * Written in 2014 by Sampo Niskanen / Mobile Wellness Solutions MWS Ltd
 * 
 * To the extent possible under law, the author(s) have dedicated all copyright and
 * related and neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 * 
 * See <http://creativecommons.org/publicdomain/zero/1.0/> for full details.
 */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.hadoop.conf.Configuration
import org.bson.BSONObject
import org.bson.BasicBSONObject
import sifti.mongo.bson.BSONFileInputFormatGeneric


object ScalaWordCountBSON {

  def main(args: Array[String]) {

    val sc = new SparkContext("local", "Scala Word Count")
    
    val config = new Configuration()

    config.set("mapred.input.dir", "file:///Users/ifti/mongo-spark/beowulf/beowulf/input.bson");
    config.set("mapred.output.dir", "file:///Users/ifti/mongo-spark/beowulf/beowulf/output.bson");

   val mongoRDD = sc.newAPIHadoopRDD(config, classOf[BSONFileInputFormatGeneric[Object,BSONObject]], classOf[Object], classOf[BSONObject])

    // Input contains tuples of (ObjectId, BSONObject)
    val countsRDD = mongoRDD.flatMap(arg => {
      var str = arg._2.get("text").toString
      str = str.toLowerCase().replaceAll("[.,!?\n]", " ")
      str.split(" ")
    })
    .map(word => (word, 1))
    .reduceByKey((a, b) => a + b)
    
    // Output contains tuples of (null, BSONObject) - ObjectId will be generated by Mongo driver if null
    val saveRDD = countsRDD.map((tuple) => {
      var bson = new BasicBSONObject()
      bson.put("word", tuple._1)
      bson.put("count", tuple._2)
      (null, bson)
    })
    

    saveRDD.saveAsNewAPIHadoopFile("file:///Users/ifti/mongo-spark/beowulf/testoutput.bson", classOf[Any], classOf[Any], classOf[com.mongodb.hadoop.BSONFileOutputFormat[Any, Any]], config)

  }
}
