package fr.istic

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import fr.istic.mongo.Tree
import fr.istic.mongo.Tree.{Leaf, Node, Tree}

import scala.jdk.CollectionConverters.IteratorHasAsScala

object StructureChecker {

  def check(text: String): Boolean = {
    val mapper = new ObjectMapper
    val root = mapper.readTree(text)
    Tree.isIncludedIn(toTree("<root>", root), DataPath.tree.get)
  }

  private def toTree(name: String, obj: JsonNode): Tree = {
    if(obj.isObject) {
      val list = obj.asInstanceOf[ObjectNode].fields().asScala.map(entry => toTree(entry.getKey, entry.getValue)).toList
      Node(name, list)
    } else if(obj.isArray) {
      println(obj.asInstanceOf[ArrayNode].iterator().asScala
        .map(arrayToTree)
        .filter(_.isDefined)
        .map(_.get)
        .map(Node("<root>", _)).size)

      obj.asInstanceOf[ArrayNode].iterator().asScala
        .map(arrayToTree)
        .filter(_.isDefined)
        .map(_.get)
        .map(Node("<root>", _))
        .reduceLeft(Tree.merge)
    } else
      Leaf(name)
   }

  private def arrayToTree(obj: JsonNode): Option[List[Tree]] = {
    if (obj.isObject)
      Some(obj.asInstanceOf[ObjectNode].fields().asScala.map(entry => toTree(entry.getKey, entry.getValue)).toList)
    else
      None
  }

}
