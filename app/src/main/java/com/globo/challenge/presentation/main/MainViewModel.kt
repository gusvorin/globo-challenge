package com.globo.challenge.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.globo.challenge.BaseApplication
import com.globo.challenge.ext.addTo
import com.globo.challenge.presentation.BaseViewModel
import com.globo.domain.Result
import com.globo.domain.model.Movie
import com.globo.domain.usecase.favorites.DeleteFavoriteUseCase
import com.globo.domain.usecase.favorites.GetFavoritesUseCase
import com.globo.domain.usecase.movies.GetMoviesUseCase
import com.globo.domain.usecase.favorites.InsertFavoriteUseCase
import com.globo.domain.usecase.session.ClearSessionUseCase
import com.globo.domain.usecase.session.GetSavedUserUseCase
import com.globo.domain.usecase.user.ChangePasswordUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val router : MainRouter,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val insertFavoriteUseCase: InsertFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val getSavedUserUseCase: GetSavedUserUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    application: BaseApplication
) : BaseViewModel(application) {

    private val movies = MutableLiveData<List<Movie>>().apply { value = emptyList() }
    fun getMovies() : LiveData<List<Movie>> = movies

    private val favorites = MutableLiveData<List<Movie>>().apply { value = emptyList() }
    fun getFavorites() : LiveData<List<Movie>> = favorites

    private val currentUser = MutableLiveData<String>().apply { value = null }
    fun getCurrentUser() : LiveData<String> = currentUser

    init {
        currentUser.postValue(getSavedUserUseCase.execute())
    }

    fun onChangePasswordCalled(newPassword : String) {
        GlobalScope.launch {
            changePasswordUseCase.execute(newPassword)
        }
    }

    fun onLogoutClicked() {
        clearSessionUseCase.execute()
        router.navigate(MainRouter.Route.LOGIN)
    }

    fun boundMovies() {
        getAllMovies()
    }

    fun boundFavorites() {
        GlobalScope.launch {
            getDbFavorites()
        }
    }

    private suspend fun getDbFavorites() {
        val favoritesDb = getFavoritesUseCase.execute()
        favorites.postValue(favoritesDb)

    }

    private fun getAllMovies() {
        getMoviesUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { handleMoviesResult(it) }
            .addTo(disposables)
    }

    private fun handleMoviesResult(result : Result.MoviesResult) {
        when(result) {
            is Result.MoviesResult.Success -> {
                GlobalScope.launch {
                    getFavoriteMovies(result.movies)
                }
            }
            is Result.MoviesResult.Failure -> hideDialog()
            is Result.MoviesResult.Loading -> showDialog()
        }
    }

    suspend fun setAsFavorite(movie : Movie) {
        insertFavoriteUseCase.execute(movie)
    }

    suspend fun deleteFavorite(movie : Movie) {
        deleteFavoriteUseCase.execute(movie)
    }

    private suspend fun getFavoriteMovies(apiMovies : List<Movie>) {
        val favorites = getFavoritesUseCase.execute()

        favorites.forEach { favorite ->
            apiMovies.find { favorite.title == it.title }?.let{
                it.isFavorite = true
                it.user = favorite.user
                it.id = favorite.id
            }
        }

        movies.postValue(apiMovies)
        hideDialog()
    }

}