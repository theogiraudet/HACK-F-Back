package fr.istic

import fr.istic.mongo.Tree._

import java.util.logging.Logger

case class DataPath private (path: String)

object DataPath {

  var tree: Option[Tree] = None
  private val logger = Logger.getLogger("DataPath")

  def init(tree: Option[Tree]): Unit = {
    logger.info("Initialize Tree's Datapath to " + tree)
    this.tree = tree
  }

  def of(str: String): Option[DataPath] = {
    if(tree.isEmpty)
       None
    else if(isValid("<root>" :: str.split("\\.").toList, tree.get))
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
