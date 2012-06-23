/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import org.apache.log4j.Logger;


/**
 *
 * @author jusjoken
 * public Gemstone single Weather instance to use across the app and all extenders
 */
public class Weather {
    private static WeatherAPI GemstoneWeather = null;
    static private final Logger LOG = Logger.getLogger(Weather.class);

    //always call Init first - should be called in the Gemstone Init 
    public static void Init(){
        GemstoneWeather = new WeatherAPI();
        GemstoneWeather.Init();
    }
    public static Boolean IsConfigured(){
        if (GemstoneWeather==null){
            return Boolean.FALSE;
        }else{
            return GemstoneWeather.IsConfigured();
        }
    }
    public static void Update(){
        if (IsConfigured()){
            GemstoneWeather.Update();
        }
    }
    public static Boolean IsWeatherDotCom(){
        return GemstoneWeather.IsWeatherDotCom();
    }
    public static Boolean IsGoogleWeather(){
        return GemstoneWeather.IsGoogleWeather();
    }
    public static String GetType() {
        return GemstoneWeather.GetType();
    }
    //get a name for menu items/settings
    public static String GetTypeName() {
        return GemstoneWeather.GetTypeName();
    }
    //change to the next valid WeatherAPI type
    public static void NextType() {
        GemstoneWeather.NextType();
        GemstoneWeather.Init();
        GemstoneWeather.Update();
    }

    public static String GetTemp(){
        //LOG.debug("GetTemp: returning '" + GemstoneWeather.GetTemp() + "'");
        return GemstoneWeather.GetTemp();
    }
    public static String GetTempFull(){
        //LOG.debug("GetTempFull: returning '" + GemstoneWeather.GetTempFull() + "'");
        return GemstoneWeather.GetTempFull();
    }
    public static void SetUnits(String Value){
        GemstoneWeather.SetUnits(Value);
    }
    public static String GetUnits(){
        return GemstoneWeather.GetUnits();
    }
    public static String GetUnitsDisplay(){
        return GemstoneWeather.GetUnitsDisplay();
    }
    public static String GetIcon(){
        return GemstoneWeather.GetIcon();
    }
    public static String GetHumidity(){
        return GemstoneWeather.GetHumidity();
    }
    public static String GetCondition(){
        return GemstoneWeather.GetCondition();
    }
    public static String GetWind(){
        return GemstoneWeather.GetWind();
    }
    public static String GetLocation(){
        return GemstoneWeather.GetLocation();
    }
    public static String GetRecordedAtLocation(){
        return GemstoneWeather.GetRecordedAtLocation();
    }
    public static String GetUpdateTime(){
        return GemstoneWeather.GetUpdateTime();
    }
    public static String GetUpdateTimeExt(){
        return GemstoneWeather.GetUpdateTimeExt();
    }
    public static String GetFCDayName(Object DayNumber){
        return GemstoneWeather.GetFCDayName(DayNumber);
    }
    public static String GetFCDayNameFull(Object DayNumber){
        //LOG.debug("GetFCDayNameFull: for '" + DayNumber + "'");
        return GemstoneWeather.GetFCDayNameFull(DayNumber);
    }
    
}
