package com.gurpreetsk.android_starter._storage.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class User(
    @PrimaryKey(autoGenerate = true) val id: Long
)