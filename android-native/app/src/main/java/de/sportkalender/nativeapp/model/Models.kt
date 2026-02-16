package de.sportkalender.nativeapp.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class OpenLigaMatch(
    @SerializedName(
        value = "matchDateTimeUTC",
        alternate = ["MatchDateTimeUTC"]
    )
    val matchDateTimeUtc: String?,
    @SerializedName(
        value = "matchDateTime",
        alternate = ["MatchDateTime"]
    )
    val matchDateTime: String?,
    @SerializedName(value = "team1", alternate = ["Team1"]) val team1: TeamRef?,
    @SerializedName(value = "team2", alternate = ["Team2"]) val team2: TeamRef?,
    @SerializedName(value = "matchResults", alternate = ["MatchResults"]) val matchResults: List<MatchResult>?
) {
    fun localKickoff(): ZonedDateTime? {
        // Preferred if timezone is included.
        val utcParsed = matchDateTimeUtc?.let { raw ->
            runCatching {
                OffsetDateTime.parse(raw).atZoneSameInstant(ZoneId.of("Europe/Berlin"))
            }.getOrNull()
        }
        if (utcParsed != null) return utcParsed

        // Fallback for local datetime strings without explicit offset.
        val localRaw = matchDateTime ?: return null
        return runCatching {
            LocalDateTime.parse(localRaw).atZone(ZoneId.of("Europe/Berlin"))
        }.getOrNull()
    }
}

data class TeamRef(
    @SerializedName(value = "teamName", alternate = ["TeamName"]) val teamName: String?
)

data class MatchResult(
    @SerializedName(value = "resultTypeID", alternate = ["ResultTypeID"]) val resultTypeId: Int?,
    @SerializedName(value = "pointsTeam1", alternate = ["PointsTeam1"]) val pointsTeam1: Int?,
    @SerializedName(value = "pointsTeam2", alternate = ["PointsTeam2"]) val pointsTeam2: Int?
)

data class LeagueOption(
    val id: String,
    val label: String
)

data class SeasonOption(
    val id: String,
    val label: String
)

data class TvChannel(
    val name: String,
    val isFree: Boolean
)
