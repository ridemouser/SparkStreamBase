package com.demo.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import kafka.serializer.StringDecoder
import org.apache.spark.{SparkContext, TaskContext, SparkConf}
// import org.apache.spark.streaming.kafka.{OffsetRange, HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SparkSession
import com.google.common.io.Resources; 
import org.apache.kafka.common.serialization.StringDeserializer
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;
import java.io._

import org.apache.spark.sql.{DataFrame,SQLContext}
import play.api.libs.json._

//import collection.JavaConversions._
//import collection.JavaConverters._
//import com.typesafe.config.Config
//import com.typesafe.config._


object SparkParser {
	def main(args: Array[String]): Unit = {
			if (args.length < 2) {
				System.err.println(s"""
						|Usage: DirectKafkaWordCount <brokers> <topics>
						|  <brokers> is a list of one or more Kafka brokers
						|  <topics> is a list of one or more kafka topics to consume from
						|
						""".stripMargin)
						System.exit(1)
			}

			val Array(brokers, topics) = args

	    // Create context with 10 second batch interval
			val sparkConf = new SparkConf().setAppName("SparkParser").setMaster("local")

			val ssc = new StreamingContext(sparkConf, Seconds(10))
			System.out.println("Started Streaming Context")
			val sqlContext = SparkSessionSingleton.getInstance(sparkConf)
			// Create direct kafka stream with brokers and topics
			val topicsSet = topics.split(",").toSet
			/*
    val messages = KafkaUtils.createDirectStream[String, String] (
      ssc, PreferConsistent, Subscribe[String, String](topicsSet, kafkaParams))*/

			val kafkaParams = Map[String, Object](
					"bootstrap.servers" -> "localhost:9092",
					"key.deserializer" -> classOf[StringDeserializer],
					"value.deserializer" -> classOf[StringDeserializer],
					"group.id" -> "test",
					"auto.offset.reset" -> "latest",
					"enable.auto.commit" -> (false: java.lang.Boolean)
					)
					val messages = KafkaUtils.createDirectStream[String, String] (
							ssc, PreferConsistent, Subscribe[String, String](topicsSet, kafkaParams))


							import sqlContext.implicits._
							import sqlContext.sql
							// Convert Hive table into a Spark Dataframe
							val accountDF = sql("SELECT * FROM account") 
							accountDF.createOrReplaceTempView("accounts")
							System.out.println("Hive Account table")  
							accountDF.printSchema()
							accountDF.show()
							// test

							messages.foreachRDD(x => {      
								if (x != null) {
									val y = x.map(record => (record.key, record.value)).map(_._2)
											System.out.println("Starting datdrame read json command")
											val df = sqlContext.read.json(y) // add schema for transactions
											System.out.println("Kafka Transaction table")
											df.printSchema()
											df.show()

											// Create a temporary view over transactions received from kafka

											df.createOrReplaceTempView("kafka_trans")                                 // Your DF Operations
											if( df.count() > 1) {
												val joinDF = sqlContext.sql("select t.*,a.acc_credit_limit,a.acc_type from kafka_trans t JOIN accounts a ON t.acct_num = a.acct_num").write.mode("append")
														.saveAsTable("transactions")      
														System.out.println(joinDF) 
											}


								}
							}
									)

									// Start the computation
									ssc.start()
									ssc.awaitTermination()
	}
}
/** Lazily instantiated singleton instance of SparkSession */
object SparkSessionSingleton {

	@transient  private var instance: SparkSession = _

			def getInstance(sparkConf: SparkConf): SparkSession = {
			if (instance == null) {
				instance = SparkSession
						.builder
						.config(sparkConf)
						.config("spark.sql.warehouse.dir", "hdfs://hive-docker:9000/table")
						.enableHiveSupport()
						.getOrCreate()
			}
			instance
	}
}
