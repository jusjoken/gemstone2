/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.util.Date;

import sagex.phoenix.tools.annotation.API;

/**
 * Interface to access the Weather Date for either the current weather
 * conditions or for the forecasted weather. Not all fields will have data,
 * depending on type of data being populated (ie, current or forecast)
 * 
 * @author sls
 */
@API(group = "weather", proxy = true)
public interface IWeatherData {
	/**
	 * Returns the {@link Date} for the given weather condition
	 * 
	 * @return {@link Date}
	 */
	public Date getDate();

	/**
	 * Returns the formatted Low Temperature for the forecasted weather.
	 * 
	 * @return low temp
	 */
	public String getLow();

	/**
	 * Returns the formatted High Temperature for the forecasted weather.
	 * 
	 * @return high temp
	 */
	public String getHigh();

	/**
	 * Returns the current formatted temperature.
	 * 
	 * @return current temp
	 */
	public String getTemp();

	/**
	 * Returns a descriptive text for the current weather condition. It will be
	 * something like, "Mostly Cloudy", or "Partly Sunny"
	 * 
	 * @return textual description of the current weather condition.
	 */
	public String getText();

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
	public String getCode();

	/**
	 * Returns a textual description of the current weather code, ie,
	 * "snow showers"
	 * 
	 * @return textual description of the weather code
	 */
	public String getCodeText();

	/**
	 * Return the day for the forecasted weather, ie, "Mon", "Tue", "Wed", etc.
	 * 
	 * @return day for the forecasted weather.
	 */
	public String getDay();

	/**
	 * Returns the Sunrise time, such as, "7:28 am", if known (Usually only set
	 * on current weather instance)
	 * 
	 * @return time of sunrise
	 */
	public String getSunrise();

	/**
	 * Returnt he Sunset time, such as, "4:53 pm", if known (Usually only set on
	 * current weather instance)
	 * 
	 * @return time of sunset
	 */
	public String getSunset();
}