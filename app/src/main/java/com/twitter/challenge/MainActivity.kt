package com.twitter.challenge

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope

import com.google.gson.Gson
import com.twitter.challenge.TemperatureConverter.celsiusToFahrenheit
import com.twitter.challenge.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.weather_details.view.*

import kotlinx.coroutines.*

import okhttp3.OkHttpClient
import okhttp3.Request

import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val template = "https://twitter-code-challenge.s3.amazonaws.com/%s.json"

    private val url = template.format("current")

    private val futureUrl: (futureDay: Int) -> String = { template.format("future_$it") }

    private val client by lazy {
        OkHttpClient
                .Builder()
                .callTimeout(2, TimeUnit.SECONDS)
                .build()
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Toast.makeText(this@MainActivity, "Could not fetch because:$throwable", Toast.LENGTH_LONG).show()
    }

    private val supervisorJob = SupervisorJob()

    private val ioScope = CoroutineScope(supervisorJob + Dispatchers.IO)

    private var job: Job? = null

    private val lastForecasts: MutableMap<Int, Forecast> = HashMap()

    @Suppress("BlockingMethodInNonBlockingContext")
    @Throws(IOException::class)
    @WorkerThread
    fun getResponse(url: String): Forecast? {
        val request = Request.Builder().url(url).build()

        return client
                .newCall(request)
                .execute()
                .use { response -> gson.fromJson(response.body?.string(), Forecast::class.java) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val retrieved = savedInstanceState
                ?.getParcelableArrayList<Forecast>(LAST_FORECAST)

        if (retrieved != null && retrieved.size > 0) {
            lastForecasts.clear()

            retrieved.forEachIndexed { index, forecast ->
                lastForecasts[index] = forecast
            }
        }


        if (lastForecasts.isNotEmpty()) { renderAll() }

        binding
                .fetchInfo
                .setOnClickListener {
                    lifecycleScope.launch(exceptionHandler) { onClick(lifecycleScope) }
                }

        setContentView(binding.root)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun onClick(scope: CoroutineScope) {
        Toast.makeText(this@MainActivity, "Loading Forecast...", Toast.LENGTH_SHORT).show()

        val deferredResult: MutableList<Deferred<Forecast?>> = mutableListOf()

        // Refetch the other forecast.
        val todaysForecast = scope.async(ioScope.coroutineContext) { getResponse(url) }

        deferredResult.add(todaysForecast)

        for (i in 1..5) {
            val forecast: Deferred<Forecast?> = scope.async(ioScope.coroutineContext) { getResponse(futureUrl(i)) }
            deferredResult.add(forecast)
        }

        deferredResult.forEachIndexed { index, deferred ->
            val forecast = deferred.await()
            if (forecast != null) {
                lastForecasts[index] = forecast
            }
        }

        renderAll()

        Toast.makeText(this@MainActivity, "Done Loading Forecast...", Toast.LENGTH_SHORT).show()
    }

    private fun renderAll() {
        if (lastForecasts.isNotEmpty()) {
            val bindings = with(binding) { listOf(today, dayOne, dayTwo, dayThree, dayFour, dayFive) }

            lastForecasts
                    .entries
                    .forEach { render(it.value, bindings[it.key]) }

            val stdDev: Float = standardDeviation(lastForecasts.entries.map { it.value.weather })

            binding.stdDev.text = getString(R.string.std_dev, stdDev, stdDev.celsiusToFahrenheit())
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun onResume() {
        super.onResume()
        job = lifecycleScope.launch(exceptionHandler) {
            if (lastForecasts.isEmpty()) {
                val forecast = withContext(ioScope.coroutineContext) { getResponse(url) }
                if (forecast != null) {
                    render(forecast, binding.today)

                    lastForecasts[0] = forecast
                } else {
                    exceptionHandler.handleException(ioScope.coroutineContext, Exception("API Result Incompatible."))
                }
            }
        }
    }

    private fun render(forecast: Forecast?, today: ConstraintLayout) {
        if (forecast != null) {
            val windSpeedText = today.windspeed
            val temperatureTextView = today.temperature
            val cloudinessIndicator = today.cloudy

            val temp = forecast.weather.temp

            temperatureTextView.text = getString(R.string.temperature, temp, temp.celsiusToFahrenheit())

            windSpeedText.text = getString(R.string.windspeed, forecast.wind.speed)

            val cloudiness = forecast.clouds.cloudiness

            cloudinessIndicator.visibility = if (cloudiness > 50) View.VISIBLE else View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val list = ArrayList(lastForecasts.entries.map { it.value }.toList())
        outState.putParcelableArrayList(LAST_FORECAST, list)
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }

    companion object {
        private val gson = Gson()
        const val LAST_FORECAST = "LAST_FORECAST"
    }
}