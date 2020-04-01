package com.twitter.challenge

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
{
"coord": {
"lon": -122.42,
"lat": 37.77
},
"weather": {
"temp": 14.77,
"pressure": 1007,
"humidity": 85
},
"wind": {
"speed": 0.51,
"deg": 284
},
"rain": {
"3h": 1
},
"clouds": {
"cloudiness": 65
},
"name": "San Francisco"
}
 */
@Parcelize
data class Forecast(
        val name: String,
        val clouds: CloudInfo,
        val wind: WindInfo,
        val rain: RainInfo,
        val weather: WeatherInfo,
        val coord: GpsInfo
) : Parcelable

@Parcelize
data class CloudInfo(val cloudiness: Int) : Parcelable

@Parcelize
data class GpsInfo(val lon: Float, val lat: Float) : Parcelable

@Parcelize
data class WindInfo(val speed: Float, val deg: Int) : Parcelable

@Parcelize
data class WeatherInfo(val temp: Float, val pressure: Int, val humidity: Int) : Parcelable

@Parcelize
data class RainInfo(@SerializedName("3h") val next3Hours: Int) : Parcelable

fun standardDeviation(weather: List<WeatherInfo>): Float =
        standardDeviationInternal(weather.map { it.temp })

fun standardDeviationInternal(temps: List<Float>): Float {
    if(temps.isEmpty()) return 0f

    val count = temps.size
    val average = temps.sum() / count

    val cumulativeDeviationFromMean = temps.map { (it - average).pow(2) }.sum()

    return sqrt(abs(cumulativeDeviationFromMean) / count)
}