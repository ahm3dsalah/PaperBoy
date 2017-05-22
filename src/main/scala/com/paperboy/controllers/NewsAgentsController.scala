package com.paperboy.controllers

import akka.actor.{Actor, Props}
import slick.lifted.Query
import com.paperboy.dao.DB._
import com.paperboy.model._
import com.paperboy.dao.DB.api._

import scala.concurrent.Future
/**
  * Created by MAC Windows on 5/22/2017.
  */
object NewsAgentsController {
  val props = Props[NewsAgentsController]
  val GET_AGENTS = "GET_AGENTS"
}
class NewsAgentsController extends Actor{
  import NewsAgentsController._
  import context.dispatcher
  //val tableQuery: TableQuery[NewsAgents] = newsAgents

  override def receive: Receive = {

    //when receiving a getAgent message this controller will reply with future of seq[NewsAGENTS]
    case GET_AGENTS => sender ! getAllAgents
  }

  def getAllAgents: Future[Seq[NewsAgent]] = {
    val query: Query[NewsAgents, NewsAgent, Seq] = for (
      newsAgent <- newsAgents
    ) yield newsAgent

    val action = for (
      seq: Seq[NewsAgent] <- query.result
    ) yield seq

    db.run(action)
  }
  //a function that reads news agents from DB and return it to caller




}
