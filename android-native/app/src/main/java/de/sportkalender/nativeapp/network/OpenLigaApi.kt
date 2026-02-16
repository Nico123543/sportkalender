package de.sportkalender.nativeapp.network

import de.sportkalender.nativeapp.model.OpenLigaMatch
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenLigaApi {
    @GET("getmatchdata/{league}/{season}")
    suspend fun getMatches(
        @Path("league") league: String,
        @Path("season") season: String
    ): List<OpenLigaMatch>
}
