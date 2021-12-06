package fr.istic

import fr.istic.Tree.{Leaf, Node, Tree}

case class DataPath private (path: String)

object DataPath {

  var tree: Tree = null

  def init(tree: Tree): Unit = this.tree = tree

  def of(str: String): Option[DataPath] = {
    if(isValid("<root>" :: str.split("\\.").toList, tree))
      Some(new DataPath(str))
    else
      None
  }

  private def isValid(nodes: List[String], tree: Tree): Boolean = {
    (nodes, tree) match {
      case (Nil, _) =>  false
      case (hd :: Nil, Leaf(str)) => hd == str
      case (_, Leaf(_)) => false
      case (hd :: Nil, Node(str, _)) => hd == str
      case (hd :: tl, Node(str, list)) => hd == str && list.exists(isValid(tl, _))
    }
  }

}
