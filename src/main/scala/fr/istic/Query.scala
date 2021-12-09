package fr.istic

import fr.istic.parameters.QueryParameters.QueryParameter

import scala.util.Try

trait Query {

  def apply(filter: QueryParameter): fr.istic.Query
  def send(): Try[String]
}
