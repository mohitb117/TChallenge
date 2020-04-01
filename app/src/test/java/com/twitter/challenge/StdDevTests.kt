package com.twitter.challenge

import com.twitter.challenge.TemperatureConverter.celsiusToFahrenheit

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class StdDevTests {
    @Test
    fun testStdDevCalc() {
        val precision = within(0.01f)

        val dataIdentical = listOf(100f, 100f, 100f, 100f, 100f, 100f)
        val dataNotIdentical = listOf(100f, 100f, 100f, 101f, 100f, 101f)

        // Identical don't deviate from mean.
        assertThat(standardDeviationInternal(dataIdentical)).isEqualTo(0f, precision)

        // Non-Identical deviate from mean.
        assertThat(standardDeviationInternal(dataNotIdentical)).isGreaterThan(0f)

        // Empty Data Set
        assertThat(standardDeviationInternal(emptyList())).isEqualTo(0f, precision)
    }
}