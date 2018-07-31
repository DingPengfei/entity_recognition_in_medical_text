import java.util.logging.{Level, Logger}

import org.ansj.recognition.impl.StopRecognition
import org.ansj.splitWord.analysis.DicAnalysis
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Created by Ding on 7/31/2018.
  */
object TextClassificationDemo {
  def main(args: Array[String]) = {
    Logger.getLogger("org.apache.spark").setLevel(Level.INFO)
    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val stop = new StopRecognition()
    stop.insertStopNatures("w")//过滤掉标点
    stop.insertStopNatures("m")//过滤掉m词性
    stop.insertStopNatures("null")//过滤掉null词性
    stop.insertStopNatures("<br/>")//过滤掉<bt/>词性
    stop.insertStopNatures(":")
    stop.insertStopNatures("’")

    val spark = SparkSession.builder().appName("textClassificationDemo").master("local").getOrCreate()
    val data = spark.sparkContext.textFile("src/main/scala/files/a.txt")

    val splits = data.map(_.replaceAll("[1-9]\\.", "\r\n")).map(_.replaceAll("<.*?>", ""))

//    splits.saveAsTextFile("src/main/scala/files/b")

    val doc = splits.map(x => DicAnalysis.parse(x).recognition(stop))

    doc.collect().foreach(println)


    spark.close()

  }

}
