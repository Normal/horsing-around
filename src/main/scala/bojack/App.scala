package bojack

import java.util.Scanner

import bojack.handling.{JsonParser, RequestHandler}
import bojack.model.Response
import com.typesafe.scalalogging.StrictLogging
import org.slf4j.{Logger, LoggerFactory}

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}


object App extends StrictLogging {

  val errorLog: Logger = LoggerFactory.getLogger("error")

  def main(args: Array[String]): Unit = {
    logger.info("Copy input data below:")

    val input: String = new Scanner(System.in).asScala.mkString
    JsonParser.fromJson(input) match {

      case Failure(ex) =>
        errorLog.warn("Can't parse input data", ex)
        logger.info("Can't parse any data from the input (incorrect format probably) " +
          "please check error logs for details")

      case Success((agents, jobs, requests, errors)) =>

        if (errors.nonEmpty) {
          logger.info("Some entities were parsed with errors, check error log for details")
          errors.foreach(er => errorLog.warn("Element wasn't parsed", er.ex))
        }

        val responseSeq: Seq[Response] = RequestHandler.process(agents, jobs, requests)
        val output: String = JsonParser.toJson(responseSeq)

        logger.info(s"Result:\n$output")

    }
  }
}
