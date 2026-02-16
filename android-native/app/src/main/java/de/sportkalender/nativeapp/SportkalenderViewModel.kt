package de.sportkalender.nativeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.sportkalender.nativeapp.data.MatchRepository
import de.sportkalender.nativeapp.domain.resolveTvChannels
import de.sportkalender.nativeapp.model.LeagueOption
import de.sportkalender.nativeapp.model.OpenLigaMatch
import de.sportkalender.nativeapp.model.SeasonOption
import de.sportkalender.nativeapp.model.TvChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class MatchItemUi(
    val kickoff: String,
    val home: String,
    val away: String,
    val score: String,
    val tvChannels: List<TvChannel>
)

data class DayBlockUi(
    val dayLabel: String,
    val dateLabel: String,
    val isToday: Boolean,
    val matches: List<MatchItemUi>
)

data class UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLeague: LeagueOption = LEAGUES.first(),
    val selectedSeason: SeasonOption = SEASONS.first(),
    val weekOffset: Int = 0,
    val weekLabel: String = "",
    val dayBlocks: List<DayBlockUi> = emptyList()
)

val LEAGUES = listOf(
    LeagueOption("bl1", "Bundesliga 1"),
    LeagueOption("bl2", "Bundesliga 2"),
    LeagueOption("bl3", "Bundesliga 3"),
    LeagueOption("dfb", "DFB-Pokal"),
    LeagueOption("ucl", "Champions League")
)

val SEASONS = listOf(
    SeasonOption("2025", "2025/26"),
    SeasonOption("2024", "2024/25"),
    SeasonOption("2023", "2023/24")
)

class SportkalenderViewModel(
    private val repository: MatchRepository = MatchRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var allMatches: List<OpenLigaMatch> = emptyList()

    init {
        loadMatches()
    }

    fun onLeagueChanged(league: LeagueOption) {
        _uiState.value = _uiState.value.copy(selectedLeague = league, weekOffset = 0)
        loadMatches()
    }

    fun onSeasonChanged(season: SeasonOption) {
        _uiState.value = _uiState.value.copy(selectedSeason = season, weekOffset = 0)
        loadMatches()
    }

    fun previousWeek() {
        val newOffset = _uiState.value.weekOffset - 1
        _uiState.value = _uiState.value.copy(weekOffset = newOffset)
        recalcWeek()
    }

    fun nextWeek() {
        val newOffset = _uiState.value.weekOffset + 1
        _uiState.value = _uiState.value.copy(weekOffset = newOffset)
        recalcWeek()
    }

    fun goToCurrentWeek() {
        _uiState.value = _uiState.value.copy(weekOffset = 0)
        recalcWeek()
    }

    private fun loadMatches() {
        val league = _uiState.value.selectedLeague.id
        val season = _uiState.value.selectedSeason.id
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.loadMatches(league, season) }
                .onSuccess { matches ->
                    allMatches = matches
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    recalcWeek()
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unbekannter Fehler"
                    )
                }
        }
    }

    private fun recalcWeek() {
        val state = _uiState.value
        val monday = startOfCurrentWeek().plusDays((state.weekOffset * 7).toLong())
        val sunday = monday.plusDays(6)
        val dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)

        val blocks = (0L..6L).map { dayShift ->
            val date = monday.plusDays(dayShift)
            val dayMatches = allMatches
                .filter { match ->
                    val local = match.localKickoff()?.toLocalDate()
                    local == date
                }
                .sortedBy { it.localKickoff() }
                .map { match ->
                    val kickoff = match.localKickoff()
                    val score = finalScore(match) ?: "vs"
                    MatchItemUi(
                        kickoff = kickoff?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--",
                        home = match.team1?.teamName ?: "Unbekannt",
                        away = match.team2?.teamName ?: "Unbekannt",
                        score = score,
                        tvChannels = resolveTvChannels(state.selectedLeague.id, kickoff)
                    )
                }

            DayBlockUi(
                dayLabel = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.GERMAN),
                dateLabel = date.format(dateFmt),
                isToday = date == LocalDate.now(ZoneId.of("Europe/Berlin")),
                matches = dayMatches
            )
        }

        _uiState.value = state.copy(
            weekLabel = "${monday.format(dateFmt)} - ${sunday.format(dateFmt)}",
            dayBlocks = blocks
        )
    }

    private fun startOfCurrentWeek(): LocalDate {
        val today = LocalDate.now(ZoneId.of("Europe/Berlin"))
        return today.minusDays((today.dayOfWeek.value - 1).toLong())
    }

    private fun finalScore(match: OpenLigaMatch): String? {
        val final = match.matchResults
            ?.sortedByDescending { it.resultTypeId ?: 0 }
            ?.firstOrNull { it.pointsTeam1 != null && it.pointsTeam2 != null }
        return if (final != null) "${final.pointsTeam1}:${final.pointsTeam2}" else null
    }
}
