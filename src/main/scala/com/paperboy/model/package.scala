package com.paperboy

/**
  * Created by MAC Windows on 5/21/2017.
  */
package object model {

  case class NewsAgent(agentId: Int, url: String)
  case class NewsDetail(id: Int, agentId: Int, newsURL: String, header: String, body: String)

}
