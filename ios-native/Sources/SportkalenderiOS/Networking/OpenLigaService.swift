import Foundation

final class OpenLigaService {
    func loadMatches(league: String, season: String) async throws -> [OpenLigaMatch] {
        let url = URL(string: "https://api.openligadb.de/getmatchdata/\(league)/\(season)")!
        let (data, _) = try await URLSession.shared.data(from: url)
        let decoder = JSONDecoder()
        return try decoder.decode([OpenLigaMatch].self, from: data)
    }
}
