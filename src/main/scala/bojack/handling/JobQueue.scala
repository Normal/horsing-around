package bojack.handling

import bojack.model.{Agent, Job}

import scala.collection.mutable

/**
  * The main implementation idea is to create two queues for each job type.
  * One queue for urgent jobs and other for normal jobs.
  *
  * So it creates 2 * num_of_types queues which separated into two maps by urgent flag.
  * All elements sorted by initial order in input jobs seq.
  *
  */
class JobQueue(jobs: Seq[Job]) {

  // save initial order for correct priority evaluation
  private val jobsWithIndex: Seq[(Job, Int)] = jobs.zipWithIndex

  private val (urgentJobs, normalJobs) = jobsWithIndex.partition(_._1.urgent)

  private val highPriority: Map[String, mutable.Queue[(Job, Int)]] = createQueues(urgentJobs)
  private val normalPriority: Map[String, mutable.Queue[(Job, Int)]] = createQueues(normalJobs)

  private def createQueues(jobs: Seq[(Job, Int)]): Map[String, mutable.Queue[(Job, Int)]] =
    jobs
      .groupBy { case(job, index) => job.`type` }
      .view
      // my apologies for not functional approach here
      .mapValues(seq => mutable.Queue.from(seq.sortBy { case(job, index) => index}))
      .toMap

  /**
    * Retrieving of the most relevant job by passed skill set and map of queues.
    * Supposed time complexity is O(N), there N is number of skills.
    */
  private def findJob(skills: Seq[String], queues: Map[String, mutable.Queue[(Job, Int)]]): Option[Job] = {
    val queueWithTheMostRelevantHeadElement: Option[mutable.Queue[(Job, Int)]] = skills
      // lets get a queue for each skill
      .map(skill => queues.get(skill))
      .filter(maybeQueue => maybeQueue.isDefined && maybeQueue.get.nonEmpty)
      // now get head element's priority (index) from each queue
      .map(queue => queue.get -> queue.get.head._2)
      // take the one with the smallest index (the highest priority)
      .minByOption { case (queue, priority) => priority }
      .map { case (queue, priority) => queue}

    queueWithTheMostRelevantHeadElement.map(q => q.dequeue()._1)
  }

  /**
    * Retrieving of the most relevant job for all combinations of skills and job priorities.
    */
  def dequeue(agent: Agent): Option[Job] =
    findJob(agent.primarySkills, highPriority)
      .orElse(findJob(agent.primarySkills, normalPriority))
      .orElse(findJob(agent.secondarySkills, highPriority))
      .orElse(findJob(agent.secondarySkills, normalPriority))

  /**
    * Additional util method.
    * Returns Seq.empty if there is no suitable jobs.
    */
  def dequeueAll(agent: Agent): Seq[Job] = {

    def dequeueAll(a: Agent, jobs: Seq[Job]): Seq[Job] = {
      dequeue(a) match {
        case Some(j) => dequeueAll(a, jobs :+ j)
        case None => jobs
      }
    }

    dequeueAll(agent, Seq.empty)
  }

}
