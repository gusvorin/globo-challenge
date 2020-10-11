package com.globo.domain.repository

import com.globo.domain.model.User

interface UserRepository {

    suspend fun registerUser(user : User)

    suspend fun getUser(user : String, password : String) : User

    fun saveUser(user : User)

}