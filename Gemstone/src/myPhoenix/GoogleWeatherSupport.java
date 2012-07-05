/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPhoenix;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import java.util.Date;
import java.util.List;

import java.util.regex.Pattern;
import sagex.phoenix.configuration.proxy.GroupProxy;
import myPhoenix.IWeatherData;
import myPhoenix.IWeatherSupport;
import sage.google.weather.GoogleWeather;
import sagex.phoenix.weather.WeatherConfiguration;
/**
 *
 * @author jusjoken
 */
public class GoogleWeatherSupport implements IWeatherSupport {
    private Logger log = Logger.getLogger(this.getClass());

    private Date lastUpdated = null;
    private int ttl = 45;  //update every 45 mins

    private String error;

    private WeatherConfiguration config = GroupProxy.get(WeatherConfiguration.class);

    private IWeatherData current = null;
    private List<IWeatherData> forecast = null;

    private String locationName;

    private GoogleWeather gWeather = null;

    public GoogleWeatherSupport() {
        gWeather = GoogleWeather.getInstance();
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
            error = "Please configure the Google location";
            return false;
        }

        if (shouldUpdate()) {

            String LangCode = sagex.api.Configuration.GetProperty("ui/translation_language_code", "en");
            if (isNWSConfigured()){
                log.info("Getting Google Weather for '" + gWeather.getGoogleWeatherLoc() + "' and NWS Weather for '" + gWeather.getNWSZipCode() + "'");
                gWeather.updateAllNow(LangCode);
            }else{
                log.info("Getting Google Weather for '" + gWeather.getGoogleWeatherLoc() + "'");
                gWeather.updateGoogleNow(LangCode);
            }
            lastUpdated = new Date(System.currentTimeMillis());
            locationName = gWeather.getGWCityName();

            //populate current
            WeatherData d = new WeatherData();
            d.setCode("0");  //TODO: need to convert google condition to a code - perhaps use WIcons and the parsed url 
            d.setCodeText(gWeather.getGWCurrentCondition("CondText"));
            d.setDay(gWeather.getGWForecastCondition(0, "name"));
            d.setHigh(gWeather.getGWForecastCondition(0, "high"));
            d.setLow(gWeather.getGWForecastCondition(0, "low"));
            d.setTemp(gWeather.getGWCurrentCondition("Temp"));
            d.setText(gWeather.getGWCurrentCondition("CondText"));
            d.setDate(null); //Google weather does not have a date - perhaps could determine it from the day name????
            
            current = d;
            

            //populate all the days in the forecast
            //forecast  = handler.getDays();

        }

        return false;
    }

    public boolean isConfigured() {
        return !StringUtils.isEmpty(gWeather.getGoogleWeatherLoc());
    }

    public boolean isNWSConfigured() {
        return !StringUtils.isEmpty(gWeather.getNWSZipCode());
    }

    private boolean shouldUpdate() {
        if (lastUpdated==null) return true;
        long later = lastUpdated.getTime() + (ttl * 60 * 1000);
        if (System.currentTimeMillis()>later) return true;
        return false;
    }
    
    private boolean isValidZIP(String ZIPCode){
        //String regex = "^\\d{5}(-\\d{4})?$";        
        String regex = "^\\d{5}";
        return Pattern.matches(regex, ZIPCode);
    }

    @Override
    public boolean setLocation(String postalOrZip) {
        gWeather.setGoogleWeatherLoc(postalOrZip);
        //attempt to set the NWS location as well
        if (isValidZIP(postalOrZip)){
            gWeather.setNWSZipCode(postalOrZip);
        }
        lastUpdated = null;
        return true;
    }

    @Override
    public String getLocation() {
        return gWeather.getGoogleWeatherLoc();
    }

    @Override
    public void setUnits(Units u) {
        if (u == null || u == Units.Metric) {
            gWeather.setUnits("m");
        } else {
            gWeather.setUnits("s");
        }
    }

    @Override
    public Units getUnits() {
        String u = gWeather.getUnits();
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