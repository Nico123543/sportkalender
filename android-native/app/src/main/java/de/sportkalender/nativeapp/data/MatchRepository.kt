package de.sportkalender.nativeapp.data

import de.sportkalender.nativeapp.model.OpenLigaMatch
import de.sportkalender.nativeapp.network.OpenLigaApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MatchRepository {
    private val api: OpenLigaApi = Retrofit.Builder()
        .baseUrl("https://api.openligadb.de/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenLigaApi::class.java)

    suspend fun loadMatches(league: String, season: String): List<OpenLigaMatch> {
        return api.getMatches(league, season)
    }
}
