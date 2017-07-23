package com.paperboy.controllers

import akka.actor.Actor
import scala.concurrent.duration._
import com.paperboy.controllers.NewsAgentsController._
import com.paperboy.model._
import scala.concurrent.Future
import org.jsoup.nodes.Element

class PaperBoyMain extends Actor{
  import Reciptionist._
  import context.dispatcher

  val reciptionist = context.actorOf(Reciptionist.props)
  val newsAgentController = context.actorOf(NewsAgentsController.props)
  val parser = context.actorOf(Parser.props)



  newsAgentController ! GET_AGENTS
  parser ! "http://www.youm7.com/story/2017/7/17/بعد-22-يوم-عرض-السقا-يصل-لـ43-مليونًا-ويسجل-أعلى/3329107"

  context.setReceiveTimeout(10 second)
  
  def receive: Receive = {
    case Result(linksCollection: LinksCollection) => {
      val baseUrl = linksCollection.agentUrl
      val mySeq = linksCollection.links.filter(x => x.contains("story")).map(x => baseUrl + x)
      //mySeq.foreach(println)
    }

    case Failed(url) =>
      println("failed to fetch ", url)

    case future: Future[Seq[NewsAgent]] => future.onSuccess {
      case seq => seq.foreach {
        // send the receptionist each news agent to parse
        case newsAgent => reciptionist ! Reciptionist.Get(newsAgent)
      }
    }
    case title: String =>
      println(title)

    case list: Iterator[Element] =>
      list.foreach(println)

    /*case ReceiveTimeout =>
      println("timeout")
      context.stop(self)*/

  }
  
  override def postStop(): Unit = {
    WebClient.shutDown()
  }
}
