 

package com.paperboy.controllers

import com.ning.http.client.AsyncHttpClient
import scala.concurrent.Future
import java.util.concurrent.Executor
import scala.concurrent.Promise

object WebClient {
  
  val A_TAG = "(?i)<a ([^>]+).+?</a>".r
  val HREF = """\s*(?i)href\s*=\s*(?:"([^"]*)"|'([^']*)'|([^'">\s]+))""".r
  
  // find link in result body
  def findLinks(body:String): Iterator[String] = {
    for {
      anchor <- A_TAG.findAllMatchIn(body)
      HREF(dquote, quote, bare) <- anchor.subgroups
    } yield
      if(dquote != null) dquote
      else if (quote != null) quote
      else bare
  }
  
  
  //parse and get body
  val client = new AsyncHttpClient
  def get(url: String)(implicit excec: Executor): Future[String] = {
    val f = client.prepareGet(url).execute()
    val p = Promise[String]() 
    f.addListener(new Runnable {
      
      def run = {
        val respons = f.get()
        if(respons.getStatusCode < 400){
          p.success(respons.getResponseBodyExcerpt(1000000))
        }
        else
          throw new Exception("bad request")
      }
    }, excec)
    p.future
  }
  
  def shutDown() : Unit = {
    client.close()
  }
}