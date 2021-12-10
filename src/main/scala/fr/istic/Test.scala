package fr.istic

import com.fasterxml.jackson.databind.ObjectMapper

object Test extends App {

  val foo = "{\"name\":\"Tartiflettedu56\",\"officialLanguage\":\"EN\",\"labelCode\":\"NOR\",\"homeCountries\":[\"Norvège\"],\"deezerUrl\":\"https://www.deezer.com/fr/album/2999681\",\"spotifyUrl\":\"https://open.spotify.com/album/18Zy53OB9BCG0GtoGkQXv0\",\"visits\":[{\"number\":26,\"year\":2004,\"shows\":[{\"date\":\"1101942000\",\"city\":\"Parc des Expositions - Hall 5\"}]}]}"

  val mapper = new ObjectMapper
  val root = mapper.readTree(foo)
  println(StructureChecker.check(foo))

}
