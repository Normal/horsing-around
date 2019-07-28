package bojack.model

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

case class Request(agentId: String) extends AnyVal

object Request {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  def apply(json: JValue): Request =
    json
      .transformField {
        case ("agent_id", x) => ("agentId", x)
      }
      .extract[Request]
}