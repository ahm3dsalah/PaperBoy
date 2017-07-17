package com.paperboy

/**
  * Created by MAC Windows on 5/21/2017.
  */
package object model {

  //DB Model
  case class NewsAgent(agentId: Int, url: String)
  case class NewsDetail(id: Int, agentId: Int, newsURL: String, header: String, body: String)
  case class PromiseRespons(agentUrl: String, body: String, agentId: Int)
  case class LinksCollection(agentId: Int, agentUrl: String,  links: Iterator[String])

  //Custom model to be used across app
  //case class()

}
