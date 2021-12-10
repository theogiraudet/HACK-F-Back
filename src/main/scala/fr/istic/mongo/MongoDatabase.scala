package fr.istic.mongo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import fr.istic.mongo.Tree.{getSchema, merge}
import fr.istic.{Artist, DataPath, Database, Query}
import org.mongodb.scala.{Document, MongoClient, MongoCollection}

import java.util.logging.Logger
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Try

class MongoDatabase(val url: String, val databaseName: String, val collectionName: String) extends Database {

  private val logger = Logger.getLogger("Artist Ressource REST")
  lazy private val collection: MongoCollection[Document] = {
    val mongoClient = MongoClient(url)
    val database = mongoClient.getDatabase(databaseName)
    val collection = database.getCollection[Document](collectionName)
    collection
  }

  {
    logger.info("Initialize path...")
    val future = collection.find().map(_ - "_id").toFuture().map(d => DataPath.init(Some(d.map(x => getSchema(x)).reduce(merge))))
    Await.ready(future, Duration.Inf)
  }

  def write(artist: Artist): Try[Unit] = {
    val objectMapper = new ObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
    val json = objectMapper.writeValueAsString(artist)
    val future = collection.insertOne(Document(json)).toFuture()
    Await.ready(future, Duration.Inf)
    future.value.get.map(_ => ())
  }
  def createReadQuery(): Query = new MongoQuery(collection)

}
