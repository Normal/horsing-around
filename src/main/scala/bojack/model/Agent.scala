package bojack.model

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

case class Agent(id: String,
                 name: String,
                 primarySkills: Seq[String],
                 secondarySkills: Seq[String]
                )

object Agent {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  def apply(json: JValue): Agent =
    json
      .transformField {
        case ("primary_skillset", x) => ("primarySkills", x)
        case ("secondary_skillset", x) => ("secondarySkills", x)
      }.extract[Agent]


}
