import Foundation

private struct Rule {
    let weekday: Int
    let hour: Int
    let minute: Int
    let channels: [TvChannel]
}

private let rules: [String: [Rule]] = [
    "bl1": [
        Rule(weekday: 6, hour: 20, minute: 30, channels: [TvChannel(name: "Sky", isFree: false)]),
        Rule(weekday: 7, hour: 15, minute: 30, channels: [TvChannel(name: "Sky", isFree: false), TvChannel(name: "DAZN Konf.", isFree: false)]),
        Rule(weekday: 7, hour: 18, minute: 30, channels: [TvChannel(name: "Sky", isFree: false)]),
        Rule(weekday: 1, hour: 15, minute: 30, channels: [TvChannel(name: "DAZN", isFree: false)]),
        Rule(weekday: 1, hour: 17, minute: 30, channels: [TvChannel(name: "DAZN", isFree: false)]),
        Rule(weekday: 1, hour: 19, minute: 30, channels: [TvChannel(name: "DAZN", isFree: false)])
    ],
    "bl2": [
        Rule(weekday: 6, hour: 18, minute: 30, channels: [TvChannel(name: "Sky", isFree: false)]),
        Rule(weekday: 7, hour: 13, minute: 0, channels: [TvChannel(name: "Sky", isFree: false)]),
        Rule(weekday: 7, hour: 20, minute: 30, channels: [TvChannel(name: "Sky", isFree: false), TvChannel(name: "RTL", isFree: true)]),
        Rule(weekday: 1, hour: 13, minute: 30, channels: [TvChannel(name: "Sky", isFree: false)])
    ],
    "dfb": [
        Rule(weekday: -1, hour: -1, minute: -1, channels: [TvChannel(name: "Sky", isFree: false), TvChannel(name: "ARD/ZDF", isFree: true)])
    ],
    "ucl": [
        Rule(weekday: -1, hour: -1, minute: -1, channels: [TvChannel(name: "DAZN", isFree: false), TvChannel(name: "Prime", isFree: false), TvChannel(name: "ZDF", isFree: true)])
    ]
]

func resolveTvChannels(league: String, kickoff: Date?) -> [TvChannel] {
    guard let kickoff, let leagueRules = rules[league] else { return [] }
    let cal = Calendar(identifier: .gregorian)
    let comps = cal.dateComponents(in: TimeZone(identifier: "Europe/Berlin")!, from: kickoff)
    let weekday = comps.weekday ?? -1
    let hour = comps.hour ?? -1
    let minute = comps.minute ?? -1

    if let exact = leagueRules.first(where: { $0.weekday == weekday && $0.hour == hour && $0.minute == minute }) {
        return exact.channels
    }
    return leagueRules.first(where: { $0.weekday == -1 })?.channels ?? []
}
