/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import org.apache.log4j.Logger;
import sage.google.weather.GoogleWeather;
import tv.sage.weather.WeatherDotCom;
/**
 *
 * @author jusjoken
 */
public class WeatherAPI {
    private static enum APITypes{GOOGLE,WEATHERCOM};
    private APITypes APIType = APITypes.GOOGLE;
    private GoogleWeather gWeather = null;
    private WeatherDotCom wWeather = null;
    static private final Logger LOG = Logger.getLogger(WeatherAPI.class);
    
    public WeatherAPI(String APIType) {
        //used for temporarily getting a WeatherAPI object without saving the type
        this.APIType = getAPIType(APIType);
    }
    public WeatherAPI() {
        this.APIType = getAPIType();
        //InitAPI();
    }

    private APITypes getAPIType() {
        String tAPIType = util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            return APITypes.GOOGLE;
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            return APITypes.WEATHERCOM;
        }else{
            return APITypes.GOOGLE;
        }
    }
    private APITypes getAPIType(String tAPIType) {
        APITypes tempAPIType = APITypes.GOOGLE;
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            tempAPIType = APITypes.GOOGLE;
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            tempAPIType = APITypes.WEATHERCOM;
        }
        return tempAPIType;
    }

    private void setAPIType(APITypes newAPIType) {
        APITypes currAPIType = getAPIType();
        //LOG.debug("setAPIType: current '" + currAPIType + "' changing to '" + newAPIType + "'");
        if (!newAPIType.equals(currAPIType)){
            //change the type
            util.SetOption(Const.WeatherProp, "APIType", newAPIType.toString());
            APIType = newAPIType;
        }
    }
    
    public void Init(){
        LOG.debug("Init: Type '" + APIType + "'");
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather = WeatherDotCom.getInstance();
            gWeather = null;
        }else{
            //default to GOOGLE
            gWeather = GoogleWeather.getInstance();
            wWeather = null;
        }
    }
    public void Update(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.updateNow();
        }else{
            //Google
            //get the language code
            String LangCode = util.GetProperty("ui/translation_language_code", "en");
            gWeather.updateAllNow(LangCode);
        }
    }
    public Boolean IsConfigured(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (wWeather==null){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }else{
            if (gWeather==null){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }
    }
    public Boolean IsWeatherDotCom(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public Boolean IsGoogleWeather(){
        if (APIType.equals(APITypes.GOOGLE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public Boolean IsGoogleNWSWeather(){
        if (APIType.equals(APITypes.GOOGLE)){
            if (gWeather.getNWSZipCode()==null || gWeather.getNWSZipCode().length()==0){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }else{
            return Boolean.FALSE;
        }
    }
    public String GetType() {
        return util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
    }
    //get a name for menu items/settings
    public String GetTypeName() {
        String tAPIType = util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            return "Google Weather";
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            return "Weather.com";
        }else{
            return "Google Weather";
        }
    }
    //change to the next valid WeatherAPI type
    public void NextType() {
        APITypes tempAPIType = getAPIType();
        if (tempAPIType.equals(APITypes.GOOGLE)){
            setAPIType(APITypes.WEATHERCOM);
        }else{
            setAPIType(APITypes.GOOGLE);
        }
    }
    //allow override of the default conditions or forecast display
    public String GetDefaultConditionsDisplay() {
        String tDefault = util.GetOptionName(Const.WeatherProp, "DefaultConditionsDisplay", util.OptionNotFound);
        if (tDefault.equals(util.OptionNotFound)){
            if (APIType.equals(APITypes.WEATHERCOM)){
                return "Old";
            }else{
                return "New";
            }
        }else{
            return tDefault;
        }
    }
    public void SetDefaultConditionsDisplay(String Value) {
        if (Value.equals("Old")){
            util.SetOption(Const.WeatherProp, "DefaultConditionsDisplay", Value);
        }else{
            util.SetOption(Const.WeatherProp, "DefaultConditionsDisplay", "New");
        }
    }
    public String GetDefaultForecastDisplay() {
        String tDefault = util.GetOptionName(Const.WeatherProp, "DefaultForecastDisplay", util.OptionNotFound);
        if (tDefault.equals(util.OptionNotFound)){
            if (APIType.equals(APITypes.WEATHERCOM)){
                return "Old";
            }else{
                return "New";
            }
        }else{
            return tDefault;
        }
    }
    public void SetDefaultForecastDisplay(String Value) {
        if (Value.equals("Old")){
            util.SetOption(Const.WeatherProp, "DefaultForecastDisplay", Value);
        }else{
            util.SetOption(Const.WeatherProp, "DefaultForecastDisplay", "New");
        }
    }

    public String GetTemp(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            //need to strip off the degree and unit from the temp
            String tTemp = wWeather.getCurrentCondition("curr_temp");
            return tTemp.substring(0, tTemp.length()-2);
        }else{
            return gWeather.getGWCurrentCondition("Temp");
        }
    }
    //return the temp with the degrees and units as part of the display
    public String GetTempFull(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            //this already has the units so just return the results
            return wWeather.getCurrentCondition("curr_temp");
        }else{
            return gWeather.getGWCurrentCondition("Temp") + GetUnitsDisplay();
        }
    }
    public void SetUnits(String Value){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.setUnits(Value);
        }else{
            gWeather.setUnits(Value);
        }
    }
    public String GetUnits(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getUnits();
        }else{
            return gWeather.getUnits();
        }
    }
    public String GetUnitsDisplay(){
        if (GetUnits().equals("s")){
            return "\u00B0" + "F";
        }else{
            return "\u00B0" + "C";
        }
    }
    public String GetIcon(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return WIcons.GetWeatherIconByNumber(wWeather.getCurrentCondition("curr_icon"));
        }else{
            return WIcons.GetWeatherIconURL(gWeather.getGWCurrentCondition("iconURL"));
        }
    }
    public String GetFCIcon(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (iDay==0 && !FCHasTodaysHigh()){
                return WIcons.GetWeatherIconByNumber(wWeather.getForecastCondition("icon" + "n" + iDay));
            }else{
                return WIcons.GetWeatherIconByNumber(wWeather.getForecastCondition("icon" + "d" + iDay));
            }
        }else{
            return WIcons.GetWeatherIconURLDay(gWeather.getGWForecastCondition(iDay, "iconURL"));
        }
    }
    public String GetFCIcon(Object DayNumber, String DayPart){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            return WIcons.GetWeatherIconByNumber(wWeather.getForecastCondition("icon" + ValidateDayPart(DayPart) + iDay));
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return WIcons.GetWeatherIconURL(gWeather.getNWSForecastCondition(GetPeriod(iDay, ValidateDayPart(DayPart)), "icon_url"));
            }else{
                return WIcons.GetWeatherIconURLDay(gWeather.getGWForecastCondition(iDay, "iconURL"));
            }
            
        }
    }
    public String GetFCIconPeriod(Integer Period){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return WIcons.GetWeatherIconByNumber(wWeather.getForecastCondition("icon" + GetDayPartFromPeriod(Period) + GetDayFromPeriod(Period)));
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return WIcons.GetWeatherIconURL(gWeather.getNWSForecastCondition(Period, "icon_url"));
            }else{
                return WIcons.GetWeatherIconURLDay(gWeather.getGWForecastCondition(GetDayFromPeriod(Period), "iconURL"));
            }
            
        }
    }
    public String GetHumidity(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_humidity");
        }else{
            return gWeather.getGWCurrentCondition("HumidText").replaceAll("Humidity:", "").trim();
        }
    }
    public String GetCondition(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_conditions");
        }else{
            return gWeather.getGWCurrentCondition("CondText");
        }
    }
    public String GetWind(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tWind = wWeather.getCurrentCondition("curr_wind");
            if (tWind.startsWith("CALM")){
                return "CALM";
            }
            String WindDir = tWind.substring(0, tWind.indexOf(" "));
            String WindSpeed = tWind.substring(tWind.indexOf(" ")+1);
            return WindDir + "/" + WindSpeed;
        }else{
            String tWind = gWeather.getGWCurrentCondition("WindText");
            if (tWind.contains("0 mph")){
                return "CALM";
            }else{
                return tWind.replaceAll("Wind:", "").trim().replaceFirst(" at ", "/");
            }
        }
    }
    public String GetLocation(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getLocationInfo("curr_location");
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.getGWCityName() + " (" + gWeather.getNWSZipCode() + ")";
            }else{
                return gWeather.getGWCityName();
            }
            
        }
    }
    //Use the Ext function for sources that have a second source like NWS to return the 2nd source
    public String GetLocationExt(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getLocationInfo("curr_location");
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.getNWSCityName() + " (" + gWeather.getNWSZipCode() + ")";
            }else{
                return gWeather.getGWCityName();
            }
            
        }
    }
    public String GetLocationID(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getLocationID();
        }else{
            return gWeather.getGoogleWeatherLoc();
        }
    }
    public void SetLocationID(String Value){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.setLocationID(Value);
        }else{
            gWeather.setGoogleWeatherLoc(Value);
        }
    }
    public void RemoveLocationID(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.setLocationID("");
        }else{
            gWeather.removeGoogleWeatherLoc();
        }
    }
    //Use the Ext function for sources that have a second source like NWS to return the 2nd source
    public String GetLocationIDExt(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getLocationID();
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.getNWSZipCode();
            }else{
                return gWeather.getGoogleWeatherLoc();
            }
            
        }
    }
    public Boolean SetLocationIDExt(String Value){
        if (APIType.equals(APITypes.WEATHERCOM)){
            wWeather.setLocationID(Value);
            return Boolean.TRUE;
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.setNWSZipCode(Value);
            }else{
                return Boolean.FALSE;
            }
            
        }
    }
    public void RemoveLocationIDExt(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            //setting this to blank may need to be tested
            wWeather.setLocationID("");
        }else{
            if (IsGoogleNWSWeather()){
                gWeather.removeNWSZipCode();
            }else{
                //do nothing as has no Extended function
            }
            
        }
    }
    public String GetRecordedAtLocation(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_recorded_at");
        }else{
            return gWeather.getGWCityName();
        }
    }
    public String GetUpdateTime(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_updated");
        }else{
            Long tTime = gWeather.getLastUpdateTimeGW();
            return sagex.api.Utility.PrintDateLong(tTime) + " at " + sagex.api.Utility.PrintTimeShort(tTime);
        }
    }
    public String GetUpdateTimeExt(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getCurrentCondition("curr_updated");
        }else{
            Long tTime = gWeather.getLastUpdateTimeGW();
            if (IsGoogleNWSWeather()){
                tTime = gWeather.getLastUpdateTimeNWS();
            }
            return sagex.api.Utility.PrintDateLong(tTime) + " at " + sagex.api.Utility.PrintTimeShort(tTime);
        }
    }
    public String GetFCDayName(Object DayNumber){
        //return a short dayname like Sun, Mon, Tues etc (first 3 letters)
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tName = wWeather.getForecastCondition("date" + iDay);
            return tName.substring(0, 3);
        }else{
            return gWeather.getGWForecastCondition(iDay, "name");
        }
    }
    public String GetFCDayNameFull(Object DayNumber){
        //return the full dayname that is available
        //LOG.debug("GetFCDayNameFull: for '" + DayNumber + "'");
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("date" + iDay);
        }else{
            return gWeather.getGWForecastCondition(iDay, "name");
        }
    }
    public String GetFCDayNamePeriod(Integer Period){
        //return a short dayname like Sun, Mon, Tues etc (first 3 letters)
        Integer iDay = GetDayFromPeriod(Period);
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tName = wWeather.getForecastCondition("date" + iDay);
            return tName.substring(0, 3);
        }else{
            if (IsGoogleNWSWeather()){
                String tDateName = gWeather.getNWSForecastCondition(GetPeriod(iDay, "d"), "name");
                if (tDateName.toLowerCase().equals("monday")){
                    return tDateName.substring(0, 3);
                }else if (tDateName.toLowerCase().equals("tuesday")){
                    return tDateName.substring(0, 3);
                }else if (tDateName.toLowerCase().equals("wednesday")){
                    return tDateName.substring(0, 3);
                }else if (tDateName.toLowerCase().equals("thursday")){
                    return tDateName.substring(0, 3);
                }else if (tDateName.toLowerCase().equals("friday")){
                    return tDateName.substring(0, 3);
                }else if (tDateName.toLowerCase().equals("saturday")){
                    return tDateName.substring(0, 3);
                }else if (tDateName.toLowerCase().equals("sunday")){
                    return tDateName.substring(0, 3);
                }else{
                    //return the first word to keep it short
                    return tDateName.substring(0, tDateName.indexOf(" "));
                }
            }else{
                return gWeather.getGWForecastCondition(iDay, "name");
            }
        }
    }
    public String GetFCDayNameFullPeriod(Integer Period){
        //return the full dayname that is available
        if (IsGoogleNWSWeather()){
            return gWeather.getNWSForecastCondition(Period, "name");
        }
        Integer iDay = GetDayFromPeriod(Period);
        String DayPart = GetDayPartFromPeriod(Period);
        //special handling for Today and Tonight
        if (iDay==0){
            if (DayPart.equals("d")){
                return "Today";
            }else{
                return "Tonight";
            }
        }else{
            if (APIType.equals(APITypes.WEATHERCOM)){
                String DayName = wWeather.getForecastCondition("date" + iDay);
                DayName = DayName.substring(0, DayName.indexOf(" "));
                if (DayPart.equals("n")){
                    return DayName + " Night";
                }else{
                    return DayName;
                }
            }else{
                return gWeather.getGWForecastCondition(iDay, "name");
            }
        }
    }
    public String GetFCHigh(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            //need to strip off the degree and unit from the temp
            String tTemp = wWeather.getForecastCondition("hi"+iDay);
            return tTemp.substring(0, tTemp.length()-2);
        }else{
            return gWeather.getGWForecastCondition(iDay, "high");
        }
    }
    //return the temp with the degrees and units as part of the display
    public String GetFCHighFull(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("hi"+iDay);
        }else{
            return gWeather.getGWForecastCondition(iDay, "high") + GetUnitsDisplay();
        }
    }
    public String GetFCLow(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tTemp = wWeather.getForecastCondition("low"+iDay);
            return tTemp.substring(0, tTemp.length()-2);
        }else{
            return gWeather.getGWForecastCondition(iDay, "low");
        }
    }
    //return the temp with the degrees and units as part of the display
    public String GetFCLowFull(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("low"+iDay);
        }else{
            return gWeather.getGWForecastCondition(iDay, "low") + GetUnitsDisplay();
        }
    }
    //get a forecst temp for a specified period
    public String GetFCTempPeriod(Integer Period){
        if (IsGoogleNWSWeather()){
            return gWeather.getNWSForecastCondition(Period, "temp");
        }
        LOG.debug("GetFCTempPeriod: for Period '" + Period + "'");
        if (GetDayPartFromPeriod(Period).equals("d")){
            return GetFCHigh(GetDayFromPeriod(Period));
        }else{
            return GetFCLow(GetDayFromPeriod(Period));
        }
    }
    public String GetFCTempFullPeriod(Integer Period){
        if (IsGoogleNWSWeather()){
            return gWeather.getNWSForecastCondition(Period, "temp") + GetUnitsDisplay();
        }
        if (GetDayPartFromPeriod(Period).equals("d")){
            return GetFCHighFull(GetDayFromPeriod(Period));
        }else{
            return GetFCLowFull(GetDayFromPeriod(Period));
        }
    }
    public Boolean FCHasTodaysHigh(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tHigh = wWeather.getForecastCondition("hi0");
            if (tHigh.startsWith("N/A")){
                return Boolean.FALSE;
            }else{
                return Boolean.TRUE;
            }
        }else{
            return Boolean.TRUE;
        }
    }
    //get a single forecst condition representing the day
    public String GetFCCondition(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (iDay==0 && !FCHasTodaysHigh()){
                return wWeather.getForecastCondition("conditions" + "n" + iDay);
            }else{
                return wWeather.getForecastCondition("conditions" + "d" + iDay);
            }
        }else{
            return gWeather.getGWForecastCondition(iDay, "CondText");
        }
    }
    //get a forecst condition for the specified part of the day
    public String GetFCCondition(Object DayNumber, String DayPart){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("conditions" + ValidateDayPart(DayPart) + iDay);
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return gWeather.getNWSForecastCondition(GetPeriod(iDay, ValidateDayPart(DayPart)), "summary");
            }else{
                //without NWS, Google only has one condition
                return gWeather.getGWForecastCondition(iDay, "CondText");
            }
        }
    }
    //get a forecst condition for a specified period
    public String GetFCConditionPeriod(Integer Period){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("conditions" + GetDayPartFromPeriod(Period) + GetDayFromPeriod(Period));
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return gWeather.getNWSForecastCondition(Period, "summary");
            }else{
                //without NWS, Google only has one condition
                return gWeather.getGWForecastCondition(GetDayFromPeriod(Period), "CondText");
            }
        }
        
    }
    public String GetFCPrecip(Object DayNumber){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            //need to remove the % symbol
            if (iDay==0 && !FCHasTodaysHigh()){
                String tPrecip = wWeather.getForecastCondition("precip" + "n" + iDay);
                return tPrecip.substring(0, tPrecip.length()-1);
            }else{
                String tPrecip = wWeather.getForecastCondition("precip" + "d" + iDay);
                return tPrecip.substring(0, tPrecip.length()-1);
            }
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.getNWSForecastCondition(GetPeriod(iDay, "d"), "precip");
            }else{
                return "N/A";
            }
        }
    }
    public String GetFCPrecip(Object DayNumber, String DayPart){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tPrecip = wWeather.getForecastCondition("precip" + ValidateDayPart(DayPart) + iDay);
            return tPrecip.substring(0, tPrecip.length()-1);
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.getNWSForecastCondition(GetPeriod(iDay, ValidateDayPart(DayPart)), "precip");
            }else{
                return "N/A";
            }
        }
    }
    public String GetFCPrecipPeriod(Integer Period){
        if (APIType.equals(APITypes.WEATHERCOM)){
            String tPrecip = wWeather.getForecastCondition("precip" + GetDayPartFromPeriod(Period) + GetDayFromPeriod(Period));
            return tPrecip.substring(0, tPrecip.length()-1);
        }else{
            if (IsGoogleNWSWeather()){
                return gWeather.getNWSForecastCondition(Period, "precip");
            }else{
                return "N/A";
            }
        }
    }
    public Boolean HasFCDescription(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return Boolean.FALSE;
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return Boolean.TRUE;
            }else{
                //without NWS, Google only has one condition
                return Boolean.FALSE;
            }
        }
    }
    public String GetFCDescription(Object DayNumber, String DayPart){
        Integer iDay = util.GetInteger(DayNumber, 0);
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("conditions" + ValidateDayPart(DayPart) + iDay);
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return gWeather.getNWSForecastCondition(GetPeriod(iDay, ValidateDayPart(DayPart)), "forecast_text");
            }else{
                //without NWS, Google only has one condition
                return gWeather.getGWForecastCondition(iDay, "CondText");
            }
        }
    }
    //get a forecst condition for a specified period
    public String GetFCDescriptionPeriod(Integer Period){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return wWeather.getForecastCondition("conditions" + GetDayPartFromPeriod(Period) + GetDayFromPeriod(Period));
        }else{
            if (IsGoogleNWSWeather()){
                //with NWS need to convert Day and DayPart to a period
                return gWeather.getNWSForecastCondition(Period, "forecast_text");
            }else{
                //without NWS, Google only has one condition
                return gWeather.getGWForecastCondition(GetDayFromPeriod(Period), "CondText");
            }
        }
        
    }
    public Boolean HasExtForecast(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            return Boolean.TRUE;
        }else{
            if (IsGoogleNWSWeather()){
                return Boolean.TRUE;
            }else{
                //without NWS, Google does not have enough info for an extended forecast
                return Boolean.FALSE;
            }
        }
    }
    public String GetFCTempTypeText(Integer Period){
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (GetDayPartFromPeriod(Period).equals("d")){
                return "High";
            }else{
                return "Low";
            }
        }else{
            if (IsGoogleNWSWeather()){
                if (gWeather.getNWSForecastCondition(Period, "tempType").equals("h")){
                    return "High";
                }else{
                    return "Low";
                }
            }else{
                return "High";
            }
        }
        
    }
    public Integer GetFCPeriodCount(){
        if (IsGoogleNWSWeather()){
            return gWeather.getNWSPeriodCount();
        }else{
            if (APIType.equals(APITypes.WEATHERCOM)){
                return 10;
            }else{
                return gWeather.getGWDayCount()*2;
            }
        }
    }
    
    private Integer GetPeriod(Integer DayNumber, String DayPart){
        //will return -1 if the period is not valid
        //find offset by checking period 0 for "l" or "h"
        Integer DayPartOffset = 0;
        if (DayPart.equals("n")){
            DayPartOffset = 1;
        }
        String checkPeriod = gWeather.getNWSForecastCondition(0, "tempType");
        if (checkPeriod.equals("h")){
            return (DayNumber * 2) + DayPartOffset; 
        }else{
            return (DayNumber * 2) + DayPartOffset - 1; 
        }
    }
    private Integer GetDayFromPeriod(Integer Period){
        if (IsGoogleNWSWeather()){
            String checkPeriod = gWeather.getNWSForecastCondition(0, "tempType");
            if (checkPeriod.equals("h")){
                return Period/2; 
            }else{
                return (Period + 1)/2; 
            }
        }else{
            return Period/2; 
        }
    }
    private String GetDayPartFromPeriod(Integer Period){
        if (IsGoogleNWSWeather()){
            String checkPeriod = gWeather.getNWSForecastCondition(0, "tempType");
            if (checkPeriod.equals("h")){
                LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' checkPeriod '" + checkPeriod + "'");
                if (Period%2==0){
                    LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' checkPeriod '" + checkPeriod + "' returning 'd'");
                    return "d";
                }else{
                    LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' checkPeriod '" + checkPeriod + "' returning 'n'");
                    return "n";
                }
            }else{
                LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' checkPeriod '" + checkPeriod + "'");
                if (Period%2==0){
                    LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' checkPeriod '" + checkPeriod + "' returning 'n'");
                    return "n";
                }else{
                    LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' checkPeriod '" + checkPeriod + "' returning 'd'");
                    return "d";
                }
            }
        }else{
            if (Period%2==0){
                LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' returning 'd'");
                return "d";
            }else{
                LOG.debug("GetDayPartFromPeriod: for Period '" + Period + "' returning 'n'");
                return "n";
            }
        }
    }
    private String ValidateDayPart(String DayPart){
        //DayPart can be d for day or n for night - default to d
        if (DayPart.startsWith("n")){
            return "n";
        }else{
            return "d";
        }
    }
    
}
