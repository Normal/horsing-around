package bojack

import java.util.Scanner

import com.typesafe.scalalogging.StrictLogging

import scala.collection.JavaConverters._


object App extends StrictLogging {

  def main(args: Array[String]): Unit = {
    logger.info("It is alive")

    val input = new Scanner(System.in).asScala.mkString

    val (agents, jobs, requests) = JsonParser.read(input)

    logger.info(agents.mkString)
    logger.info(jobs.mkString)
    logger.info(requests.mkString)
  }

}
