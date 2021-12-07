package fr.istic

object Main extends App {

  val raw_filter = """or(contains(labelCode, "FRA"), eq(name, "Border Crossing"))"""
  Database.init("mongodb://root:root@localhost:27017/?authSource=admin")
  Database.read(raw_filter).map(_.foreach(println))


}
