 

package com.paperboy.controllers

import com.ning.http.client.{AsyncHttpClient, ListenableFuture, Response}

import scala.concurrent.Future
import java.util.concurrent.Executor

import scala.concurrent.Promise
import com.paperboy.model._

object WebClient {
  
  val A_TAG = "(?i)<a ([^>]+).+?</a>".r
  val HREF = """\s*(?i)href\s*=\s*(?:"([^"]*)"|'([^']*)'|([^'">\s]+))""".r
  
  // find link in result body
  def findLinks(promiseResponse: PromiseRespons): LinksCollection = {
    val links: Iterator[String] = for {
      anchor <- A_TAG.findAllMatchIn(promiseResponse.body)
      HREF(dquote, quote, bare) <- anchor.subgroups
    } yield
      if(dquote != null) dquote
      else if (quote != null) quote
      else bare

    LinksCollection(promiseResponse.agentId, promiseResponse.agentUrl, links)
  }
  
  
  //parse and get body
  val client = new AsyncHttpClient
  def get(newsAgent: NewsAgent)(implicit excec: Executor): Future[PromiseRespons] = {
    val agentId = newsAgent.agentId
    val f: ListenableFuture[Response] = client.prepareGet(newsAgent.url).execute()
    val p = Promise[PromiseRespons]()
    f.addListener(new Runnable {
      
      def run = {
        val respons = f.get()
        if(respons.getStatusCode < 400){
          p.success(PromiseRespons(newsAgent.url, respons.getResponseBodyExcerpt(1000000), agentId))
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