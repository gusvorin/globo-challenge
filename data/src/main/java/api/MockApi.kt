package api

import io.reactivex.Single
import response.MovieResponse
import javax.inject.Inject

class MockApi @Inject constructor(
    private val mockApi: MockEndpoint
) {
    fun getAllMovies() : Single<List<MovieResponse>> = mockApi.getAllMovies()
}