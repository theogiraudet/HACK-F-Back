package fr.istic

import scala.util.Try

trait Database {

  def createReadQuery(): Query
  def write(json: Artist): Try[Unit]

}
