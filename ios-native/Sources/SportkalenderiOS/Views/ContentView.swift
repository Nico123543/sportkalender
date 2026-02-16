import SwiftUI

struct ContentView: View {
    @StateObject private var vm = SportkalenderViewModel()

    var body: some View {
        NavigationStack {
            List {
                Section("Filter") {
                    Picker("Liga", selection: Binding(
                        get: { vm.selectedLeague },
                        set: { vm.changeLeague($0) }
                    )) {
                        ForEach(leagues) { l in
                            Text(l.label).tag(l)
                        }
                    }

                    Picker("Saison", selection: Binding(
                        get: { vm.selectedSeason },
                        set: { vm.changeSeason($0) }
                    )) {
                        ForEach(seasons) { s in
                            Text(s.label).tag(s)
                        }
                    }
                }

                Section("Woche") {
                    Text(vm.weekLabel).font(.headline)
                    HStack {
                        Button("Vorwoche") { vm.previousWeek() }
                        Spacer()
                        Button("Heute") { vm.currentWeek() }
                        Spacer()
                        Button("Nächste") { vm.nextWeek() }
                    }
                    .buttonStyle(.bordered)
                }

                if vm.isLoading {
                    Section {
                        ProgressView("Lade Spiele …")
                    }
                }

                if let error = vm.error {
                    Section {
                        Text("Fehler: \(error)").foregroundStyle(.red)
                    }
                }

                ForEach(vm.dayBlocks) { day in
                    Section {
                        if day.matches.isEmpty {
                            Text("Keine Spiele").foregroundStyle(.secondary)
                        } else {
                            ForEach(day.matches) { match in
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack(alignment: .top) {
                                        Text("\(match.home) vs \(match.away)")
                                            .font(.body.weight(.semibold))
                                            .lineLimit(2)
                                        Spacer()
                                        Text(match.kickoff)
                                            .font(.caption.weight(.semibold))
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 4)
                                            .background(.thinMaterial, in: Capsule())
                                    }

                                    Text(match.score).font(.subheadline.weight(.bold))

                                    if match.tvChannels.isEmpty {
                                        Text("Keine TV-Daten")
                                            .font(.caption)
                                            .foregroundStyle(.secondary)
                                    } else {
                                        ScrollView(.horizontal, showsIndicators: false) {
                                            HStack(spacing: 8) {
                                                ForEach(match.tvChannels, id: \.self) { channel in
                                                    Text(channel.name)
                                                        .font(.caption)
                                                        .padding(.horizontal, 8)
                                                        .padding(.vertical, 4)
                                                        .background(channel.isFree ? Color.green.opacity(0.2) : Color.blue.opacity(0.2), in: Capsule())
                                                }
                                            }
                                        }
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    } header: {
                        HStack {
                            Text(day.dayLabel)
                            Spacer()
                            Text(day.dateLabel)
                        }
                    }
                }
            }
            .navigationTitle("Sportkalender")
            .listStyle(.insetGrouped)
            .task { vm.onAppear() }
        }
    }
}
