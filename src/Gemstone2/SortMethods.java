/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Gemstone2;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author SBANTA
 * @author JUSJOKEN
 * - 9/29/2011 - minor code flow updating, log message changes for testing
 * - 04/04/2012 - updated for Gemstone
 */
public class SortMethods {

    static private final Logger LOG = Logger.getLogger(SortMethods.class);

    public static String Title = "Gemstone_MetadataCalls_GetSortTitle";
    public static String AiringDate="Gemstone_MetadataCalls_GetOriginalAirDate";
    public static String RecordingDate="GetAiringStartTime";
    public static String WatchedDate = "GetRealWatchedEndTime";
    public static String Seasons = "Gemstone_MetadataCalls_GetSeasonNumber";
    public static String EpisodeTitle = "Gemstone_MetadataCalls_GetEpisodeTitle";
    public static String DateAdded="Gemstone_MetadataCalls_GetMediaFileID";

    public static String PropertyAdder ="Default";
    public static String PropertyDefault = Const.BaseProp + Const.PropDivider;

    public static String PropertyPrefix= Const.BaseProp + Const.PropDivider + PropertyAdder + Const.PropDivider;

    public static void main(String[] args){
        String Value=ClassFromString.GetSortMethod("AiringDate");
        System.out.println(Value);
    }

    public static String GetMainSortMethod(){
        LOG.trace("Getting Main sort Method");
        String CurrentMethod = sagex.api.Configuration.GetProperty(PropertyPrefix+"MainSortMethod","Title");
        LOG.info("Main sort method property="+CurrentMethod);
        String Return = ClassFromString.GetSortMethod(CurrentMethod);
        LOG.debug("Main sort ClassFromString="+Return);
        return Return;
    }

    public static String GetEpisodeSortMethod(){
        LOG.trace("Getting Episode sort Method");
        String CurrentMethod = sagex.api.Configuration.GetProperty(PropertyPrefix+"EpisodeSortMethod","EpisodeTitle");
        LOG.info("Episode sort method property="+CurrentMethod);
        String Return = ClassFromString.GetSortMethod(CurrentMethod);
        LOG.debug("Episode sort ClassFromString="+Return);
        return Return;
    }

    public static boolean IsTitleType(String SubSort){
        return SubSort.contains("Title");
    }

    public static List<String> GetAllSortMethods(String Type){
        List<String> Sorts = new ArrayList<String>();
        if(Type.contains("Main")){
            Sorts.add("Title");
            Sorts.add("AiringDate");
            Sorts.add("RecordingDate");
            Sorts.add("WatchedDate");
        }
        else{
            Sorts.add("EpisodeTitle");
            Sorts.add("AiringDate");
            Sorts.add("RecordingDate");
            Sorts.add("WatchedDate");
            Sorts.add("Seasons");
        }
        return Sorts;
    }

    public static int GetFinalSortMethod(String sortname){
        String[] FullTitle =sortname.split("&&");
        String Location = FullTitle[0];
        return Integer.parseInt(Location);
    }

}
