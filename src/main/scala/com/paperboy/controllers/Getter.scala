

package com.paperboy.controllers

import akka.actor.Actor
import java.util.concurrent.Executor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import scala.util.Failure
import akka.actor.Status
import akka.actor.Props
import akka.actor.actorRef2Scala


object Getter {
  
  val props = Props[Getter]
  
  case object Done
  case object Failed
  case object Abort
}
class Getter(url: String, depth: Int) extends Actor{
  import Getter._
  
  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  

 
  
  // this function from WebClient to get the body of certian url
  val future: Future[String] = WebClient.get(url)
  
  future onComplete {
    case Success(body) => self ! body
    case Failure(err) => self ! Status.Failure(err)
  }
  
  // the recieve which is the result of the parsing of url
  
  def receive = {
    case body: String => 
      for(link <- WebClient.findLinks(body))
          context.parent ! Contoller.Check(link, depth)
           stop()
        
    case _: Status.Failure => stop()
    case Abort => stop() 
  }
  
  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
  
}