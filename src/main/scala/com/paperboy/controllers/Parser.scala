package com.paperboy.controllers

import java.util.concurrent.Executor
import org.jsoup.Jsoup
import com.paperboy.dao.DB.api._
import akka.actor.{Actor, Props}
import com.paperboy.dao.DB._
import com.paperboy.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
/**
  * Created by MAC Windows on 7/17/2017.
  */

object Parser {
  val props = Props[Parser]
}
class Parser(agentId: Int, url: String) extends  Actor {

  implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]


  val document = Jsoup.connect(url).get()
  val title = document.title()
  val body: String = document.getElementsByTag("p").text().substring(0, 500)
  val future = persistNews(agentId, url, title, body)

  future onComplete {
    case Success(x) => context.stop(self)
    case Failure(err) => {
      println(err)
      context.stop(self)
    }
  }

  override def receive: Receive = {
    case url: String => {
      val document = Jsoup.connect(url).get()
      val title = document.title()
      println(title)
      val body: String = document.getElementsByTag("p").text().substring(0, 500)
      //
      val x: Future[NewsDetail] = persistNews(1, url, title, body)
      x onComplete {
        case Success(x) => context.stop(self)
        case Failure(err) => context.stop(self)
      }
    }
  }

  def persistNews(agentId: Int, url: String, title: String, body: String): Future[NewsDetail] = {

    def insert = newsDetails returning newsDetails.map(_.id) into ((entity, id) => entity.copy(id = id))
    val insertAction = insert += NewsDetail(0, 1, url, title, body)
    db.run(insertAction)
  }
}
