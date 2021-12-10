package fr.istic.mongo

import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonValue}

import scala.jdk.CollectionConverters.CollectionHasAsScala

object Tree {

  sealed trait Tree

  case class Node(name: String, children: List[Tree]) extends Tree

  case class Leaf(name: String) extends Tree

  def getName(tree: Tree): String = {
    tree match {
      case Leaf(name) => name
      case Node(name, _) => name
    }
  }

  def merge(tree1: Tree, tree2: Tree): Tree = {
    (tree1, tree2) match {
      case (Node(str1, list), Node(str2, list2)) if str1 == str2 => Node(str1, (list ++ list2).groupBy(getName).map(x => merge(x._2)).toList)
      case _ => tree1
    }
  }

  def merge(list: List[Tree]): Tree = {
    list match {
      case hd :: Nil => hd
      case hd :: tl => (hd :: tl).reduce(merge)
    }
  }

  def getSchema(d: Document): Tree = Node("<root>", d.toList.map(getSchema))

  def getSchema(tuple: (String, BsonValue)): Tree = {
    tuple match {
      case (str, value: BsonDocument) => Node(str, bsonToList(value))
      case (str, value: BsonArray) => getSchema(str, value)
      case (str, _) => Leaf(str)
    }
  }

  def getSchema(str: String, value: BsonArray): Tree = {
    value.toArray.toList match {
      case (hd: BsonDocument) :: _ => Node(str, bsonToList(hd))
      case _ => Leaf(str)
    }
  }

  private def bsonToList(value: BsonDocument) = value.entrySet().asScala.map(e => (e.getKey, e.getValue)).map(getSchema).toList

  def isIncludedIn(tree1: Tree, tree2: Tree): Boolean = {
    (tree1, tree2) match {
      case (Leaf(a), Leaf(b)) => a == b
      case (Node(name, l1), Node(name2, l2)) => name == name2 && l1.isEmpty == l2.isEmpty && l1.forall(x => l2.exists(isIncludedIn(x, _)))
      case _ => false
    }
  }


  def toString(tree: Tree, indent: Int = 0): String = {
    tree match {
      case Leaf(str) => "\t".repeat(indent) + str + "\n"
      case Node(str, list) => "\t".repeat(indent) + str + "\n" + list.map(toString(_, indent + 1)).mkString
    }

  }

}
