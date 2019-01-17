package com.gurpreetsk.android_starter._storage.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class Error

@Parcelize class ApplicationError(
    val errorType: ErrorType,
    val code: Int? = null,
    val message: String? = null
) : Parcelable, Error()
