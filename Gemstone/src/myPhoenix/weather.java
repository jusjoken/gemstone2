/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.util.Date;
import java.util.List;

import myPhoenix.WeatherAPI;
import myPhoenix.IWeatherData;

/**
 * API Generated: Fri Nov 25 17:41:22 EST 2011<br/>
 * API Source: {@link IWeatherData}<br/>

 * Interface to access the Weather Date for either the current weather
 * conditions or for the forecasted weather. Not all fields will have data,
 * depending on type of data being populated (ie, current or forecast)
 * 
 * @author sls
 
 * API Source: {@link WeatherAPI}<br/>

 * WeatherAPI provides access to weather information, including current forecast and 
 * long range forecasts.
 *  
 * @author seans
 
  */
public final class weather {
   /**

	 * Returns the {@link Date} for the given weather condition
	 * 
	 * @return {@link Date}
	 
    */
   public static Date GetDate(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getDate();
   }

   /**

	 * Returns the formatted Low Temperature for the forecasted weather.
	 * 
	 * @return low temp
	 
    */
   public static String GetLow(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getLow();
   }

   /**

	 * Returns the formatted High Temperature for the forecasted weather.
	 * 
	 * @return high temp
	 
    */
   public static String GetHigh(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getHigh();
   }

   /**

	 * Returns the current formatted temperature.
	 * 
	 * @return current temp
	 
    */
   public static String GetTemp(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getTemp();
   }

   /**

	 * Returns a descriptive text for the current weather condition. It will be
	 * something like, "Mostly Cloudy", or "Partly Sunny"
	 * 
	 * @return textual description of the current weather condition.
	 
    */
   public static String GetText(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getText();
   }

   /**

	 * Return the weather condition code, as described in
	 * http://developer.yahoo.com/weather/#codes
	 * 
	 * <pre>
	 * 0	tornado
	 * 1	tropical storm
	 * 2	hurricane
	 * 3	severe thunderstorms
	 * 4	thunderstorms
	 * 5	mixed rain and snow
	 * 6	mixed rain and sleet
	 * 7	mixed snow and sleet
	 * 8	freezing drizzle
	 * 9	drizzle
	 * 10	freezing rain
	 * 11	showers
	 * 12	showers
	 * 13	snow flurries
	 * 14	light snow showers
	 * 15	blowing snow
	 * 16	snow
	 * 17	hail
	 * 18	sleet
	 * 19	dust
	 * 20	foggy
	 * 21	haze
	 * 22	smoky
	 * 23	blustery
	 * 24	windy
	 * 25	cold
	 * 26	cloudy
	 * 27	mostly cloudy (night)
	 * 28	mostly cloudy (day)
	 * 29	partly cloudy (night)
	 * 30	partly cloudy (day)
	 * 31	clear (night)
	 * 32	sunny
	 * 33	fair (night)
	 * 34	fair (day)
	 * 35	mixed rain and hail
	 * 36	hot
	 * 37	isolated thunderstorms
	 * 38	scattered thunderstorms
	 * 39	scattered thunderstorms
	 * 40	scattered showers
	 * 41	heavy snow
	 * 42	scattered snow showers
	 * 43	heavy snow
	 * 44	partly cloudy
	 * 45	thundershowers
	 * 46	snow showers
	 * 47	isolated thundershowers
	 * 3200	not available
	 * </pre>
	 * 
	 * Implementations should map their condition codes to the codes listed
	 * 
	 * @return
	 
    */
   public static String GetCode(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getCode();
   }

   /**

	 * Returns a textual description of the current weather code, ie,
	 * "snow showers"
	 * 
	 * @return textual description of the weather code
	 
    */
   public static String GetCodeText(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getCodeText();
   }

   /**

	 * Return the day for the forecasted weather, ie, "Mon", "Tue", "Wed", etc.
	 * 
	 * @return day for the forecasted weather.
	 
    */
   public static String GetDay(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getDay();
   }

   /**

	 * Returns the Sunrise time, such as, "7:28 am", if known (Usually only set
	 * on current weather instance)
	 * 
	 * @return time of sunrise
	 
    */
   public static String GetSunrise(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getSunrise();
   }

   /**

	 * Returnt he Sunset time, such as, "4:53 pm", if known (Usually only set on
	 * current weather instance)
	 * 
	 * @return time of sunset
	 
    */
   public static String GetSunset(IWeatherData iweatherdata) {
      if (iweatherdata == null) {
         return null;
      }
      return iweatherdata.getSunset();
   }

   private static myPhoenix.WeatherAPI weatherapi = new myPhoenix.WeatherAPI();
   /**

	 * Forces the current and forecasted weather to update.  It is up to the implementation to 
	 * ensure that weather updates are cached.  If the weather is updated since the last call
	 * then it will return true.  If an Error happens, then IsError will return true and GetError
	 * will contain the failure message.
	 * 
	 * @return true if the weather was updated
	 
    */
   public static boolean Update() {
      return weatherapi.Update();
   }

   /**

	 * Sets the user's location as a Postal Code or Zip Code.  If an implementation doesn't
	 * natively use this information, it should try to convert the location into something
	 * can be consumed by the implementation.  For example, the Yahoo Weather Service doesn't use
	 * zip code so it will convert it into the Yahoo WOEID.  If the implemenation can't convert
	 * the postal or zip code, then this method will return false.
	 * 
	 * @param postalOrZip ZIP or Postal Code
	 * @return true if the implementation accepted the zip code.
	 
    */
   public static boolean SetLocation(String postalOrZip) {
      return weatherapi.SetLocation(postalOrZip);
   }

   /**

	 * Get the current weather location's ZIP or Postal Code.  It may return null, if the weather hasn't
	 * been configured.
	 * 
	 * @return
	 
    */
   public static String GetLocation() {
      return weatherapi.GetLocation();
   }

   /**

	 * Set the Unit for the weather service.  Valid values are 'm' for Metric, and 's' for Standard (imperial) units.
	 * 
	 * @param units
	 
    */
   public static void SetUnits(String units) {
       weatherapi.SetUnits(units);
   }

   /**

	 * Return the configured units for the Weather Service
	 * 
	 * @return
	 
    */
   public static String GetUnits() {
      return weatherapi.GetUnits();
   }

   /**

	 * Returns the current weather information.  You should call update() before calling this method
	 * since this will not force an update automatically.
	 * 
	 * @return {@link IWeatherData} instance for the current weather conditions
	 
    */
   public static IWeatherData GetCurrentWeather() {
      return weatherapi.GetCurrentWeather();
   }

   /**

	 * Returns the long range forecast.  Depending on the implementation is may include today's weather.
	 *  
	 * @return {@link List} of {@link IWeatherData} instances for each day, ordered by day.
	 
    */
   public static List<IWeatherData> GetForecast() {
      return weatherapi.GetForecast();
   }

   /**

	 * Return true if the Weather Service is configured.
	 * 
	 * @return true if configured
	 
    */
   public static boolean IsConfigured() {
      return weatherapi.IsConfigured();
   }

   /**

	 * Returns the number of days in the weather forecast
	 * 
	 * @return days in the forecast
	 
    */
   public static int GetForecastDays() {
      return weatherapi.GetForecastDays();
   }

   /**

	 * Returns the {@link Date} the weather was last updated.
	 * 
	 * @return {@link Date} of last update
	 
    */
   public static Date GetLastUpdated() {
      return weatherapi.GetLastUpdated();
   }

   /**

	 * Returns the location name (usually the City) if known.  This may be null until an
	 * update happens.
	 * 
	 * @return location name, usually the city
	 
    */
   public static String GetLocationName() {
      return weatherapi.GetLocationName();
   }

   /**

	 * Return true if there was a Weather Service error
	 * 
	 * @return true if error
	 
    */
   public static boolean HasError() {
      return weatherapi.HasError();
   }

   /**

	 * Returns the error if HasError return true, otherwise it will return null.
	 * 
	 * @return
	 
    */
   public static String GetError() {
      return weatherapi.GetError();
   }

}