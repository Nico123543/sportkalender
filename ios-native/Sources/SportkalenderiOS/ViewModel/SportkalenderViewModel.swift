import Foundation

@MainActor
final class SportkalenderViewModel: ObservableObject {
    @Published var selectedLeague: LeagueOption = leagues.first!
    @Published var selectedSeason: SeasonOption = seasons.first!
    @Published var weekOffset: Int = 0
    @Published var isLoading = false
    @Published var error: String?
    @Published var weekLabel = ""
    @Published var dayBlocks: [DayBlock] = []

    private let service = OpenLigaService()
    private var allMatches: [OpenLigaMatch] = []

    static let leagues: [LeagueOption] = [
        .init(id: "bl1", label: "Bundesliga 1"),
        .init(id: "bl2", label: "Bundesliga 2"),
        .init(id: "bl3", label: "Bundesliga 3"),
        .init(id: "dfb", label: "DFB-Pokal"),
        .init(id: "ucl", label: "Champions League")
    ]

    static let seasons: [SeasonOption] = [
        .init(id: "2025", label: "2025/26"),
        .init(id: "2024", label: "2024/25"),
        .init(id: "2023", label: "2023/24")
    ]

    func onAppear() {
        if dayBlocks.isEmpty { Task { await reload() } }
    }

    func reload() async {
        isLoading = true
        error = nil
        do {
            allMatches = try await service.loadMatches(league: selectedLeague.id, season: selectedSeason.id)
            isLoading = false
            recalcWeek()
        } catch {
            isLoading = false
            self.error = error.localizedDescription
        }
    }

    func changeLeague(_ league: LeagueOption) {
        selectedLeague = league
        weekOffset = 0
        Task { await reload() }
    }

    func changeSeason(_ season: SeasonOption) {
        selectedSeason = season
        weekOffset = 0
        Task { await reload() }
    }

    func previousWeek() {
        weekOffset -= 1
        recalcWeek()
    }

    func nextWeek() {
        weekOffset += 1
        recalcWeek()
    }

    func currentWeek() {
        weekOffset = 0
        recalcWeek()
    }

    private func recalcWeek() {
        let cal = Calendar(identifier: .gregorian)
        let tz = TimeZone(identifier: "Europe/Berlin")!
        var comps = cal.dateComponents(in: tz, from: Date())
        comps.hour = 0; comps.minute = 0; comps.second = 0
        let today = cal.date(from: comps) ?? Date()
        let weekday = cal.component(.weekday, from: today)
        let mondayOffset = weekday == 1 ? -6 : (2 - weekday)
        let monday = cal.date(byAdding: .day, value: mondayOffset + (weekOffset * 7), to: today) ?? today
        let sunday = cal.date(byAdding: .day, value: 6, to: monday) ?? monday

        let labelFmt = DateFormatter()
        labelFmt.timeZone = tz
        labelFmt.locale = Locale(identifier: "de_DE")
        labelFmt.dateFormat = "dd.MM.yyyy"

        weekLabel = "\(labelFmt.string(from: monday)) - \(labelFmt.string(from: sunday))"

        let dayFmt = DateFormatter()
        dayFmt.timeZone = tz
        dayFmt.locale = Locale(identifier: "de_DE")
        dayFmt.dateFormat = "EEEE"

        let timeFmt = DateFormatter()
        timeFmt.timeZone = tz
        timeFmt.locale = Locale(identifier: "de_DE")
        timeFmt.dateFormat = "HH:mm"

        dayBlocks = (0...6).compactMap { d in
            guard let date = cal.date(byAdding: .day, value: d, to: monday) else { return nil }
            let dateLabel = labelFmt.string(from: date)
            let matches = allMatches
                .filter { m in
                    guard let kickoff = m.kickoffDate() else { return false }
                    return cal.isDate(kickoff, inSameDayAs: date)
                }
                .sorted { ($0.kickoffDate() ?? .distantFuture) < ($1.kickoffDate() ?? .distantFuture) }
                .map { m -> MatchItem in
                    let kickoff = m.kickoffDate()
                    let score: String = finalScore(m) ?? "vs"
                    let id = "\(dateLabel)-\(m.team1?.teamName ?? "x")-\(m.team2?.teamName ?? "y")-\(timeFmt.string(from: kickoff ?? date))"
                    return MatchItem(
                        id: id,
                        kickoff: kickoff.map { timeFmt.string(from: $0) } ?? "--:--",
                        home: m.team1?.teamName ?? "Unbekannt",
                        away: m.team2?.teamName ?? "Unbekannt",
                        score: score,
                        tvChannels: resolveTvChannels(league: selectedLeague.id, kickoff: kickoff)
                    )
                }

            return DayBlock(
                id: dateLabel,
                dayLabel: dayFmt.string(from: date).capitalized,
                dateLabel: dateLabel,
                isToday: cal.isDate(date, inSameDayAs: today),
                matches: matches
            )
        }
    }

    private func finalScore(_ match: OpenLigaMatch) -> String? {
        let final = match.matchResults?
            .sorted(by: { ($0.resultTypeID ?? 0) > ($1.resultTypeID ?? 0) })
            .first(where: { $0.pointsTeam1 != nil && $0.pointsTeam2 != nil })

        if let f = final, let a = f.pointsTeam1, let b = f.pointsTeam2 {
            return "\(a):\(b)"
        }
        return nil
    }
}

let leagues = SportkalenderViewModel.leagues
let seasons = SportkalenderViewModel.seasons
