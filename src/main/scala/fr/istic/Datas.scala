package fr.istic

import com.fasterxml.jackson.annotation.JsonProperty

import java.sql.Timestamp

case class Artist(@JsonProperty("name") name: String,
                  @JsonProperty("homeCountries") homeCountries: java.util.List[String],
                  @JsonProperty("homeCities") homeCities: java.util.List[String],
                  @JsonProperty("visits") visits: java.util.List[VisitedEdition],
                  @JsonProperty("deezerUrl") deezerUrl: String,
                  @JsonProperty("spotifyUrl") spotifyUrl: String,
                  @JsonProperty("officialLanguage") officialLanguage: String,
                  @JsonProperty("labelCode") labelCode: String)

case class VisitedEdition(@JsonProperty("year") year: Int, @JsonProperty("number") number: Int, @JsonProperty("shows") shows: java.util.List[Show])

case class Show(@JsonProperty("theater") theater: String,
                @JsonProperty("date") date: Timestamp,
                @JsonProperty("city") city: String,
                @JsonProperty("project") project: String)
