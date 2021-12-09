package fr.istic.mongo

import fr.istic.mongo.Tree.{getSchema, merge}
import fr.istic.{DataPath, Database, Query}
import org.mongodb.scala.{Document, MongoClient, MongoCollection}

import java.util.logging.Logger
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

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


  def createReadQuery(): Query = new MongoQuery(collection)

}
