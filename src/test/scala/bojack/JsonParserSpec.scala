package bojack

import bojack.model.{Agent, Job, Request, Response}
import org.scalatest.{FunSpec, Matchers}


class JsonParserSpec extends FunSpec with Matchers {

  describe("Json parser") {

    it("should read an empty input") {
      val (s1, s2, s3) = JsonParser.fromJson("[]")

      s1 should have length 0
      s2 should have length 0
      s3 should have length 0
    }

    it("should read agents in") {
      val input = """[{
                    |    "new_agent": {
                    |      "id": "a1",
                    |      "name": "Todd Chavez",
                    |      "primary_skillset": [
                    |        "rewards-question"
                    |      ]
                    |    }
                    |  },
                    |  {
                    |    "new_agent": {
                    |      "id": "a2",
                    |      "name": "Sarah Lynn",
                    |      "secondary_skillset": [
                    |        "bills-questions"
                    |      ]
                    |    }
                    |  }]""".stripMargin
      val (agents, _, _) = JsonParser.fromJson(input)

      val expected: Seq[Agent] = Seq(
        Agent("a1", "Todd Chavez", Seq("rewards-question"), Seq.empty[String]),
        Agent("a2", "Sarah Lynn", Seq.empty[String], Seq("bills-questions"))
      )

      assert(agents == expected)
    }

    it("should read jobs in") {
      val input = """[{
                    |    "new_job": {
                    |      "id": "j1",
                    |      "type": "bills-questions",
                    |      "urgent": false
                    |    }
                    |  },
                    |  {
                    |    "new_job": {
                    |      "id": "j2",
                    |      "type": "rewards-question",
                    |      "urgent": true
                    |    }
                    |  }]""".stripMargin

      val (_, jobs, _) = JsonParser.fromJson(input)

      val expected: Seq[Job] = Seq(
        Job("j1", "bills-questions", urgent = false),
        Job("j2", "rewards-question", urgent = true)
      )

      assert(jobs == expected)
    }

    it("should read requests in") {
      val input = """[{
                    |    "job_request": {
                    |      "agent_id": "1"
                    |    }
                    |  }]""".stripMargin

      val (_, _, requests) = JsonParser.fromJson(input)

      val expected: Seq[Request] = Seq(Request("1"))
      assert(requests == expected)
    }

    it("should write response in") {
      val input = Seq(Response(agentId = "a1", jobId = "j1"))
      val jsonStr = JsonParser.toJson(input)

      val expected = """[ {
                       |  "job_assigned" : {
                       |    "job_id" : "j1",
                       |    "agent_id" : "a1"
                       |  }
                       |} ]""".stripMargin

      assert(jsonStr == expected)
    }
  }

}
