package bojack

import java.util.Scanner

import bojack.handling.{JsonParser, RequestHandler}
import bojack.model.Response
import com.typesafe.scalalogging.StrictLogging

import scala.jdk.CollectionConverters._


object App extends StrictLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Copy input data below:")

    val input: String = new Scanner(System.in).asScala.mkString
    val (agents, jobs, requests) = JsonParser.fromJson(input)

    val responseSeq: Seq[Response] = RequestHandler.process(agents, jobs, requests)
    val output: String = JsonParser.toJson(responseSeq)

    logger.info(s"Result:\n$output")
  }

}
