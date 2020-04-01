package com.twitter.challenge

import com.twitter.challenge.TemperatureConverter.celsiusToFahrenheit
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.assertj.core.api.Java6Assertions
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class TemperatureConverterTests {
    @Test
    fun testCelsiusToFahrenheitConversion() {
        val precision = within(0.01f)
        assertThat((-50f).celsiusToFahrenheit()).isEqualTo(-58f, precision)
        assertThat(0f.celsiusToFahrenheit()).isEqualTo(32f, precision)
        assertThat(10f.celsiusToFahrenheit()).isEqualTo(50f, precision)
        assertThat(21.11f.celsiusToFahrenheit()).isEqualTo(70f, precision)
        assertThat(37.78f.celsiusToFahrenheit()).isEqualTo(100f, precision)
        assertThat(100f.celsiusToFahrenheit()).isEqualTo(212f, precision)
        assertThat(1000f.celsiusToFahrenheit()).isEqualTo(1832f, precision)
    }
}