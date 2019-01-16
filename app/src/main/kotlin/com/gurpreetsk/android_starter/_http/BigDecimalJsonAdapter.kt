package com.gurpreetsk.android_starter._http

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.math.BigDecimal

class BigDecimalJsonAdapter : JsonAdapter<BigDecimal>() {
  override fun fromJson(reader: JsonReader): BigDecimal? =
      BigDecimal(reader.nextString())

  override fun toJson(writer: JsonWriter, value: BigDecimal?) {
    value?.let {
      writer.value(it.toString())
    }
  }
}
