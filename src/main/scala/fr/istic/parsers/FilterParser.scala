package fr.istic.parsers

import fr.istic.DataPath
import fr.istic.parameters.QueryParameters
import fr.istic.parameters.QueryParameters.Filter

import scala.util.Try
import scala.util.parsing.combinator.RegexParsers

object FilterParser extends RegexParsers {

  def operator: Parser[Filter] = and | or | isNull | eq | neq | lt | lte | gt | gte | contains | in | nin

  def and: Parser[Filter] = "and" ~> "(" ~> operator ~ "," ~ operator <~ ")" ^^ { case op1 ~ _ ~ op2 => QueryParameters.&&(op1, op2) }

  def or: Parser[Filter] = "or" ~> "(" ~> operator ~ "," ~ operator <~ ")" ^^ { case op1 ~ _ ~ op2 => QueryParameters.||(op1, op2) }

  def isNull: Parser[Filter] = "is_null" ~> "(" ~> path ~ "," ~ boolean <~ ")" ^^ { case path ~ _ ~ bool => QueryParameters.isNull(path, bool) }

  def eq: Parser[Filter] = "eq" ~> "(" ~> path ~ "," ~ value <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.==(path, value) }

  def neq: Parser[Filter] = "neq" ~> "(" ~> path ~ "," ~ value <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.!=(path, value) }

  def lt: Parser[Filter] = "lt" ~> "(" ~> path ~ "," ~ comparable <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.<(path, value) }

  def lte: Parser[Filter] = "lte" ~> "(" ~> path ~ "," ~ comparable <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.<=(path, value) }

  def gt: Parser[Filter] = "gt" ~> "(" ~> path ~ "," ~ comparable <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.>(path, value) }

  def gte: Parser[Filter] = "gte" ~> "(" ~> path ~ "," ~ comparable <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.>=(path, value) }

  def contains: Parser[Filter] = "contains" ~> "(" ~> path ~ "," ~ list <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.contains(path, value) }

  def in: Parser[Filter] = "in" ~> "(" ~> path ~ "," ~ list <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.in(path, value) }

  def nin: Parser[Filter] = "nin" ~> "(" ~> path ~ "," ~ list <~ ")" ^^ { case path ~ _ ~ value => QueryParameters.notIn(path, value) }

  def list: Parser[List[Any]] = "[" ~> value.* <~ "]" ^^ { x => x } | value ^^ { x => List(x) }

  private def path: Parser[DataPath] =
    """[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)*""".r >> {
      case path if DataPath.of(path).nonEmpty => success(DataPath.of(path).get)
      case path => failure(s"'$path' is not a valid path.")
    }

  private def value = integer | boolean | string

  private def comparable = integer | string

  private def boolean: Parser[Boolean] = "true" ^^ (_ => true) | "false" ^^ (_ => false)

  private def string: Parser[String] = "\"" ~> """(?:[^"\\]|\\.)*""".r <~ "\"" ^^ { x => x }

  private def integer: Parser[Int] = """\d+""".r ^^ {
    _.toInt
  }

  def analyserFilter(s: String): Try[Filter] = {
    if (s == "")
      scala.util.Success(QueryParameters.NoFilter)
    else
      FilterParser.parseAll(operator, s) match {
        case Success(result, _) => scala.util.Success(result)
        case Failure(msg, _) => scala.util.Failure(new Exception("FAILURE: " + msg))
        case Error(msg, _) => scala.util.Failure(new Exception("ERROR: " + msg))
      }
  }


}
