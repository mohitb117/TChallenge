We would like you to create an Android app to display the weather:

● Use the provided starter code to get started.
● Use the API to fetch the current weather.
● Display the following current conditions:
    ○ Temperature in Celsius and Fahrenheit
    ○ Wind speed.
    ○ A cloud icon if the cloudiness percentage is greater than 50%.

● Provide a button, that when tapped, fetches the weather for the next 5 days, and displays the
standard deviation of the temperature, whose formula is provided below:

      stddev =  ∑ √(x −x) /  √ n−1 

n = number of data points
x = average/mean of the data
x each of the values of the data i =
∑ is the summation operator which is defined as the addition of all the elements of the series.
In this case it’s the addition of (x ) for each temperature in the series

Because this is a brief exercise and not a full fledged app, the following is not required:
● The 5 days of weather, only the standard deviation of the 5 days is required.
● Persisting data across multiple app launches or refreshing the data while the app is running.
● We know making a great-looking UI takes a lot longer than 3 hours. Your UI should be
functional and responsive, but is not expected to be attractive. You will be judged on the quality
of your code, not the style of your UI.

Additional instructions:
X ● You must be able to handle configuration, orientation changes, and process death, by properly
implementing onSaveInstanceState without loading the weather data over the internet
again. The app must adapt correctly and not prevent rotation to landscape.

X ● The app should not crash under any reasonable circumstances, but robust error handling is not
necessary. Logging exceptions is sufficient.

X ● Consider what would happen if the user is on a slow or unavailable network.

X ● Feel free to use any public third-party libraries, and write in Java or Kotlin

X ● Write unit tests for the core math operations.

● Once finished, please zip up the folder and send it back to Twitter. (Don’t delete the .git
directory please)