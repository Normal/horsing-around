package bojack.handling

import bojack.model._
import org.json4s._
import org.json4s.jackson.Serialization.writePretty
import org.json4s.native.JsonMethods.parse

import scala.util.Try

case class ParsingError(ex: Throwable)

object JsonParser {

  private def parseJson(input: String): Try[(Seq[Any], Seq[Any])] = {

    Try { parse(input).asInstanceOf[JArray] }.map(jsonArray => {
      val JArray(arr) = jsonArray

      val elements = arr.map {
        case v if v \ "new_agent" != JNothing =>
          Try { Agent(v \ "new_agent") }.toEither
        case v if v \ "new_job" != JNothing =>
          Try { Job(v \ "new_job") }.toEither
        case v if v \ "job_request" != JNothing =>
          Try { Request(v \ "job_request") }.toEither
        case v =>
          Right(ParsingError(new RuntimeException(s"Element is not recognized: $v")))
      }.map {
        case Left(ex) => ParsingError(ex)
        case Right(entity) => entity
      }

      elements.partition(!_.isInstanceOf[ParsingError])
    })
  }

  def fromJson(input: String): Try[(Seq[Agent], Seq[Job], Seq[Request], Seq[ParsingError])] = {

    parseJson(input) map { case (valid, invalid) =>

      val (agents, jobs, requests) = valid
        .foldLeft((Seq.empty[Agent], Seq.empty[Job], Seq.empty[Request])) {
          case ((as, js, rs), el) =>
            el match {
              case a: Agent => (as :+ a, js, rs)
              case j: Job => (as, js :+ j, rs)
              case r: Request => (as, js, rs :+ r)
            }
        }

      val parsingErrors = invalid.map(_.asInstanceOf[ParsingError])
      (agents, jobs, requests, parsingErrors)
    }
  }

  def toJson(output: Seq[Response]): String = {

    implicit val formats: DefaultFormats.type = DefaultFormats
    import org.json4s.JsonDSL._

    val outputJObjects: List[JObject] = output
      .map(r => {
        JObject("job_assigned" -> (("job_id" -> r.jobId) ~ ("agent_id" -> r.agentId)))
      })
      .toList

    writePretty(JArray(outputJObjects))
  }

}
