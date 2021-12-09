package fr.istic

object Sorting {

  sealed trait Sorting

  case class ascending(paths: List[DataPath])
  case class descending(paths: List[DataPath])
  case class orderBy(sorting: List[Sorting])

}
