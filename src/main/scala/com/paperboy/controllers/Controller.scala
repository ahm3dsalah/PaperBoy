

package com.paperboy.controllers

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import scala.concurrent.duration._ 
import akka.actor.ReceiveTimeout
import akka.actor.actorRef2Scala



object Contoller {
  val props = Props[Controller]
  case class Check(url: String, depth: Int)
  case class Result(links: Set[String])
}
class Controller extends Actor with ActorLogging{
  import Contoller._
  context.setReceiveTimeout(10 second)
  
  var cache = Set.empty[String]
  
  // set of child controllers
  var children = Set.empty[ActorRef]
  
  def receive: Receive = {
    case Check(url, depth) =>
     
      if(!cache(url) && depth > 0)
        children += context.actorOf(Props(new Getter(url, depth - 1)))
        cache +=url
    case Getter.Done =>
        children -= sender
        if(children.isEmpty) context.parent ! Result(cache)
    case ReceiveTimeout => children foreach { _ ! Getter.Abort }
      
  }
}