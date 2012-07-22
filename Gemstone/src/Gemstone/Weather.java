/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.Collection;
import org.apache.log4j.Logger;
import sagex.phoenix.Phoenix;
import sagex.phoenix.weather.IForecastPeriod;
import sagex.phoenix.weather.ILongRangeForecast;


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
        GemstoneWeather = new WeatherAPI();
        GemstoneWeather.Init();
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
    public static String GetDefaultConditionsDisplay() {
        return GemstoneWeather.GetDefaultConditionsDisplay();
    }
    public static String GetDefaultConditionsButtonText() {
        return GemstoneWeather.GetDefaultConditionsButtonText();
    }
    public static void SetDefaultConditionsDisplayNext() {
        GemstoneWeather.SetDefaultConditionsDisplayNext();
    }
    public static String GetDefaultForecastDisplay() {
        return GemstoneWeather.GetDefaultForecastDisplay();
    }
    public static String GetDefaultForecastButtonText() {
        return GemstoneWeather.GetDefaultForecastButtonText();
    }
    public static void SetDefaultForecastDisplayNext() {
        GemstoneWeather.SetDefaultForecastDisplayNext();
    }
    public static Collection<String> GetDayListShort(){
        return GemstoneWeather.GetDayListShort();
    }
    public static Collection<String> GetDayList(){
        return GemstoneWeather.GetDayList();
    }
    public static Integer GetDayCount(){
        return GemstoneWeather.GetDayCount();
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
    public static String GetFCIcon(Object DayNumber){
        return GemstoneWeather.GetFCIcon(DayNumber);
    }
    public static String GetFCIcon(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCIcon(DayNumber, DayPart);
    }
    public static String GetFCIconPeriod(Integer Period){
        return GemstoneWeather.GetFCIconPeriod(Period);
    }
    public static String GetHumidity(){
        return GemstoneWeather.GetHumidity();
    }
    public static String GetFCHumidity(Object DayNumber){
        return GemstoneWeather.GetFCHumidity(DayNumber);
    }
    public static String GetFCHumidityPeriod(Integer Period){
        return GemstoneWeather.GetFCHumidityPeriod(Period);
    }
    public static String GetFCHumidity(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCHumidity(DayNumber, DayPart);
    }
    public static String GetSunrise(){
        return GemstoneWeather.GetSunrise();
    }
    public static String GetSunset(){
        return GemstoneWeather.GetSunset();
    }
    public static String GetFCSunrisePeriod(Integer Period){
        return GemstoneWeather.GetFCSunrisePeriod(Period);
    }
    public static String GetFCSunrise(Object DayNumber){
        return GemstoneWeather.GetFCSunrise(DayNumber);
    }
    public static String GetFCSunsetPeriod(Integer Period){
        return GemstoneWeather.GetFCSunsetPeriod(Period);
    }
    public static String GetFCSunset(Object DayNumber){
        return GemstoneWeather.GetFCSunset(DayNumber);
    }
    public static String GetUVIndex(){
        return GemstoneWeather.GetUVIndex();
    }
    public static String GetUVWarn(){
        return GemstoneWeather.GetUVWarn();
    }
    public static String GetVisibility(){
        return GemstoneWeather.GetVisibility();
    }
    public static String GetFeelsLike(){
        return GemstoneWeather.GetFeelsLike();
    }
    public static String GetBarometer(){
        return GemstoneWeather.GetBarometer();
    }
    public static String GetDewPoint(){
        return GemstoneWeather.GetDewPoint();
    }
    public static String GetCondition(){
        return GemstoneWeather.GetCondition();
    }
    public static String GetWind(){
        return GemstoneWeather.GetWind();
    }
    public static String GetFCWind(Object DayNumber){
        return GemstoneWeather.GetFCWind(DayNumber);
    }
    public static String GetFCWindPeriod(Integer Period){
        return GemstoneWeather.GetFCWindPeriod(Period);
    }
    public static String GetFCWind(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCWind(DayNumber, DayPart);
    }
    public static String GetLocation(){
        return GemstoneWeather.GetLocation();
    }
    public static String GetLocationExt(){
        return GemstoneWeather.GetLocationExt();
    }
    public static String GetLocationID(){
        return GemstoneWeather.GetLocationID();
    }
    public static void SetLocationID(String Value){
        GemstoneWeather.SetLocationID(Value);
    }
    public static void RemoveLocationID(){
        GemstoneWeather.RemoveLocationID();
    }
    public static String GetLocationIDExt(){
        return GemstoneWeather.GetLocationIDExt();
    }
    public static Boolean SetLocationIDExt(String Value){
        return GemstoneWeather.SetLocationIDExt(Value);
    }
    public static void RemoveLocationIDExt(){
        GemstoneWeather.RemoveLocationIDExt();
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
    public static String GetFCDayName(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCDayName(DayNumber, DayPart);
    }
    public static String GetFCDayNamePeriod(Integer Period){
        return GemstoneWeather.GetFCDayNamePeriod(Period);
    }
    public static String GetFCDayNameFullPeriod(Integer Period){
        return GemstoneWeather.GetFCDayNameFullPeriod(Period);
    }
    public static String GetFCHigh(Object DayNumber){
        return GemstoneWeather.GetFCHigh(DayNumber);
    }
    public static String GetFCHighFull(Object DayNumber){
        return GemstoneWeather.GetFCHighFull(DayNumber);
    }
    public static String GetFCLow(Object DayNumber){
        return GemstoneWeather.GetFCLow(DayNumber);
    }
    public static String GetFCLowFull(Object DayNumber){
        return GemstoneWeather.GetFCLowFull(DayNumber);
    }
    public static String GetFCTempPeriod(Integer Period){
        return GemstoneWeather.GetFCTempPeriod(Period);
    }
    public static String GetFCTempFullPeriod(Integer Period){
        return GemstoneWeather.GetFCTempFullPeriod(Period);
    }
    public static Boolean FCHasTodaysHigh(){
        return GemstoneWeather.FCHasTodaysHigh();
    }
    public static String GetFCCondition(Object DayNumber){
        return GemstoneWeather.GetFCCondition(DayNumber);
    }
    public static String GetFCCondition(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCCondition(DayNumber, DayPart);
    }
    public static String GetFCConditionPeriod(Integer Period){
        return GemstoneWeather.GetFCConditionPeriod(Period);
    }
    public static String GetFCPrecip(Object DayNumber){
        return GemstoneWeather.GetFCPrecip(DayNumber);
    }
    public static String GetFCPrecip(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCPrecip(DayNumber, DayPart);
    }
    public static String GetFCPrecipPeriod(Integer Period){
        return GemstoneWeather.GetFCPrecipPeriod(Period);
    }
    public static Boolean HasFCDescription(){
        return GemstoneWeather.HasFCDescription();
    }
    public static String GetFCDescription(Object DayNumber, String DayPart){
        return GemstoneWeather.GetFCDescription(DayNumber, DayPart);
    }
    public static String GetFCDescriptionPeriod(Integer Period){
        return GemstoneWeather.GetFCDescriptionPeriod(Period);
    }
    public static Boolean HasExtForecast(){
        return GemstoneWeather.HasExtForecast();
    }
    public static String GetFCTempTypeText(Integer Period){
        return GemstoneWeather.GetFCTempTypeText(Period);
    }
    public static Integer GetFCPeriodCount(){
        return GemstoneWeather.GetFCPeriodCount();
    }
    public static Integer GetPeriod(Integer DayNumber, String DayPart){
        return GemstoneWeather.GetPeriod(DayNumber, DayPart);
    }
    public static Integer GetDayFromPeriod(Integer Period){
        return GemstoneWeather.GetDayFromPeriod(Period);
    }
    public static String GetDayPartFromPeriod(Integer Period){
        return GemstoneWeather.GetDayPartFromPeriod(Period);
    }
    public static Object GetInstance(){
        return GemstoneWeather.GetInstance();
    }
    
    
    
}
