package bojack

import bojack.model.{Agent, Job, Request, Response}

object Processor {


  def process(agents: Seq[Agent], jobs: Seq[Job], requests: Seq[Request]): Seq[Response] = {

    val initialQueue = new JobQueue(jobs)

    val requestedAgents: Seq[Agent] = requests
      .map(r => agents.find(_.id == r.agentId))
      .filter(_.isDefined)
      .map(a => a.get)

    val (_, responseSeq) = requestedAgents.foldLeft((initialQueue, Seq.empty[Response])) { case ((q, seq), a) =>
      val (maybeJob, iterQueue) = q.dequeue(a)
      val iterSeq = maybeJob.map(j => Response(a.id, j.id) +: seq).getOrElse(seq)
      (iterQueue, iterSeq)
    }

    responseSeq
  }

}
