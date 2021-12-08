package fr.istic

import fr.istic.Tree.{getSchema, merge}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.{Document, MongoClient, MongoCollection}

import java.util.logging.Logger
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Try

object Database {

  private var url = ""
  private val databaseName = "hack"
  private val collectionName = "test"
  lazy private val collection: MongoCollection[Document] = {
    val mongoClient = MongoClient(url)
    val database = mongoClient.getDatabase(databaseName)
    val collection = database.getCollection[Document](collectionName)
    collection
  }

  private val logger = Logger.getLogger("Artist Ressource REST")

  def init(url: String): Unit = {
    this.url = url
    val future = collection.find().map(_ - "_id").toFuture().map(d => DataPath.init(Some(d.map(x => getSchema(x)).reduce(merge))))
    Await.ready(future, Duration.Inf)
  }

  def read(query: String): Try[String] = {
    val result = QueryParser.analyserFilter(query)
      .map(FiltersToMongo.toMongo)
      .flatMap(awaitGet)
      .map("[" + _.toList.map(x => x.toJson).mkString(",") +" ]")
    logger.info(result.toString)
    result
  }

  private def awaitGet(bson: Bson) = {
    val future = collection.find(bson).toFuture()
    Await.ready(future, Duration.Inf)
    future.value.get.map(x => x.map(_ - "_id"))
  }

}
