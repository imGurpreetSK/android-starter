package com.gurpreetsk.android_starter

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CanaryTest {
    @Test
    fun `This will fail if the test environment setup isn't correct`() {
        assertThat(true).isTrue()
    }
}
