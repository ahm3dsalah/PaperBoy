package com.paperboy.dao

import slick.driver.MySQLDriver
import slick.jdbc.JdbcBackend.Database
import slick.lifted.{Rep, TableQuery, Tag}
import slick.driver.MySQLDriver.api._

/**
  * Created by Ahmed Salah
  */
object DB extends MySQLDriver{

  val db = Database.forConfig("mariadb")


  class Suppliers(tag: Tag) extends Table[(Int, String, String)](tag, "NEWS_AGENT") {
    def newsId: Rep[Int] = column[Int]("NEWS_ID", O.PrimaryKey) // This is the primary key column
    def header: Rep[String] = column[String]("HEADER", O.Length(50))
    def body: Rep[String] = column[String]("BODY", O.Length(50))

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (newsId, header, body)
  }
  val suppliers = TableQuery[Suppliers]
  suppliers.schema.createStatements.foreach(s => println(s.toUpperCase.concat(";")))

  def printCreateStatment(): Unit = {
    suppliers.schema.createStatements.foreach(s => println(s.toUpperCase.concat(";")))
  }
}
