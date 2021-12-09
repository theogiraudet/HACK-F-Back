package fr.istic

object Filter {
  sealed trait Filter

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

  case object noFilter extends Filter
}