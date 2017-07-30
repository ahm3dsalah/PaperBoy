package com.paperboy.controllers

import akka.actor.{Actor, Props}

import scala.concurrent.duration._
import com.paperboy.controllers.NewsAgentsController._
import com.paperboy.model._

import scala.concurrent.Future

class PaperBoyMain extends Actor{
  import Reciptionist._
  import context.dispatcher

  val reciptionist = context.actorOf(Reciptionist.props)
  val newsAgentController = context.actorOf(NewsAgentsController.props)
  // get news agents first
  newsAgentController ! GET_AGENTS

  context.setReceiveTimeout(10 second)
  
  def receive: Receive = {
    case Result(linksCollection: LinksCollection) => {
      val agentId = linksCollection.agentId
      val baseUrl = linksCollection.agentUrl
      val links = linksCollection.links.map(x => baseUrl + x)

      links foreach { link =>
        context.actorOf(Props(new Parser(agentId, link)))
        //println(link)
      }
    }

    case future: Future[Seq[NewsAgent]] => future.onSuccess {
      case seq => seq.foreach {
        // send the receptionist each news agent to parse
        case newsAgent =>
          reciptionist ! Reciptionist.Get(newsAgent)
      }
      /*case ReceiveTimeout =>
      println("timeout")
      context.stop(self)*/
    }
  }
  
  override def postStop(): Unit = {
    WebClient.shutDown()
  }
}
