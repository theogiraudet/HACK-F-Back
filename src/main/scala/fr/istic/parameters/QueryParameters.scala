package fr.istic.parameters

import fr.istic.DataPath

object QueryParameters {

  sealed trait QueryParameter extends Ordered[QueryParameter] {
    override def compare(o: QueryParameter): Int = {
      o match {
        case _: Filter => 0
        case _: Sorting => 1
      }
    }
  }


  sealed trait Filter extends QueryParameter {
    def reduce(param1: Filter): Filter = &&(this, param1)
  }

  case class isNull(path: DataPath, isNull: Boolean) extends Filter
  case class ==(path: DataPath, value: Any) extends Filter
  case class !=(path: DataPath, value: Any) extends Filter
  case class <(path: DataPath, value: Any) extends Filter
  case class <=(path: DataPath, value: Any) extends Filter
  case class >(path: DataPath, value: Any) extends Filter
  case class >=(path: DataPath, value: Any) extends Filter
  case class contains(path: DataPath, value: List[Any]) extends Filter
  case class in(path: DataPath, value: List[Any]) extends Filter
  case class notIn(path: DataPath, value: List[Any]) extends Filter
  case class &&(filter1: Filter, Filter2: Filter) extends Filter
  case class ||(filter1: Filter, Filter2: Filter) extends Filter
  case object NoFilter extends Filter


  sealed trait Sorting extends QueryParameter {
    def reduce(param1: Sorting): Sorting = orderBy(this :: param1 :: Nil)
  }

  case class ascending(paths: List[DataPath]) extends Sorting
  case class descending(paths: List[DataPath]) extends Sorting
  case class orderBy(sorting: List[Sorting]) extends Sorting
  case object NoSorting extends Sorting

}
