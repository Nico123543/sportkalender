import Foundation

struct OpenLigaMatch: Decodable {
    let matchDateTimeUTC: String?
    let matchDateTime: String?
    let team1: TeamRef?
    let team2: TeamRef?
    let matchResults: [MatchResult]?

    enum CodingKeys: String, CodingKey {
        case matchDateTimeUTC = "matchDateTimeUTC"
        case matchDateTimeUTCAlt = "MatchDateTimeUTC"
        case matchDateTime = "matchDateTime"
        case matchDateTimeAlt = "MatchDateTime"
        case team1 = "team1"
        case team1Alt = "Team1"
        case team2 = "team2"
        case team2Alt = "Team2"
        case matchResults = "matchResults"
        case matchResultsAlt = "MatchResults"
    }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        matchDateTimeUTC = try c.decodeIfPresent(String.self, forKey: .matchDateTimeUTC)
            ?? c.decodeIfPresent(String.self, forKey: .matchDateTimeUTCAlt)
        matchDateTime = try c.decodeIfPresent(String.self, forKey: .matchDateTime)
            ?? c.decodeIfPresent(String.self, forKey: .matchDateTimeAlt)
        team1 = try c.decodeIfPresent(TeamRef.self, forKey: .team1)
            ?? c.decodeIfPresent(TeamRef.self, forKey: .team1Alt)
        team2 = try c.decodeIfPresent(TeamRef.self, forKey: .team2)
            ?? c.decodeIfPresent(TeamRef.self, forKey: .team2Alt)
        matchResults = try c.decodeIfPresent([MatchResult].self, forKey: .matchResults)
            ?? c.decodeIfPresent([MatchResult].self, forKey: .matchResultsAlt)
    }

    func kickoffDate() -> Date? {
        if let utc = matchDateTimeUTC {
            let formatter = ISO8601DateFormatter()
            if let date = formatter.date(from: utc) { return date }
        }
        if let local = matchDateTime {
            let f = DateFormatter()
            f.locale = Locale(identifier: "en_US_POSIX")
            f.timeZone = TimeZone(identifier: "Europe/Berlin")
            f.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
            if let date = f.date(from: local) { return date }
        }
        return nil
    }
}

struct TeamRef: Decodable {
    let teamName: String?

    enum CodingKeys: String, CodingKey {
        case teamName = "teamName"
        case teamNameAlt = "TeamName"
    }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        teamName = try c.decodeIfPresent(String.self, forKey: .teamName)
            ?? c.decodeIfPresent(String.self, forKey: .teamNameAlt)
    }
}

struct MatchResult: Decodable {
    let resultTypeID: Int?
    let pointsTeam1: Int?
    let pointsTeam2: Int?

    enum CodingKeys: String, CodingKey {
        case resultTypeID = "resultTypeID"
        case resultTypeIDAlt = "ResultTypeID"
        case pointsTeam1 = "pointsTeam1"
        case pointsTeam1Alt = "PointsTeam1"
        case pointsTeam2 = "pointsTeam2"
        case pointsTeam2Alt = "PointsTeam2"
    }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)
        resultTypeID = try c.decodeIfPresent(Int.self, forKey: .resultTypeID)
            ?? c.decodeIfPresent(Int.self, forKey: .resultTypeIDAlt)
        pointsTeam1 = try c.decodeIfPresent(Int.self, forKey: .pointsTeam1)
            ?? c.decodeIfPresent(Int.self, forKey: .pointsTeam1Alt)
        pointsTeam2 = try c.decodeIfPresent(Int.self, forKey: .pointsTeam2)
            ?? c.decodeIfPresent(Int.self, forKey: .pointsTeam2Alt)
    }
}

struct LeagueOption: Identifiable, Hashable {
    let id: String
    let label: String
}

struct SeasonOption: Identifiable, Hashable {
    let id: String
    let label: String
}

struct TvChannel: Hashable {
    let name: String
    let isFree: Bool
}

struct MatchItem: Identifiable {
    let id: String
    let kickoff: String
    let home: String
    let away: String
    let score: String
    let tvChannels: [TvChannel]
}

struct DayBlock: Identifiable {
    let id: String
    let dayLabel: String
    let dateLabel: String
    let isToday: Bool
    let matches: [MatchItem]
}
