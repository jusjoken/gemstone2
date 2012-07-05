/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.util.Date;
import java.util.List;

public interface IWeatherSupport {
	public static enum Units {Metric, Standard}

	public boolean update();

	public boolean setLocation(String postalOrZip);

	public String getLocation();
	
	public String getLocationName();

	public void setUnits(Units u);

	public Units getUnits();

	public IWeatherData getCurrentWeather();

	public List<IWeatherData> getForecast();
	
	public boolean isConfigured();

	public Date getLastUpdated();

	public boolean hasError();
	public String getError();
}