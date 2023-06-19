package com.zero.androidgrpc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.grpc.common.Genre
import com.me.grpc.movie.MovieSearchRequest
import com.me.grpc.movie.MovieServiceGrpc
import com.me.grpc.user.UserSearchRequest
import com.me.grpc.user.UserServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val address = "192.168.1.131"

class MainViewModel : ViewModel() {

    private var userChannel: ManagedChannel? = null
    private var movieChannel: ManagedChannel? = null

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result


    init {
        userChannel = ManagedChannelBuilder.forAddress(address, 6565).usePlaintext().build()
        movieChannel =
            ManagedChannelBuilder.forAddress(address, 7575).usePlaintext().build()
    }

    fun get(loginName: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val blockingStub = UserServiceGrpc.newBlockingStub(userChannel)
            try {
                val request = UserSearchRequest.newBuilder().setLoginId(loginName).build()
                val userGenre = blockingStub.getUserGenre(request)
                val res = userGenre.loginId.plus(", ").plus(userGenre.name)
                updateResult(res)
            } catch (e: Exception) {
                updateResult(e.message.toString())
            }
        }
    }

    private fun updateResult(res: String) {
        _result.postValue(res)
    }

    fun update(genre: String) {
        viewModelScope.launch(context = Dispatchers.Main) {
            try {
                val blockingStub = MovieServiceGrpc.newBlockingStub(movieChannel)
                val request =
                    MovieSearchRequest.newBuilder().setGenre(Genre.valueOf(genre.uppercase()))
                        .build()
                val response = blockingStub.getMovies(request)
                val res = response.moviesList.toString()
                updateResult(res)
            } catch (e: Exception) {
                updateResult(e.message.toString())
            }
        }
    }

}