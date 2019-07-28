package bojack

import java.util.Scanner

import com.typesafe.scalalogging.StrictLogging
import scala.collection.JavaConverters._

object App extends StrictLogging {

  def main(args: Array[String]): Unit = {
    logger.info("It is alive")

    val input = new Scanner(System.in).asScala.mkString
    logger.info(input)
  }

}
