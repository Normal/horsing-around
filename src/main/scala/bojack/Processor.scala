package bojack

import bojack.model.{Agent, Job, Request, Response}

object Processor {

  def process(agents: Seq[Agent], jobs: Seq[Job], requests: Seq[Request]): Seq[Response] = {

    val jobQueue = new JobQueue(jobs)

    val requestedAgents: Seq[Agent] = requests
      .map(r => agents.find(_.id == r.agentId))
      .filter(_.isDefined)
      .map(a => a.get)

    requestedAgents
      .map(agent => jobQueue.dequeue(agent)
        .map(job => Response(agent.id, job.id))
        .getOrElse(Response(agent.id, "Job-not-found"))
      )
  }

}
