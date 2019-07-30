package bojack

import bojack.model.{Agent, Job}

class JobQueue(jobs: Seq[Job]) {

  val (highPriority, normalPriority) = jobs.partition(_.urgent)

  def dequeue(agent: Agent): (Option[Job], JobQueue) = {

    def find(jobs: Seq[Job], skills: Seq[String]): Option[Job] =
      jobs.find(j => skills.contains(j.`type`))

    val candidate: Option[Job] =
      find(highPriority, agent.primarySkills)
      .orElse(find(highPriority, agent.secondarySkills))
      .orElse(find(normalPriority, agent.primarySkills))
      .orElse(find(normalPriority, agent.secondarySkills))

    val queue = candidate.map(j => new JobQueue(jobs.filterNot(_.id == j.id))).getOrElse(this)
    (candidate, queue)
  }

}
