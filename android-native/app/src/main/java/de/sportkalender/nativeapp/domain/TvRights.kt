package de.sportkalender.nativeapp.domain

import de.sportkalender.nativeapp.model.TvChannel
import java.time.ZonedDateTime

private data class Rule(
    val day: Int,
    val hour: Int,
    val minute: Int,
    val channels: List<TvChannel>
)

private val rightsByLeague: Map<String, List<Rule>> = mapOf(
    "bl1" to listOf(
        Rule(5, 20, 30, listOf(TvChannel("Sky", false))),
        Rule(6, 15, 30, listOf(TvChannel("Sky", false), TvChannel("DAZN Konf.", false))),
        Rule(6, 18, 30, listOf(TvChannel("Sky", false))),
        Rule(0, 15, 30, listOf(TvChannel("DAZN", false))),
        Rule(0, 17, 30, listOf(TvChannel("DAZN", false))),
        Rule(0, 19, 30, listOf(TvChannel("DAZN", false))),
        Rule(2, 20, 30, listOf(TvChannel("Sky", false))),
        Rule(3, 20, 30, listOf(TvChannel("Sky", false)))
    ),
    "bl2" to listOf(
        Rule(5, 18, 30, listOf(TvChannel("Sky", false))),
        Rule(6, 13, 0, listOf(TvChannel("Sky", false))),
        Rule(6, 20, 30, listOf(TvChannel("Sky", false), TvChannel("RTL", true))),
        Rule(0, 13, 30, listOf(TvChannel("Sky", false)))
    ),
    "dfb" to listOf(
        Rule(-1, -1, -1, listOf(TvChannel("Sky", false), TvChannel("ARD/ZDF", true)))
    ),
    "ucl" to listOf(
        Rule(-1, -1, -1, listOf(TvChannel("DAZN", false), TvChannel("Prime", false), TvChannel("ZDF", true)))
    )
)

fun resolveTvChannels(league: String, kickoff: ZonedDateTime?): List<TvChannel> {
    if (kickoff == null) return emptyList()
    val rules = rightsByLeague[league] ?: return emptyList()
    val weekday = kickoff.dayOfWeek.value % 7
    val hour = kickoff.hour
    val minute = kickoff.minute

    val exact = rules.firstOrNull { it.day == weekday && it.hour == hour && it.minute == minute }
    if (exact != null) return exact.channels

    val wildcard = rules.firstOrNull { it.day == -1 }
    return wildcard?.channels.orEmpty()
}
