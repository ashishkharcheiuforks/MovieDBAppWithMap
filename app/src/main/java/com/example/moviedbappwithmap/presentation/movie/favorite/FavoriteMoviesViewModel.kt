package com.example.moviedbappwithmap.presentation.movie.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviedbappwithmap.base.BaseViewModel
import com.example.moviedbappwithmap.data.models.MovieData
import com.example.moviedbappwithmap.data.repository.MovieRepositoryImpl
import com.example.moviedbappwithmap.domain.repository.MovieRepository
import com.example.moviedbappwithmap.exceptions.NoConnectionException
import com.example.moviedbappwithmap.extensions.launchSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteMoviesViewModel : BaseViewModel(){

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    private val movieRepository: MovieRepository = MovieRepositoryImpl()

    override fun handleError(e: Throwable) {
        _liveData.value = State.HideLoading
        if (e is NoConnectionException) {
            _liveData.value = State.IntError(e.messageInt)
        } else {
            _liveData.value = State.Error(e.localizedMessage)
        }
    }


    fun loadFavMovies(accountId: Int?, sessionId: String?, page: Int = 1) {
        uiScope.launchSafe(::handleError) {
            if (page == 1) {
                _liveData.value = State.ShowLoading
            }

            val result = withContext(Dispatchers.IO) {
                val response =
                    accountId?.let { accountId ->
                        sessionId?.let{ sessionId ->
                            movieRepository.getFavoriteMovies(accountId, sessionId, page)
                        }
                    }
                val list = response?.results ?: emptyList()
                val totalPages = response?.totalPages ?: 0
                Pair(totalPages, list)
            }
            _liveData.postValue(
                State.Result(
                    totalPages = result.first,
                    list = result.second
            ))
            _liveData.value = State.HideLoading
        }
    }

    sealed class State {
        object ShowLoading: State()
        object HideLoading: State()
        data class Result(val totalPages: Int, val list: List<MovieData>): State()
        data class Error(val error: String?): State()
        data class IntError(val error: Int): State()
    }
}

