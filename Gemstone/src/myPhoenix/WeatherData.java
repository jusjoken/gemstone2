/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import java.util.Date;

import myPhoenix.IWeatherData;

public class WeatherData implements IWeatherData {
	private Date date;
	private String low;
	private String high;
	private String temp;
	private String text;
	private String code;
	private String codeText;
	private String day;
	private String sunrise;
	private String sunset;
	
	public WeatherData() {
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeText() {
		return codeText;
	}
	public void setCodeText(String codeText) {
		this.codeText = codeText;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getSunrise() {
		return sunrise;
	}

	public void setSunrise(String sunrise) {
		this.sunrise = sunrise;
	}

	public String getSunset() {
		return sunset;
	}

	public void setSunset(String sunset) {
		this.sunset = sunset;
	}

	@Override
	public String toString() {
		return "WeatherData [" + (date != null ? "date=" + date + ", " : "")
				+ (low != null ? "low=" + low + ", " : "")
				+ (high != null ? "high=" + high + ", " : "")
				+ (temp != null ? "temp=" + temp + ", " : "")
				+ (text != null ? "text=" + text + ", " : "")
				+ (code != null ? "code=" + code + ", " : "")
				+ (codeText != null ? "codeText=" + codeText + ", " : "")
				+ (day != null ? "day=" + day + ", " : "")
				+ (sunrise != null ? "sunrise=" + sunrise + ", " : "")
				+ (sunset != null ? "sunset=" + sunset : "") + "]";
	}
}