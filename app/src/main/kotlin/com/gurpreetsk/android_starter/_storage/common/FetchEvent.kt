package com.gurpreetsk.android_starter._storage.common

data class FetchEvent<out T>(
    val fetchAction: FetchAction,
    val result: T?,
    val errors: List<ApplicationError> = emptyList()
)
