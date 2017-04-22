

package com.paperboy.controllers

import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import scala.Vector
import akka.event.LoggingReceive


object Reciptionist {
  
  val props = Props[Reciptionist]
  
  case class Job(client: ActorRef, url: String)
  case class Failed(url: String)
  case class Get(url: String)
  case class Result(url: String, links: Set[String])
}
class Reciptionist extends Actor {
  import Reciptionist._
  var reqNo = 0
  

  // the init behaviour  of the actor
  def receive = waiting
  
  
  //waiting behaviour
  val waiting : Receive = {
    case Get(url) => context.become(runNext(Vector(Job(sender, url))))
      
  }
  
  //run next behaviour to determine which behaviour will be one for the actor
  def runNext(queue: Vector[Job]) : Receive = {
    reqNo +=1
    if(queue.isEmpty) 
       // return the wating behaviour
      waiting 
     
    else {
      val controller = context.actorOf(Contoller.props)
      controller ! Contoller.Check(queue.head.url, 1)
      running(queue)
    }
  }
  
  // running behaviour
  def running(queue: Vector[Job]): Receive = {
    case Contoller.Result(links) =>
      val job = queue.head
      job.client ! Result(job.url, links)
      context.stop(sender)
      context.become(runNext(queue.tail))
      
    case Get(url) => context.become(enqueueJob(queue, Job(sender, url)))  
  }
  
  
  ////enqueueJob behaviour to determine which behaviour will be one for the actor
  def enqueueJob(queue: Vector[Job], job: Job): Receive = {
    if(queue.size > 3) {
      
      sender ! Failed(job.url)
      running(queue)
    } else {
      running(queue :+ job)
    }
  }
}