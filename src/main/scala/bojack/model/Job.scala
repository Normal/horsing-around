package bojack.model

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

case class Job(id: String, `type`: String, urgent: Boolean)

object Job {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  def apply(json: JValue): Job = json.extract[Job]
}
