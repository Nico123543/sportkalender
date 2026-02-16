package de.sportkalender.nativeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.sportkalender.nativeapp.model.LeagueOption
import de.sportkalender.nativeapp.model.SeasonOption
import de.sportkalender.nativeapp.model.TvChannel
import de.sportkalender.nativeapp.ui.theme.SportkalenderTheme

private sealed interface ScheduleRow {
    val key: String
}

private data class DayHeaderRow(
    override val key: String,
    val dayLabel: String,
    val dateLabel: String,
    val isToday: Boolean
) : ScheduleRow

private data class MatchRow(
    override val key: String,
    val isToday: Boolean,
    val item: MatchItemUi
) : ScheduleRow

private data class EmptyDayRow(
    override val key: String,
    val isToday: Boolean
) : ScheduleRow

private fun buildScheduleRows(days: List<DayBlockUi>): List<ScheduleRow> {
    val rows = ArrayList<ScheduleRow>(days.size * 4)
    days.forEach { day ->
        rows += DayHeaderRow(
            key = "header-${day.dateLabel}",
            dayLabel = day.dayLabel,
            dateLabel = day.dateLabel,
            isToday = day.isToday
        )
        if (day.matches.isEmpty()) {
            rows += EmptyDayRow(
                key = "empty-${day.dateLabel}",
                isToday = day.isToday
            )
        } else {
            day.matches.forEachIndexed { idx, match ->
                rows += MatchRow(
                    key = "match-${day.dateLabel}-$idx-${match.home}-${match.away}-${match.kickoff}",
                    isToday = day.isToday,
                    item = match
                )
            }
        }
    }
    return rows
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportkalenderTheme {
                SportkalenderApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SportkalenderApp(viewModel: SportkalenderViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scheduleRows = remember(state.dayBlocks) { buildScheduleRows(state.dayBlocks) }
    val weekMatchCount = remember(state.dayBlocks) { state.dayBlocks.sumOf { it.matches.size } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Sportkalender", style = MaterialTheme.typography.titleLarge)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterSection(
                    selectedLeague = state.selectedLeague,
                    selectedSeason = state.selectedSeason,
                    onLeagueChange = viewModel::onLeagueChanged,
                    onSeasonChange = viewModel::onSeasonChanged
                )
            }
            item {
                WeekControls(
                    weekLabel = state.weekLabel,
                    onPrev = viewModel::previousWeek,
                    onToday = viewModel::goToCurrentWeek,
                    onNext = viewModel::nextWeek
                )
            }
            item {
                WeekSummaryCard(
                    league = state.selectedLeague.label,
                    season = state.selectedSeason.label,
                    matchCount = weekMatchCount
                )
            }
            if (state.isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Lade Spiele ...")
                    }
                }
            }
            state.error?.let { error ->
                item {
                    Text(
                        text = "Fehler: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(
                items = scheduleRows,
                key = { it.key },
                contentType = {
                    when (it) {
                        is DayHeaderRow -> "dayHeader"
                        is MatchRow -> "match"
                        is EmptyDayRow -> "empty"
                    }
                }
            ) { row ->
                when (row) {
                    is DayHeaderRow -> DayHeaderCard(row)
                    is MatchRow -> MatchCard(item = row.item, isToday = row.isToday)
                    is EmptyDayRow -> EmptyDayCard(isToday = row.isToday)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    selectedLeague: LeagueOption,
    selectedSeason: SeasonOption,
    onLeagueChange: (LeagueOption) -> Unit,
    onSeasonChange: (SeasonOption) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.45f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Filter",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LeagueDropdown(
                    modifier = Modifier.weight(1f),
                    selected = selectedLeague,
                    onSelect = onLeagueChange
                )
                SeasonDropdown(
                    modifier = Modifier.weight(1f),
                    selected = selectedSeason,
                    onSelect = onSeasonChange
                )
            }
        }
    }
}

@Composable
private fun WeekControls(
    weekLabel: String,
    onPrev: () -> Unit,
    onToday: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.52f)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Woche: $weekLabel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPrev) { Text("Vorwoche") }
                FilledTonalButton(onClick = onToday) { Text("Heute") }
                OutlinedButton(onClick = onNext) { Text("Nächste") }
            }
        }
    }
}

@Composable
private fun WeekSummaryCard(
    league: String,
    season: String,
    matchCount: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.34f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$matchCount Spiele in dieser Woche",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$league · Saison $season",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayHeaderCard(row: DayHeaderRow) {
    Surface(
        color = if (row.isToday) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (row.isToday) 2.dp else 0.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(row.dayLabel.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold)
                Text(row.dateLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun EmptyDayCard(isToday: Boolean) {
    Surface(
        color = if (isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 0.dp
    ) {
        Text(
            text = "Keine Spiele",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun MatchCard(item: MatchItemUi, isToday: Boolean) {
    Surface(
        color = if (isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 0.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${item.home} vs ${item.away}", fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        item.kickoff,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            Text(item.score, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (item.tvChannels.isEmpty()) {
                    Text("Keine TV-Daten", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    item.tvChannels.forEach { channel ->
                        TvChip(channel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TvChip(channel: TvChannel) {
    val bg = if (channel.isFree) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val fg = if (channel.isFree) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 0.dp
    ) {
        Text(
            text = channel.name,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LeagueDropdown(
    modifier: Modifier = Modifier,
    selected: LeagueOption,
    onSelect: (LeagueOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        FilledTonalButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Liga", maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(6.dp))
            Text(selected.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LEAGUES.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun SeasonDropdown(
    modifier: Modifier = Modifier,
    selected: SeasonOption,
    onSelect: (SeasonOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        FilledTonalButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Saison", maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(6.dp))
            Text(selected.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SEASONS.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}
