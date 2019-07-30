package bojack

import java.util.Scanner

import com.typesafe.scalalogging.StrictLogging

import scala.collection.JavaConverters._


object App extends StrictLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Copy input data below:")

    val input = new Scanner(System.in).asScala.mkString
    val (agents, jobs, requests) = JsonParser.fromJson(input)

    val responseSeq = Processor.process(agents, jobs, requests)
    val output = JsonParser.toJson(responseSeq)

    logger.info(s"Result:\n$output")
  }

}
