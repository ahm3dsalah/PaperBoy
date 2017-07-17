package com.paperboy.controllers

import akka.actor.{Actor, ReceiveTimeout, Status}

import scala.concurrent.duration._
import com.paperboy.controllers.NewsAgentsController._
import com.paperboy.model._

import scala.concurrent.Future
import scala.util.{Failure, Success}





class PaperBoyMain extends Actor{
  import Reciptionist._
  import context.dispatcher


  com.paperboy.dao.DB.printCreateStatment()
  val reciptionist = context.actorOf(Reciptionist.props)
  val newsAgentController = context.actorOf(NewsAgentsController.props)


  newsAgentController ! GET_AGENTS


  context.setReceiveTimeout(10 second)
  
  def receive: Receive = {
    case Result(linksCollection: LinksCollection) => {
      val baseUrl = linksCollection.agentUrl
      val mySeq = linksCollection.links.filter(x => x.contains("story")).map(x => baseUrl+x)
      mySeq.foreach(println)
    }
      
    case Failed(url) =>
      println("failed to fetch ", url)

    case future : Future[Seq[NewsAgent]] => future.onSuccess {
      case seq => seq.foreach {
        // send the receptionist each news agent to parse
        case newsAgent => reciptionist ! Reciptionist.Get(newsAgent)
      }
    }
      
    case ReceiveTimeout => 
      println("timeout")
      context.stop(self)
    
  }
  
  override def postStop(): Unit = {
    WebClient.shutDown()
  }
  
}
