/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class WIcons {

    
    static private final Logger LOG = Logger.getLogger(WIcons.class);
    public static final String OptionNotFound = "Option not Found";
    public static final String WIconProp = "WIcons/";
    public static Map<String,String> IconsForDaytime = new HashMap<String, String>();
    public static Map<String,String> IconsForNighttime = new HashMap<String, String>();
    public static Boolean IconsBuilt = Boolean.FALSE;
    public static void BuildWeatherIconLists(){
        //start google Icons here
        AddIcon("sunny", "32", "31");
        AddIcon("mostly_sunny", "34", "33");
        AddIcon("partly_cloudy", "30", "29");
        AddIcon("mostly_cloudy", "28", "27");
        AddIcon("chance_of_storm", "37", "47");
        AddIcon("rain", "12", "12");
        AddIcon("chance_of_rain", "39", "45");
        AddIcon("chance_of_snow", "41", "46");
        AddIcon("cloudy", "26", "26");
        AddIcon("mist", "11", "11");
        AddIcon("storm", "35", "35");
        AddIcon("thunderstorm", "35", "35");
        AddIcon("chance_of_tstorm", "37", "47");
        AddIcon("sleet", "5", "5");
        AddIcon("snow", "16", "16");
        AddIcon("icy", "10", "10");
        AddIcon("dust", "19", "19");
        AddIcon("fog", "20", "20");
        AddIcon("smoke", "22", "22");
        AddIcon("haze", "21", "21");
        AddIcon("flurries", "14", "14");
        //start NWS Icons here - those begining with a "n" are the night icons (if one exists)
        //NWS is only used for Forecasts - so only the "day" or first entry is used
        AddIcon("nbkn", "27", "27");
        AddIcon("bkn", "28", "27");
        AddIcon("nra", "12", "12");
        AddIcon("ra", "12", "12");
        AddIcon("nskc", "31", "31");
        AddIcon("skc", "32", "31");
        AddIcon("nfew", "33", "33");
        AddIcon("few", "34", "33");
        AddIcon("nsct", "29", "29");
        AddIcon("sct", "30", "29");
        AddIcon("hi_nshwrs", "45", "45");
        AddIcon("hi_shwrs", "39", "45");
        AddIcon("novc", "26", "26");
        AddIcon("ovc", "26", "26");
        AddIcon("nrasn", "46", "46");
        AddIcon("rasn", "41", "46");
        AddIcon("sn", "16", "16");
        AddIcon("nsn", "16", "16");
        AddIcon("ntsra", "35", "35");
        AddIcon("tsra", "35", "35");
        AddIcon("nscttsra", "35", "35");
        AddIcon("scttsra", "35", "35");
        AddIcon("hi_tsra", "37", "37");
        AddIcon("hi_ntsra", "47", "47");
        AddIcon("nwind", "24", "24");
        AddIcon("wind", "23", "24");
        AddIcon("nfg", "20", "20");
        AddIcon("fg", "20", "20");
        AddIcon("cold", "15", "15");
        AddIcon("blizzard", "43", "43");
        AddIcon("ntor", "24", "24");
        AddIcon("tor", "23", "24");
        AddIcon("fzra", "5", "5");
        AddIcon("du", "19", "19");
        AddIcon("nshra", "12", "12");
        AddIcon("shra", "12", "12");
        AddIcon("nfu", "22", "22");
        AddIcon("fu", "22", "22");
        AddIcon("hot", "32", "31");
    }
    private static void AddIcon(String IconSource, String IconForDay, String IconForNight){
        IconsForDaytime.put(IconSource, IconForDay);
        IconsForNighttime.put(IconSource, IconForNight);
    }

    public static String ConvertURLtoCondition(String ConditionURL, Boolean ForceDay){
        String Condition = "";
        Integer tIcon = ConditionURL.lastIndexOf("/");
        if (tIcon==-1){
            return ConditionURL;
        }else{
            Condition = ConditionURL.substring(tIcon+1);
            Condition = Condition.replaceAll(".gif", "");
            Condition = Condition.replaceAll(".jpg", "");
            Condition = Condition.replaceAll(".png", "");
            //remove any % that are part of the image string
            if (Condition.contains("0")){
                Condition = Condition.replaceAll("10", "");
                Condition = Condition.replaceAll("20", "");
                Condition = Condition.replaceAll("30", "");
                Condition = Condition.replaceAll("40", "");
                Condition = Condition.replaceAll("50", "");
                Condition = Condition.replaceAll("60", "");
                Condition = Condition.replaceAll("70", "");
                Condition = Condition.replaceAll("80", "");
                Condition = Condition.replaceAll("90", "");
            }
            
            String tCondition = GetWeatherIcon(Condition, ForceDay);
            //LOG.debug("ConvertURLtoCondition: Condition '" + Condition + "' tCondition '" + tCondition + "' URL '" + ConditionURL + "'");
            if (tCondition.equals(Condition)){
                return ConditionURL;
            }else{
                return tCondition;
            }
            
        }
    }
    
    public static String GetWeatherIconURLDay(String ConditionURL){
        String tCondition = ConvertURLtoCondition(ConditionURL, Boolean.TRUE);
        if (tCondition.equals(ConditionURL)){
            LOG.debug("GetWeatherIconURLDay: unhandled url - please report '" + ConditionURL + "'");
            return ConditionURL;
        }else{
            return GetWeatherPath() + tCondition + ".png";
        }
    }
    public static String GetWeatherIconURL(String ConditionURL){
        //System.out.println("WIcons: ConditionURL '" + ConditionURL + "'");
        String tCondition = ConvertURLtoCondition(ConditionURL, Boolean.FALSE);
        if (tCondition.equals(ConditionURL)){
            LOG.debug("GetWeatherIconURL: unhandled url - please report '" + ConditionURL + "'");
            return ConditionURL;
        }else{
            return GetWeatherPath() + tCondition + ".png";
        }
    }
    public static String GetWeatherIconByNumber(String ConditionNumber){
        return GetWeatherPath() + ConditionNumber + ".png";
    }

    public static String GetWeatherIconNoURLDay(String ConditionURL){
        String tCondition = ConvertURLtoCondition(ConditionURL, Boolean.TRUE);
        if (tCondition.equals(ConditionURL)){
            LOG.debug("GetWeatherIconNoURLDay: unhandled url - please report '" + ConditionURL + "'");
            return "-1";
        }else{
            return tCondition;
        }
    }
    public static String GetWeatherIconNoURL(String ConditionURL){
        String tCondition = ConvertURLtoCondition(ConditionURL, Boolean.FALSE);
        if (tCondition.equals(ConditionURL)){
            LOG.debug("GetWeatherIconNoURL: unhandled url - please report '" + ConditionURL + "'");
            return "-1";
        }else{
            return tCondition;
        }
    }

    public static String GetWeatherIconDay(String Condition){
        return GetWeatherIcon(Condition, Boolean.TRUE);
    }
    public static String GetWeatherIcon(String Condition){
        return GetWeatherIcon(Condition, Boolean.FALSE);
    }
    public static String GetWeatherIcon(String Condition, Boolean ForceDay){
        if (!IconsBuilt){
            BuildWeatherIconLists();
            IconsBuilt = Boolean.TRUE;
        }
        String returnIcon = Condition;
        String DefaultIcon = "";
        if (IsDaytime() || ForceDay){
            if (IconsForDaytime.containsKey(Condition)){
                DefaultIcon = IconsForDaytime.get(Condition);
            }else{
                DefaultIcon = Condition;
            }
            returnIcon = util.GetProperty(WIconProp + Condition + "/" + "DayIcon", DefaultIcon );
        }else{
            if (IconsForNighttime.containsKey(Condition)){
                DefaultIcon = IconsForNighttime.get(Condition);
            }else{
                DefaultIcon = Condition;
            }
            returnIcon = util.GetProperty(WIconProp + Condition + "/" + "NightIcon", DefaultIcon );
        }
        //LOG.debug("GetWeatherIcon: returning '" + returnIcon + "' IsDaytime '" + IsDaytime() + "' ForceDay '" + ForceDay + "'");
        return returnIcon;
    }

    public static Boolean IsDaytime(){
        Calendar myCalendar = Calendar.getInstance();
        Integer currentHour = myCalendar.get(Calendar.HOUR_OF_DAY);
        String PropLocation = Const.BaseProp + Const.PropDivider + Const.WeatherProp + Const.PropDivider;
        Integer DayStartHour = util.GetPropertyAsInteger(PropLocation + "DayStartHour", 7);
        Integer DayEndHour = util.GetPropertyAsInteger(PropLocation + "DayEndHour", 19);
        if (currentHour>=DayStartHour && currentHour<DayEndHour){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public static String GetWeatherPath(){
        if (util.GetTrueFalseOption(Const.WeatherProp, "UseGemstoneWeatherIcons", Boolean.TRUE)){
            return "Themes\\Gemstone\\Weather\\Images\\";
        }else{
            return "WeatherIcons\\Images\\";
        }
    }
    
}
