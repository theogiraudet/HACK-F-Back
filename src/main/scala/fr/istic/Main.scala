package fr.istic

object Main extends App {

  val raw_filter = """or(contains(labelCode, "FRA"), eq(name, "Border Crossing"))"""
  Database.init()
  Database.read(raw_filter).map(_.foreach(println))


}
