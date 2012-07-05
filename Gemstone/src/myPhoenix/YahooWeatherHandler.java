/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.xml.sax.Attributes;

import sage.media.rss.RSSHandler;
import sagex.phoenix.util.DateUtils;
import sagex.phoenix.util.XmlUtil;
import myPhoenix.IWeatherData;

public class YahooWeatherHandler extends RSSHandler {
	private static HashMap<String, String> codeMap  =new HashMap<String, String>();
	static {
		codeMap.put("0","tornado");
		codeMap.put("1","tropical storm");
		codeMap.put("2","hurricane");
		codeMap.put("3","severe thunderstorms");
		codeMap.put("4","thunderstorms");
		codeMap.put("5","mixed rain and snow");
		codeMap.put("6","mixed rain and sleet");
		codeMap.put("7","mixed snow and sleet");
		codeMap.put("8","freezing drizzle");
		codeMap.put("9","drizzle");
		codeMap.put("10","freezing rain");
		codeMap.put("11","showers");
		codeMap.put("12","showers");
		codeMap.put("13","snow flurries");
		codeMap.put("14","light snow showers");
		codeMap.put("15","blowing snow");
		codeMap.put("16","snow");
		codeMap.put("17","hail");
		codeMap.put("18","sleet");
		codeMap.put("19","dust");
		codeMap.put("20","foggy");
		codeMap.put("21","haze");
		codeMap.put("22","smoky");
		codeMap.put("23","blustery");
		codeMap.put("24","windy");
		codeMap.put("25","cold");
		codeMap.put("26","cloudy");
		codeMap.put("27","mostly cloudy (night)");
		codeMap.put("28","mostly cloudy (day)");
		codeMap.put("29","partly cloudy (night)");
		codeMap.put("30","partly cloudy (day)");
		codeMap.put("31","clear (night)");
		codeMap.put("32","sunny");
		codeMap.put("33","fair (night)");
		codeMap.put("34","fair (day)");
		codeMap.put("35","mixed rain and hail");
		codeMap.put("36","hot");
		codeMap.put("37","isolated thunderstorms");
		codeMap.put("38","scattered thunderstorms");
		codeMap.put("39","scattered thunderstorms");
		codeMap.put("40","scattered showers");
		codeMap.put("41","heavy snow");
		codeMap.put("42","scattered snow showers");
		codeMap.put("43","heavy snow");
		codeMap.put("44","partly cloudy");
		codeMap.put("45","thundershowers");
		codeMap.put("46","snow showers");
		codeMap.put("47","isolated thundershowers");
		codeMap.put("3200","not available");		
	}
	
	private String city, region, country;
	private String unitTemp, unitDist, unitPress, unitSpeed;
	private String windChill, windSpeed, windDirection;
	private String humidity, visibility, pressure, rising;
	private String sunrise, sunset;
	private Date dateUpdated;
	private int ttl;
	private String imageUrl;
	
	private String text;
	
	private List<IWeatherData> days = new ArrayList<IWeatherData>();
	private WeatherData current;
	
	public YahooWeatherHandler() {
	}
	
	@Override
	public void startElement(String arg0, String arg1, String elName, Attributes attr) {
		super.startElement(arg0, arg1, elName, attr);

		if (city==null && "yweather:location".equalsIgnoreCase(elName)) {
			city = XmlUtil.attr(attr, "city");
			region = XmlUtil.attr(attr, "region");
			country = XmlUtil.attr(attr, "country");
		} else if (unitTemp==null && "yweather:units".equalsIgnoreCase(elName)) {
			unitTemp = XmlUtil.attr(attr, "temperature");
			unitDist = XmlUtil.attr(attr, "distance");
			unitPress = XmlUtil.attr(attr, "pressure");
			unitSpeed = XmlUtil.attr(attr, "speed");
		} else if (windChill==null && "yweather:wind".equalsIgnoreCase(elName)) {
			windChill = XmlUtil.attr(attr, "chill");
			windSpeed = XmlUtil.attr(attr, "speed");
			windDirection = XmlUtil.attr(attr, "direction");
		} else if (humidity==null && "yweather:atmosphere".equalsIgnoreCase(elName)) {
			humidity = XmlUtil.attr(attr, "humidity");
			visibility = XmlUtil.attr(attr, "visibility");
			pressure = XmlUtil.attr(attr, "pressure");
			rising = XmlUtil.attr(attr, "rising");
		} else if (sunrise==null && "yweather:astronomy".equalsIgnoreCase(elName)) {
			sunrise= XmlUtil.attr(attr, "sunrise");
			sunset = XmlUtil.attr(attr, "sunset");
		} else if ("yweather:condition".equalsIgnoreCase(elName) || "yweather:forecast".equals(elName)) {
			WeatherData d = new WeatherData();
			d.setCode(XmlUtil.attr(attr, "code"));
			d.setCodeText(codeMap.get(d.getCode()));
			d.setDay(XmlUtil.attr(attr, "day"));
			d.setHigh(addSuffix(XmlUtil.attr(attr, "high"),unitTemp));
			d.setLow(addSuffix(XmlUtil.attr(attr, "low"), unitTemp));
			d.setTemp(addSuffix(XmlUtil.attr(attr, "temp"), unitTemp));
			d.setText(XmlUtil.attr(attr, "text"));
			d.setDate(DateUtils.parseDate(XmlUtil.attr(attr, "date")));
			
			if ("yweather:condition".equalsIgnoreCase(elName)) {
				current = d;
				current.setSunrise(sunrise);
				current.setSunset(sunset);
			} else {
				days.add(d);
			}
		}
	}

	private String addSuffix(String val, String suffix) {
		if (val!=null && !StringUtils.isEmpty(suffix)) {
			return val + " " + suffix;
		}
		return val;
	}

	public static HashMap<String, String> getCodeMap() {
		return codeMap;
	}

	public String getCity() {
		return city;
	}

	public String getRegion() {
		return region;
	}

	public String getCountry() {
		return country;
	}

	public String getUnitTemp() {
		return unitTemp;
	}

	public String getUnitDist() {
		return unitDist;
	}

	public String getUnitPress() {
		return unitPress;
	}

	public String getUnitSpeed() {
		return unitSpeed;
	}

	public String getWindChill() {
		return windChill;
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getVisibility() {
		return visibility;
	}

	public String getPressure() {
		return pressure;
	}

	public String getRising() {
		return rising;
	}

	public String getSunrise() {
		return sunrise;
	}

	public String getSunset() {
		return sunset;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public List<IWeatherData> getDays() {
		return days;
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) {
		super.characters(arg0, arg1, arg2);
		text = new String(arg0, arg1, arg2).trim();
	}

	@Override
	public void endElement(String arg0, String arg1, String elName) {
		super.endElement(arg0, arg1, elName);
		if ("ttl".equals(elName)) {
			ttl = NumberUtils.toInt(text,180);
		} else if ("lastBuildDate".equals(elName)) {
			dateUpdated = DateUtils.parseDate(text);
		} else if (imageUrl==null) {
			imageUrl = text;
		}
 	}

	public int getTtl() {
		return ttl;
	}

	public WeatherData getCurrent() {
		return current;
	}

	private static Pattern woidPattern = Pattern.compile(".*yahoo.*/[^-]+-([0-9]{1,10})/", Pattern.CASE_INSENSITIVE);
	/**
	 * Parses the yahoo WOID from a Yahoo Weather URL
	 * http://weather.yahoo.com/canada/ontario/london-4063/
	 * @param url
	 * @return
	 */
	public static String parseWOID(String url) {
		if (url==null) return null;
		Matcher m = woidPattern.matcher(url);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}