package com.globo.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class Movie (
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,

    val title : String,
    val subtitle : String,
    val duration : String,
    val synopsis : String,
    val thumbnail : String,
    var user : String = String(),
    var isFavorite : Boolean = false
)