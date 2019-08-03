package bojack

import bojack.handling.JobQueue
import bojack.model.{Agent, Job}
import org.scalatest.{FunSpec, Matchers}

class JobQueueSpec extends FunSpec with Matchers {

  describe("Job queue") {

    it("should dequeue jobs with preserved order") {

      val jobs = Seq(
        Job("j1", "type1", urgent = false),
        Job("j2", "type2", urgent = false),
        Job("j3", "type1", urgent = false),
        Job("j4", "type2", urgent = false),
        Job("j5", "type1", urgent = false)
      )

      val agents = Seq(
        Agent("a1", "Todd Chavez", Seq("type1"), Nil),
        Agent("a2", "Sarah Lynn", Seq("type2"), Nil)
      )

      val queue = new JobQueue(jobs)

      val agentToJobs: Map[String, Seq[String]] = agents
        .map(a => a.id -> queue.dequeueAll(a).map(j => j.id))
        .toMap

      assert(agentToJobs("a1") == Seq("j1", "j3", "j5"))
      assert(agentToJobs("a2") == Seq("j2", "j4"))

    }

    it("should assign urgent jobs first within agent's skill set level") {
      val jobs = Seq(
        Job("j1", "type1", urgent = false),
        Job("j2", "type2", urgent = false),
        Job("j3", "type1", urgent = false),
        Job("j7", "type3", urgent = false),
        Job("j4", "type2", urgent = true),
        Job("j5", "type1", urgent = true),
        Job("j6", "type3", urgent = true),
      )

      val agents = Seq(
        Agent("a1", "Todd Chavez", Seq("type1"), Nil),
        Agent("a2", "Sarah Lynn", Seq("type2"), Seq("type3"))
      )

      val queue = new JobQueue(jobs)

      val agentToJobs: Map[String, Seq[String]] = agents
        .map(a => a.id -> queue.dequeueAll(a).map(j => j.id))
        .toMap

      assert(agentToJobs("a1") == Seq("j5", "j1", "j3"))
      assert(agentToJobs("a2") == Seq("j4", "j2", "j6", "j7"))
    }

    it("shouldn't assign same job twice") {

      val jobs = Seq(
        Job("j1", "type1", urgent = false)
      )

      val agents = Seq(
        Agent("a1", "Todd Chavez", Seq("type1"), Nil),
        Agent("a2", "Sarah Lynn", Seq("type1"), Nil),
      )

      val queue = new JobQueue(jobs)

      val agentToJobs: Map[String, Seq[String]] = agents
        .map(a => a.id -> queue.dequeueAll(a).map(j => j.id))
        .toMap

      assert(agentToJobs("a1") == Seq("j1"))
      assert(agentToJobs("a2") == Nil)
    }

    it("shouldn't assign jobs if agent skill set doesn't match with job type") {
      val jobs = Seq(
        Job("j1", "type5", urgent = false),
        Job("j2", "type6", urgent = false),
      )

      val agents = Seq(
        Agent("a1", "Todd Chavez", Seq("type1"), Seq("type3")),
        Agent("a2", "Sarah Lynn", Seq("type2"), Seq("type4"))
      )

      val queue = new JobQueue(jobs)

      val agentToJobs: Map[String, Seq[String]] = agents
        .map(a => a.id -> queue.dequeueAll(a).map(j => j.id))
        .toMap

      assert(agentToJobs("a1") == Nil)
      assert(agentToJobs("a2") == Nil)
    }

    it("should assign job by secondary skills only if there is no suitable job in primary skills") {
      val jobs = Seq(
        Job("j1", "type1", urgent = false),
        Job("j2", "type2", urgent = false),
        Job("j3", "type3", urgent = true),
        Job("j4", "type4", urgent = false)
      )

      val agents = Seq(
        Agent("a1", "Todd Chavez", Seq("type1"), Seq("type3")),
        Agent("a2", "Sarah Lynn", Seq("type5"), Seq("type2")),
        Agent("a3", "Princess Carolyn", Nil, Seq("type4")),
        Agent("a4", "Diane Nguyen", Seq("type2"), Seq("type3")),
      )

      val queue = new JobQueue(jobs)
      val agentToJobs: Map[String, String] = agents
        .map(a => a.id -> queue.dequeue(a).map(j => j.id).get)
        .toMap

      assert(agentToJobs("a1") == "j1")
      assert(agentToJobs("a2") == "j2")
      assert(agentToJobs("a3") == "j4")
      assert(agentToJobs("a4") == "j3")
    }

    it("should return None if there is no suitable job") {
      val jobs = Seq(
        Job("j1", "type3", urgent = false)
      )

      val agents = Seq(
        Agent("a1", "Todd Chavez", Seq("type1"), Nil),
        Agent("a2", "Sarah Lynn", Seq("type2"), Nil)
      )

      val queue = new JobQueue(jobs)

      assert(queue.dequeue(agents(0)).isEmpty)
      assert(queue.dequeue(agents(1)).isEmpty)
    }

  }
}
