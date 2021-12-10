package fr.istic

import fr.istic.parameters.QueryParameters.QueryParameter

import scala.util.{Failure, Success, Try}

object Utils {

  def tryToList(params: Try[_ <: QueryParameter]*): Try[List[QueryParameter]] = {
   params match {
     case Nil => Success(Nil)
     case Success(x) :: tl => tryToList(tl:_*).map(x +: _)
     case Failure(x) :: _ => Failure(x)
   }
  }

}
