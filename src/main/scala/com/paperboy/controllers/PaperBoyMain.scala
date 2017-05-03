

package com.paperboy.controllers

import akka.actor.Actor
import akka.actor.ReceiveTimeout

import scala.concurrent.duration._





class PaperBoyMain extends Actor{
  import Reciptionist._
  import com.paperboy.dao.DB._
  ///
  // connect to the database named "mysql" on port 8889 of localhost
 /* val url = "jdbc:mariadb://localhost:3310/paperboy"
  val driver = "org.mariadb.jdbc.Driver"
  val username = "root"
  val password = ""
  var connection:Connection = _
  try {
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement
    val rs = statement.executeQuery("select title from newsAgent")
    while (rs.next) {
      val title = rs.getString("title")

      println("title is ",title)
    }
  } catch {
    case e: Exception => e.printStackTrace
  }
  connection.close*/






  ////
  com.paperboy.dao.DB.printCreateStatment()
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
