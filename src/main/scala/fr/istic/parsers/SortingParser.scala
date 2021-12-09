package fr.istic.parsers

import fr.istic.DataPath
import fr.istic.parameters.QueryParameters
import fr.istic.parameters.QueryParameters.Sorting

import scala.util.Try
import scala.util.parsing.combinator.RegexParsers

object SortingParser extends RegexParsers {

  def sorting = ascending | descending | orderBy

  def ascending: Parser[Sorting] = "ascending" ~> "(" ~> listPath <~ ")" ^^ { list => QueryParameters.ascending(list) }

  def descending: Parser[Sorting] = "descending" ~> "(" ~> listPath <~ ")" ^^ { list => QueryParameters.descending(list) }

  def orderBy: Parser[Sorting] = "order_by" ~> "(" ~> listSorting <~ ")" ^^ { list => QueryParameters.orderBy(list) }

  def listSorting: Parser[List[Sorting]] = sorting ~ ("," ~> sorting).* ^^ { case x ~ y => x :: y } | sorting ^^ { x => List(x) }

  def listPath: Parser[List[DataPath]] = path ~ ("," ~> path).* ^^ { case x ~ y => x :: y } | path ^^ { x => List(x) }

  private def path: Parser[DataPath] =
    """[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)*""".r >> {
      case path if DataPath.of(path).nonEmpty => success(DataPath.of(path).get)
      case path => failure(s"'$path' is not a valid path.")
    }

  def analyserSorting(s: String): Try[Sorting] = {
    if (s == "")
      scala.util.Success(QueryParameters.NoSorting)
    else
      SortingParser.parseAll(sorting, s) match {
        case Success(result, _) => scala.util.Success(result)
        case Failure(msg, _) => scala.util.Failure(new Exception("FAILURE: " + msg))
        case Error(msg, _) => scala.util.Failure(new Exception("ERROR: " + msg))
      }
  }


}
