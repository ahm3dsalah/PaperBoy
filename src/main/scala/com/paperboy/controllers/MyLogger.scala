

package com.paperboy.controllers

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging


// this is custom logger which is used to log message as actor in order not block the main actor in the logging process
object MyLogger {
  val props = Props[MyLogger]
}
class MyLogger extends Actor with ActorLogging {
  
  def receive = {
    case msg: String => log.debug("receieved message {}", msg)
  }
  
}