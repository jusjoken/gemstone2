/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

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

    private void setAPIType(APITypes APIType) {
        APITypes currAPIType = getAPIType();
        if (!APIType.equals(currAPIType)){
            //change the type
            util.SetOption(Const.WeatherProp, "APIType", APIType.toString());
        }
    }
    
    public void Init(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            if (wWeather==null){
                wWeather = WeatherDotCom.getInstance();
                gWeather = null;
            }
        }else{
            //default to GOOGLE
            if (gWeather==null){
                gWeather = GoogleWeather.getInstance();
                wWeather = null;
            }
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
    
}
