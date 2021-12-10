package fr.istic.mongo

import fr.istic.Query
import fr.istic.parameters.QueryParameters
import fr.istic.parameters.QueryParameters._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{Filters, Sorts}
import org.mongodb.scala.{Document, MongoCollection}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try


class MongoQuery(private val collection: MongoCollection[Document]) extends Query {

  private var filter: List[Filter] = List()
  private var sorting: List[Sorting] = List()

  def apply(parameter: QueryParameter): Query = {
    parameter match {
      case x: Filter => filter ::= x
      case x: Sorting => sorting ::= x
    }
    this
  }

  def send(): Try[String] = {
    val filterApplied = filter.filter(_ != NoFilter)
      .reduceLeftOption(QueryParameters.&&)
      .map(adaptFilter)
      .map(collection.find(_))
      .getOrElse(collection.find)

    val sortApplied = sorting.filter(_ != NoSorting)
      .reduceLeftOption((x, y) => QueryParameters.orderBy(x :: y :: Nil))
      .map(adaptSorting)
      .map(filterApplied.sort)
      .getOrElse(filterApplied)

    val future = sortApplied.toFuture()
    Await.ready(future, Duration.Inf)
    future.value.get.map(x => x.map(_ - "_id")).map("[" + _.toList.map(x => x.toJson).mkString(",") + " ]")
  }

  private def adaptFilter(filter: Filter): Bson = {
    filter match {
      case isNull(a, true) => Filters.not(Filters.exists(a.path))
      case isNull(a, false) => Filters.exists(a.path)
      case a == b => Filters.eq(a.path, b)
      case a != b => Filters.ne(a.path, b)
      case a < b => Filters.lt(a.path, b)
      case a <= b => Filters.lte(a.path, b)
      case a > b => Filters.gt(a.path, b)
      case a >= b => Filters.gte(a.path, b)
      case a contains List() => Filters.exists(a.path)
      case a contains hd :: Nil => adaptFilter(QueryParameters.==(a, hd))
      case a contains hd :: tl => adaptFilter(QueryParameters.||(QueryParameters.==(a, hd), contains(a, tl)))
      case a in list => Filters.in(a.path, list: _*)
      case a notIn list => Filters.nin(a.path, list: _*)
      case a && b => Filters.and(adaptFilter(a), adaptFilter(b))
      case a || b => Filters.or(adaptFilter(a), adaptFilter(b))
    }
  }

  private def adaptSorting(sorting: Sorting): Bson = {
    sorting match {
      case ascending(paths) => Sorts.ascending(paths.map(_.path): _*)
      case descending(paths) => Sorts.descending(paths.map(_.path): _*)
      case orderBy(sorting) => Sorts.orderBy(sorting.map(adaptSorting): _*)
    }
  }
}
