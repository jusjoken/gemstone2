/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import sagex.api.Configuration;
import sagex.phoenix.tools.annotation.API;
import myPhoenix.IWeatherData;
import myPhoenix.IWeatherSupport;
import myPhoenix.IWeatherSupport.Units;
import myPhoenix.YahooWeatherSupport;

/**
 * WeatherAPI provides access to weather information, including current forecast and 
 * long range forecasts.
 *  
 * @author seans
 */
@API(group = "weather")
public class WeatherAPI {
	private static final Logger log = Logger.getLogger(WeatherAPI.class);
	private IWeatherSupport api = null;

	public WeatherAPI() {
		try {
			String prop = (String) Configuration.GetProperty(
					"phoenix/weather/weatherSupportClass",	YahooWeatherSupport.class.getName());

			api = (IWeatherSupport) Class.forName(prop).newInstance();
		} catch (Throwable e) {
			log.warn("Failed to load weather support class; defaulting to: "
					+ YahooWeatherSupport.class.getName(), e);
			api = new YahooWeatherSupport();
		}
	}
	
	/**
	 * Forces the current and forecasted weather to update.  It is up to the implementation to 
	 * ensure that weather updates are cached.  If the weather is updated since the last call
	 * then it will return true.  If an Error happens, then IsError will return true and GetError
	 * will contain the failure message.
	 * 
	 * @return true if the weather was updated
	 */
	public boolean Update() {
		return api.update();
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
	public boolean SetLocation(String postalOrZip) {
		return api.setLocation(postalOrZip);
	}

	/**
	 * Get the current weather location's ZIP or Postal Code.  It may return null, if the weather hasn't
	 * been configured.
	 * 
	 * @return
	 */
	public String GetLocation() {
		return api.getLocation();
	}
	
	/**
	 * Set the Unit for the weather service.  Valid values are 'm' for Metric, and 's' for Standard (imperial) units.
	 * 
	 * @param units
	 */
	public void SetUnits(String units) {
		IWeatherSupport.Units u = null;
		if (units==null) u = Units.Metric;
		if (units.toLowerCase().startsWith("m")) { 
			u = Units.Metric;
		} else {
			u = Units.Standard;
		}
		api.setUnits(u);
	}
	
	/**
	 * Return the configured units for the Weather Service
	 * 
	 * @return
	 */
	public String GetUnits() {
		IWeatherSupport.Units u = api.getUnits();
		if (u==null) u=Units.Metric;
		return u.name();
	}
	
	/**
	 * Returns the current weather information.  You should call update() before calling this method
	 * since this will not force an update automatically.
	 * 
	 * @return {@link IWeatherData} instance for the current weather conditions
	 */
	public IWeatherData GetCurrentWeather() {
		return api.getCurrentWeather();
	}

	/**
	 * Returns the long range forecast.  Depending on the implementation is may include today's weather.
	 *  
	 * @return {@link List} of {@link IWeatherData} instances for each day, ordered by day.
	 */
	public List<IWeatherData> GetForecast() {
		return api.getForecast();
	}
	
	/**
	 * Return true if the Weather Service is configured.
	 * 
	 * @return true if configured
	 */
	public boolean IsConfigured() {
		return api.isConfigured();
	}

	/**
	 * Returns the number of days in the weather forecast
	 * 
	 * @return days in the forecast
	 */
	public int GetForecastDays() {
		List<IWeatherData> days = GetForecast();
		if (days==null) return 0;
		return days.size();
	}
	
	/**
	 * Returns the {@link Date} the weather was last updated.
	 * 
	 * @return {@link Date} of last update
	 */
	public Date GetLastUpdated() {
		return api.getLastUpdated();
	}
	
	/**
	 * Returns the location name (usually the City) if known.  This may be null until an
	 * update happens.
	 * 
	 * @return location name, usually the city
	 */
	public String GetLocationName() {
		return api.getLocationName();
	}
	
	/**
	 * Return true if there was a Weather Service error
	 * 
	 * @return true if error
	 */
	public boolean HasError() {
		return api.hasError();
	}
	
	/**
	 * Returns the error if HasError return true, otherwise it will return null.
	 * 
	 * @return
	 */
	public String GetError() {
		return api.getError();
	}
}