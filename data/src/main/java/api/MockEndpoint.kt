package api

import io.reactivex.Single
import response.MovieResponse
import retrofit2.http.GET

interface MockEndpoint {

    @GET("/movies")
    fun getAllMovies() : Single<List<MovieResponse>>

}