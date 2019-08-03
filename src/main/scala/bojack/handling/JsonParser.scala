package bojack.handling

import bojack.model._
import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.json4s.jackson.Serialization.writePretty

object JsonParser {

  def fromJson(input: String): (Seq[Agent], Seq[Job], Seq[Request]) = {

    val JArray(list) = parse(input)

    val models = list.map {
      case v if v \ "new_agent" != JNothing => Agent(v \ "new_agent")
      case v if v \ "new_job" != JNothing => Job(v \ "new_job")
      case v if v \ "job_request" != JNothing => Request(v \ "job_request")
    }

    models
      .foldLeft((Seq.empty[Agent], Seq.empty[Job], Seq.empty[Request])) { case ((agents, jobs, requests), el) =>
        el match {
          case a: Agent => (agents :+ a, jobs, requests)
          case j: Job => (agents, jobs :+ j, requests)
          case r: Request => (agents, jobs, requests :+ r)
        }
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
