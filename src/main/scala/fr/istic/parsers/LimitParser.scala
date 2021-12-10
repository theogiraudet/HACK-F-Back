package fr.istic.parsers

import fr.istic.parameters.QueryParameters.{Limit, LimitQuery, NoLimit}

import scala.util.{Success, Try}

object LimitParser {

  def parseLimit(limit: Int): Try[Limit] = Success(if(limit < 0) NoLimit else LimitQuery(limit))

}
