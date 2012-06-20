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
    private Object APIInstance = null;
    
    public WeatherAPI(String APIType) {
        setAPIType(APIType);
        this.APIType = getAPIType();
    }
    public WeatherAPI() {
        this.APIType = getAPIType();
        InitAPI();
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
    public String getAPITypeName() {
        String tAPIType = util.GetOptionName(Const.WeatherProp, "APIType", APITypes.GOOGLE.toString());
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            return "Google Weather";
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            return "Weather.com";
        }else{
            return "Google Weather";
        }
    }

    private void setAPIType(APITypes APIType) {
        APITypes currAPIType = getAPIType();
        if (!APIType.equals(currAPIType)){
            //change the type
            util.SetOption(Const.WeatherProp, "APIType", APIType.toString());
            //init the weather instance for the new type
            InitAPI();
        }
    }
    public void setAPIType(String tAPIType) {
        APITypes tempAPIType = APITypes.GOOGLE;
        if (tAPIType.equals(APITypes.GOOGLE.toString())){
            tempAPIType = APITypes.GOOGLE;
        }else if (tAPIType.equals(APITypes.WEATHERCOM.toString())){
            tempAPIType = APITypes.WEATHERCOM;
        }
        setAPIType(tempAPIType);
    }
    
    private void InitAPI(){
        if (APIType.equals(APITypes.WEATHERCOM)){
            APIInstance = WeatherDotCom.getInstance();
        }else{
            //default to GOOGLE
            APIInstance = GoogleWeather.getInstance();
        }
    }
    
}
