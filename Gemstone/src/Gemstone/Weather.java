/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

/**
 *
 * @author jusjoken
 * public Gemstone single Weather instance to use across the app and all extenders
 */
public class Weather {
    private static WeatherAPI GemstoneWeather = null;

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
    public Boolean IsWeatherDotCom(){
        return GemstoneWeather.IsWeatherDotCom();
    }
    public Boolean IsGoogleWeather(){
        return GemstoneWeather.IsGoogleWeather();
    }
    //get a name for menu items/settings
    public String GetTypeName() {
        return GemstoneWeather.GetTypeName();
    }
    //change to the next valid WeatherAPI type
    public void NextType() {
        GemstoneWeather.NextType();
        GemstoneWeather.Init();
        GemstoneWeather.Update();
    }
    
}
