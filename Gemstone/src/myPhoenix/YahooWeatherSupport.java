/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import sage.media.rss.RSSParser;
import sagex.phoenix.configuration.proxy.GroupProxy;
import myPhoenix.IWeatherData;
import myPhoenix.IWeatherSupport;
import sagex.phoenix.weather.WeatherConfiguration;

public class YahooWeatherSupport implements IWeatherSupport {
	private Logger log = Logger.getLogger(this.getClass());
	
	private Date lastUpdated = null;
	private int ttl = 180;
	
	private String error;
	
	private WeatherConfiguration config = GroupProxy.get(WeatherConfiguration.class);
	
	private IWeatherData current = null;
	private List<IWeatherData> forecast = null;

	private String locationName;
	
	public YahooWeatherSupport() {
	}
	
	public boolean hasError() {
		return error != null;
	}
	
	public String getError() {
		return error;
	}
	
	public boolean update() {
		error = null;
		
		if (!isConfigured()) {
			error = "Please configure the Yahoo Weather WOEID for your location";
			return false;
		}
		
		if (shouldUpdate()) {
			String units = null;
			if (getUnits() == Units.Metric) {
				units = "c";
			} else {
				units = "f";
			}
			
			String woeid = config.getYahooWOEID();
			
			String rssUrl = "http://weather.yahooapis.com/forecastrss?w=" + woeid + "&u=" + units;
			log.info("Getting Yahoo Weather for " + rssUrl);
			try {
				YahooWeatherHandler handler = new YahooWeatherHandler();
				RSSParser.parseXmlFile(new URL(rssUrl), handler, false);
				lastUpdated = new Date(System.currentTimeMillis());
				ttl=handler.getTtl();
				
				current = handler.getCurrent();
				forecast  = handler.getDays();
				
				locationName = handler.getCity();
				
				return true;
			} catch (Exception e) {
				error = "Yahoo weather update failed";
				log.error("Failed to update weather for " + rssUrl, e);
			}
		}
		
		return false;
	}

	public boolean isConfigured() {
		return !StringUtils.isEmpty(config.getYahooWOEID());
	}
	
	private boolean shouldUpdate() {
		if (lastUpdated==null) return true;
		long later = lastUpdated.getTime() + (ttl * 60 * 1000);
		if (System.currentTimeMillis()>later) return true;
		return false;
	}

	@Override
	public boolean setLocation(String postalOrZip) {
		error = null;
		boolean configured=false;
		// convert zip to woeid
		try {
			config.setLocation(postalOrZip);
			
			String url = "http://query.yahooapis.com/v1/public/yql?q=select%20woeid%20from%20geo.places%20where%20text='"+postalOrZip+"'%20limit%201";
			SAXReader reader = new SAXReader();
	        Document document = reader.read(url);
			String woeid = document.getRootElement().element("results").element("place").element("woeid").getText();
			
			if (woeid != null) {
				config.setYahooWOEID(woeid);
			}
			
			configured=true;
			lastUpdated = null;
		} 
		catch (Exception e) {
			log.warn("Failed to convert " + postalOrZip + " to woeid", e);
			error = "Failed to convert the Location into a valid Yahoo WOEID";
			configured =false;
		}
		return configured;
	}

	@Override
	public String getLocation() {
		return config.getLocation();
	}

	@Override
	public void setUnits(Units u) {
		if (u == null || u == Units.Metric) {
			config.setUnits("m");
		} else {
			config.setUnits("s");
		}
	}

	@Override
	public Units getUnits() {
		String u = config.getUnits();
		if (StringUtils.isEmpty(u) || u.toLowerCase().startsWith("m")) {
			return Units.Metric;
		} else {
			return Units.Standard;
		}
	}

	@Override
	public IWeatherData getCurrentWeather() {
		return current;
	}

	@Override
	public List<IWeatherData> getForecast() {
		return forecast;
	}

	public String getLocationName() {
		return locationName;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
}