package fr.istic

import java.sql.Timestamp

case class Artist(name: String,
                  homeCountries: List[String] ,
                  homeCities: List[String] ,
                  visits: List[VisitedEdition],
                  deezerUrl: String ,
                  spotifyUrl: String ,
                  officialLanguage: String ,
                  labelCode: String )

case class VisitedEdition(year: Int, number: Int, shows: List[Show])

case class Show(theater: String ,
                date: Timestamp,
                city: String,
                project: String )