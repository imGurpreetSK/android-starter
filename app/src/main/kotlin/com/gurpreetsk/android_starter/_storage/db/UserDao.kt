package com.gurpreetsk.android_starter._storage.db

import androidx.room.Dao
import androidx.room.Insert

@Dao interface UserDao {
  @Insert fun insert(user: User)
}
