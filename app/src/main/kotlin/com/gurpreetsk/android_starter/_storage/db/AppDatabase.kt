package com.gurpreetsk.android_starter._storage.db

import com.gurpreetsk.android_starter.Database

interface AppDatabase

class SqlDatabase(val database: Database) : AppDatabase
