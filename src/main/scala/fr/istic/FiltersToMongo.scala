package fr.istic

import fr.istic.Filter._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters

object FiltersToMongo {

  def toMongo(filter: Filter): Bson = {
    filter match {
      case isNull(a, true) => Filters.not(Filters.exists(a.path))
      case isNull(a, false) => Filters.exists(a.path)
      case a == b => Filters.eq(a.path, b)
      case a != b => Filters.ne(a.path, b)
      case a < b => Filters.lt(a.path, b)
      case a <= b => Filters.lte(a.path, b)
      case a > b => Filters.gt(a.path, b)
      case a >= b => Filters.gte(a.path, b)
      case a contains Nil => Filters.exists(a.path)
      case a contains hd :: Nil => toMongo(Filter.==(a, hd))
      case a contains hd :: tl => toMongo(Filter.||(Filter.==(a, hd), Filter.contains(a, tl)))
      case a in list => Filters.in(a.path, list:_*)
      case a notIn list => Filters.nin(a.path, list:_*)
      case a && b => Filters.and(toMongo(a), toMongo(b))
      case a || b => Filters.or(toMongo(a), toMongo(b))
    }
  }


}
