package com.paperboy.dao

import slick.driver.MySQLDriver
import slick.jdbc.JdbcBackend.Database
import slick.lifted.{Rep, TableQuery, Tag}
import slick.driver.MySQLDriver.api._
import com.paperboy.model.NewsAgent
import com.paperboy.model._

/**
  * Created by Ahmed Salah
  */
object DB extends MySQLDriver{

  val db = Database.forConfig("mariadb")


  class NewsAgents(tag: Tag) extends Table[NewsAgent](tag, "NEWS_AGENTS") {
    def agentId: Rep[Int] = column[Int]("AGENT_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def url: Rep[String] = column[String]("URL", O.Length(50))

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (agentId, url) <> ((NewsAgent.apply _).tupled, NewsAgent.unapply)
  }
  val newsAgents = TableQuery[NewsAgents]

  class NewsDetails(tag: Tag) extends Table[NewsDetail](tag, "NEWS_DETAILS") {
    def id: Rep[Int] =  column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def agentId: Rep[Int] = column[Int]("AGENT_ID")
    def newsUrl: Rep[String] = column[String]("NEWS_URL", O.Length(50))
    def header: Rep[String] = column[String]("HEADER", O.Length(50))
    def body: Rep[String] = column[String]("BODY", O.Length(50))

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, agentId, newsUrl, header, body) <> ((NewsDetail.apply _).tupled, NewsDetail.unapply)

    // foreign key to news agent table
    def newsAgent = foreignKey("NEWS_DETAILS_NEWS_AGENT_FK", agentId, newsAgents)(_.agentId)
  }

  val newsDetails = TableQuery[NewsDetails]

  //printCreateStatement()
  def printCreateStatement(): Unit = {
    //newsAgents.schema.createStatements.foreach(s => println(s.toUpperCase.concat(";")))
    newsDetails.schema.createStatements.foreach(s => println(s.toUpperCase.concat(";")))
  }
}
