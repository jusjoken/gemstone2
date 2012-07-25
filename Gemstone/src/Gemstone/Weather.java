/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import org.apache.log4j.Logger;
import sagex.phoenix.weather.IForecastPeriod;


/**
 *
 * @author jusjoken
 * public Gemstone single Weather instance to use across the app and all extenders
 */
public class Weather {
    private static WeatherAPI GemstoneWeather = null;
    static private final Logger LOG = Logger.getLogger(Weather.class);
    private static String implList = util.ConvertListtoString(phoenix.weather2.GetWeatherImplKeys());
    private static final String implDefault = "google";
    private static final String unitsDefault = "Standard";

    //always call Init first - should be called in the Gemstone Init 
    public static void Init(){
        //force phoenix to use the current weather implementation setting
        String curImpl = util.GetOptionName(Const.WeatherProp, Const.WeatherImpl, implDefault);
        //check the current phoenix impl and only change it if it's different
        if (!curImpl.equals(phoenix.weather2.GetWeatherImplKey())){
            setImpl(curImpl);
        }else{
            LOG.debug("Init: phoenix weather was already set to '" + phoenix.weather2.GetWeatherImplKey() + "'");
            setUnits();
            SetLocation();
        }
        
        //TODO: remove old weather calls when new phoenix calls are complete
//        GemstoneWeather = new WeatherAPI();
//        GemstoneWeather.Init();
    }
    
    public static void SetImplNext(){
        util.SetListOptionNext(Const.WeatherProp, Const.WeatherImpl, implList);
        setImpl(util.GetOptionName(Const.WeatherProp, Const.WeatherImpl, implDefault));
        UpdateWeather();
    }
    
    private static boolean setImpl(String impl){
        boolean success = true;
        phoenix.weather2.SetWeatherImpl(impl);
        if (!impl.equals(phoenix.weather2.GetWeatherImplKey())){
            LOG.error("setImpl: failed to set the phoenix weather to '" + impl + "'. Using '" + phoenix.weather2.GetWeatherImplKey() + "' instead.");
            success = false;
        }else{
            LOG.debug("setImpl: phoenix weather set to '" + phoenix.weather2.GetWeatherImplKey() + "'");
            //as the impl changed... set units and location if required and do an update
            setUnits();
            SetLocation();
        }
        util.SetOption(Const.WeatherProp, Const.WeatherImpl, phoenix.weather2.GetWeatherImplKey());
        return success;
    }

    private static void setUnits(){
        String curUnits = util.GetOptionName(Const.WeatherProp, Const.WeatherUnits, unitsDefault);
        if (!curUnits.toLowerCase().equals(phoenix.weather2.GetUnits().toLowerCase())){
            //units are different so we need to set them
            phoenix.weather2.SetUnits(curUnits);
        }
    }
    
    public static void SetLocation(){
        String curLoc = util.GetOptionName(Const.WeatherProp, Const.WeatherLoc, util.OptionNotFound);
        if (!curLoc.equals(phoenix.weather2.GetLocation())){
            if (curLoc.equals(util.OptionNotFound)){
                //try setting using the EPG location
                curLoc = util.GetServerProperty("epg/zip_code", util.OptionNotFound);
                if (curLoc.equals(util.OptionNotFound)){
                    //we have no location so clear any existing location so the user can set it in the UI
                    phoenix.weather2.RemoveLocation();
                    LOG.debug("setLoc: no vaild location so clearing location. Will need to be set in the UI.");
                }else{
                    phoenix.weather2.SetLocation(curLoc);
                }
            }else{
                phoenix.weather2.SetLocation(curLoc);
            }
        }
    }
    
    public static void UpdateWeather(){
        if (phoenix.weather2.Update()){
            LOG.debug("UpdateWeather: " + phoenix.weather2.GetLocation() + " weather updated for '" + phoenix.weather2.GetLocationName() + "'(" + phoenix.weather2.GetLocation() + ") as of '" + phoenix.weather2.GetRecordedDate() + "'. Units '" + phoenix.weather2.GetUnits() + "'");
        }else{
            LOG.debug("UpdateWeather: weather update not performed. '" + phoenix.weather2.GetError() + "'");
        }
    }
    
    public static void SetUnitsNext(){
        if (phoenix.weather2.GetUnits().toLowerCase().equals("metric")){
            phoenix.weather2.SetUnits("standard");
        }else{
            phoenix.weather2.SetUnits("metric");
        }
        util.SetOption(Const.WeatherProp, Const.WeatherUnits, phoenix.weather2.GetUnits());
    }

    public static String GetIconImage(IForecastPeriod iforecastperiod){
        if (phoenix.weather2.GetCode(iforecastperiod)==-1){
            return WIcons.GetWeatherIconByNumber("na");
        }else{
            return WIcons.GetWeatherIconByNumber(phoenix.weather2.GetCode(iforecastperiod));
        }
    }
    public static String GetIconImageDay(IForecastPeriod iforecastperiod){
        if (phoenix.weather2.GetCode(iforecastperiod)==-1){
            return WIcons.GetWeatherIconByNumber("na");
        }else{
            return WIcons.GetWeatherIconByNumber(phoenix.weather2.GetCodeForceDay(phoenix.weather2.GetCode(iforecastperiod)));
        }
    }
    public static String GetIconImageNight(IForecastPeriod iforecastperiod){
        if (phoenix.weather2.GetCode(iforecastperiod)==-1){
            return WIcons.GetWeatherIconByNumber("na");
        }else{
            return WIcons.GetWeatherIconByNumber(phoenix.weather2.GetCodeForceNight(phoenix.weather2.GetCode(iforecastperiod)));
        }
    }

    public static String GetDefaultConditionsDisplay() {
        String tDefault = util.GetOptionName(Const.WeatherProp, "DefaultConditionsDisplay", util.OptionNotFound);
        if (tDefault.equals(util.OptionNotFound) || tDefault.equals("Default")){
            return "Gemstone";
        }else{
            return tDefault;
        }
    }
    public static String GetDefaultConditionsButtonText() {
        String tDefault = util.GetOptionName(Const.WeatherProp, "DefaultConditionsDisplay", util.OptionNotFound);
        if (tDefault.equals(util.OptionNotFound)){
            return "Default";
        }else{
            return tDefault;
        }
    }
    public static void SetDefaultConditionsDisplayNext() {
        String Value = util.GetOptionName(Const.WeatherProp, "DefaultConditionsDisplay", util.OptionNotFound);
        if (Value.equals("Gemstone")){
            util.SetOption(Const.WeatherProp, "DefaultConditionsDisplay", "SageNew");
        }else if (Value.equals("SageNew")){
            util.SetOption(Const.WeatherProp, "DefaultConditionsDisplay", "SageOld");
        }else if (Value.equals("SageOld")){
            util.SetOption(Const.WeatherProp, "DefaultConditionsDisplay", "Default");
        }else{
            util.SetOption(Const.WeatherProp, "DefaultConditionsDisplay", "Gemstone");
        }
    }
    public static String GetDefaultForecastDisplay() {
        String tDefault = util.GetOptionName(Const.WeatherProp, "DefaultForecastDisplay", util.OptionNotFound);
        if (tDefault.equals(util.OptionNotFound) || tDefault.equals("Default")){
            return "Gemstone";
        }else{
            return tDefault;
        }
    }
    public static String GetDefaultForecastButtonText() {
        String tDefault = util.GetOptionName(Const.WeatherProp, "DefaultForecastDisplay", util.OptionNotFound);
        if (tDefault.equals(util.OptionNotFound)){
            return "Default";
        }else{
            return tDefault;
        }
    }
    public static void SetDefaultForecastDisplayNext() {
        String Value = util.GetOptionName(Const.WeatherProp, "DefaultForecastDisplay", util.OptionNotFound);
        if (Value.equals("Gemstone")){
            util.SetOption(Const.WeatherProp, "DefaultForecastDisplay", "SageNew");
        }else if (Value.equals("SageNew")){
            util.SetOption(Const.WeatherProp, "DefaultForecastDisplay", "SageOld");
        }else if (Value.equals("SageOld")){
            util.SetOption(Const.WeatherProp, "DefaultForecastDisplay", "Default");
        }else{
            util.SetOption(Const.WeatherProp, "DefaultForecastDisplay", "Gemstone");
        }
    }
    
    public static boolean HasDescription(){
        if (phoenix.weather2.IsSupported(phoenix.weather2.GetDescription(phoenix.weather2.GetForecastPeriod(0)))){
            return true;
        }else{
            return false;
        }
        
    }
    public static int GetForecastColumns(){
        if (HasDescription()){
            return 3;
        }else if(phoenix.weather2.GetForecastDays()<3){
            return 2;
        }else{
            return 4;
        }
    }
    
    public static double GetForecastWidth(boolean Focused){
        if (HasDescription()){
            if (Focused){
                return (1.0/5)*3;
            }else{
                return (1.0/5);
            }
        }else if(phoenix.weather2.GetForecastDays()<3){
            return (1.0/2);
        }else{
            if (Focused){
                return (1.0/5)*2;
            }else{
                return (1.0/5);
            }
        }
    }
    
    public static boolean UseSplitForecast(boolean Focused){
        if (HasDescription()){
            if (Focused){
                return true;
            }else{
                return false;
            }
        }else if(phoenix.weather2.GetForecastDays()<3){
            return true;
        }else{
            if (Focused){
                return true;
            }else{
                return false;
            }
        }
    }
    
}
