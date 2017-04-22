

package com.paperboy.controllers

import akka.actor.Actor
import akka.actor.ReceiveTimeout
import scala.concurrent.duration._




class PaperBoyMain extends Actor{
  import Reciptionist._
  
  val reciptionist = context.actorOf(Reciptionist.props)
  println("starting actor")
  reciptionist ! Get("http://www.youm7.com")
  context.setReceiveTimeout(10 second)
  
  def receive = {
    case Result(url, set) => 
      val mySeq = set.toSeq.map(x => url+x)
      val temp = mySeq.filter(p => p.contains("story"))
      temp.foreach(println)
      
    case Failed(url) =>
      println("faild to fetch ", url)
      
    case ReceiveTimeout => 
      println("timeout")
      context.stop(self)
    
  }
  
  override def postStop(): Unit = {
    WebClient.shutDown()
  }
  
}
