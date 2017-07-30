

package com.paperboy.controllers

import akka.actor.Actor
import java.util.concurrent.Executor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import scala.util.Failure
import akka.actor.Status
import akka.actor.Props
import akka.actor.actorRef2Scala
import com.paperboy.model._

object Getter {
  
  val props = Props[Getter]
  
  case object Done
  case object Failed
  case object Abort
}
class Getter(newsAgent: NewsAgent) extends Actor{
  import Getter._
  
  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
  

 
  
  // this function from WebClient to get the body of certain url
  val future: Future[PromiseRespons] = WebClient.get(newsAgent)
  
  future onComplete {
    case Success(promiseResponse) => self ! promiseResponse
    case Failure(err) => self ! Status.Failure(err)
  }
  
  // the receive which is the result of the parsing of url
  
  def receive = {
    case promiseResponse: PromiseRespons =>
      // return links collection which contains agentId and links
      val linksCollection: LinksCollection = WebClient.findLinks(promiseResponse)
      context.parent ! Reciptionist.Result(linksCollection)
      stop()

    case _: Status.Failure => stop()
    case Abort => stop() 
  }
  
  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
  
}