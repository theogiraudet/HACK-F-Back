package fr.istic

import fr.istic.Filter._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters

object FiltersToMongo {

  def toMongo(filter: Filter): Option[Bson] = {
    filter match {
      case isNull(a, true) => Some(Filters.not(Filters.exists(a.path)))
      case isNull(a, false) => Some(Filters.exists(a.path))
      case a == b => Some(Filters.eq(a.path, b))
      case a != b => Some(Filters.ne(a.path, b))
      case a < b => Some(Filters.lt(a.path, b))
      case a <= b => Some(Filters.lte(a.path, b))
      case a > b => Some(Filters.gt(a.path, b))
      case a >= b => Some(Filters.gte(a.path, b))
      case a contains Nil => Some(Filters.exists(a.path))
      case a contains hd :: Nil => toMongo(Filter.==(a, hd))
      case a contains hd :: tl => toMongo(Filter.||(Filter.==(a, hd), Filter.contains(a, tl)))
      case a in list => Some(Filters.in(a.path, list:_*))
      case a notIn list => Some(Filters.nin(a.path, list:_*))
      case a && b => toMongo(a).zip(toMongo(b)).map(Filters.and(_:_*))
      case a || b => toMongo(a).zip(toMongo(b)).map(Filters.or(_:_*))
    }
  }


}
