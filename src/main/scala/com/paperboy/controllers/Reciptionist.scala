package com.paperboy.controllers

import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import com.paperboy.model._

object Reciptionist {
  
  val props = Props[Reciptionist]
  
  case class Job(client: ActorRef, newsAgent: NewsAgent)
  case class Failed(url: String)
  case class Get(newsAgent: NewsAgent)
  case class Result(linksCollection: LinksCollection)

  //case class Result(links: Set[String])
}
class Reciptionist extends Actor {
  import Reciptionist._

  // the init behaviour  of the actor
  def receive = waiting
  
  
  //waiting behaviour
  val waiting : Receive = {
    case Get(newsAgent: NewsAgent) => context.become(runNext(Vector(Job(sender, newsAgent))))
      
  }
  
  //run next behaviour to determine which behaviour will be one for the actor
  def runNext(queue: Vector[Job]) : Receive = {
    if (queue.isEmpty) {
      // return the waiting behaviour
      waiting
    }
    else {
      //create new getter actor to get links from news agent and return set to here
      context.actorOf(Props(new Getter(queue.head.newsAgent)))
      running(queue)
    }
  }
  
  // running behaviour
  def running(queue: Vector[Job]): Receive = {
    case Result(linksCollection: LinksCollection) =>
      val job = queue.head
      job.client ! Result(linksCollection)
      context.become(runNext(queue.tail))
      
    case Get(url) => context.become(enqueueJob(queue, Job(sender, url)))  
  }
  
  
  // if the actor get another request it will queue it
  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
      running(queue :+ job)
  }
}