/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.views.ViewFolder;

/**
 *
 * @author SBANTA
 * @author JUSJOKEN
 * - 10/01/2011 - added logging and changes to Category Filters
 * - 04/04/2012 - updated for Gemstone
 */
public class MetadataCalls {

    static private final Logger LOG = Logger.getLogger(MetadataCalls.class);
    public static String PlayonDirectory = sagex.api.Configuration.GetServerProperty("PlayonPlayback/ImportDirectory", "/SageOnlineServicesEXEs\\UPnPBrowser\\PlayOn") + "\\TV\\";
    public static String HuluFile = "Quicktime[H.264/50Kbps 480x368@24fps]";
    public static String NetflixFile = "Quicktime[H.264/50Kbps 480x368@25fps]";

    public static Integer GetSeasonNumber(Object MediaObject) {
        return sagex.api.ShowAPI.GetShowSeasonNumber(MediaObject);
    }


       public static String GetEpisodeTitle(Object MediaObject){
      if(MediaObject.getClass().equals(Dividers.SageClass))
	{
       String Title  = sagex.api.ShowAPI.GetShowEpisode(MediaObject);
       if(Title.equals("")){
		return sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "EpisodeTitle");}
       return Title;
	}
      else{
       return MediaObject.toString();}}


    // Gemstone_MetadataCalls_DisplaySeasonEpisode
    public static String DisplaySeasonEpisode(Object MediaObject, String Property) {
        //ensure there is a valid SE otherwise return a blank string
        if (MediaObject==null){
            return "";
        }
        MediaObject = phoenix.media.GetSageMediaFile(MediaObject);
        if (GetSeasonNumber(MediaObject)==0 || GetEpisodeNumber(MediaObject)==0){
            return "";
        }
    	if(Property.equals("S1E01")) {
    		return "S"+ GetSeasonNumber(MediaObject) + "E" + GetEpisodeNumberPad(MediaObject);
    	} else if(Property.equals("S01E01")) {
    		return "S"+ GetSeasonNumberPad(MediaObject) + "E" + GetEpisodeNumberPad(MediaObject);
    	} else if(Property.equals("1x01")) {
    		return GetSeasonNumber(MediaObject) + "x" + GetEpisodeNumberPad(MediaObject);
    	} else if(Property.equals("E01")) {
    		return "E" + GetEpisodeNumberPad(MediaObject);
    	} else if(Property.equals("1")) {
    		return "" + GetEpisodeNumber(MediaObject);	
    	}else if(Property.equals("None")) {
    		return "";	
    	} else {
    		return "S"+ GetSeasonNumber(MediaObject) + "E" + GetEpisodeNumberPad(MediaObject);	
    	}
    }
    
    public static String GetSeasonNumberDivider(Object MediaObject) {
        return "Season " + GetSeasonNumber(MediaObject);

    }

    public static String GetSortTitle(Object MediaObject) {
        String Title = GetMediaTitle(MediaObject);
        if (Title==null || Title.equals("")) {
            return "000";
        }
        return sagex.api.Database.StripLeadingArticles(Title.toLowerCase());
    }

    public static Integer GetEpisodeNumber(Object MediaObject) {
        return sagex.api.ShowAPI.GetShowEpisodeNumber(MediaObject);
    }

    public static String GetEpisodeNumberPad(Object MediaObject) {
        int en = GetEpisodeNumber(MediaObject);
        return String.format("%02d", en);


    }

    public static String GetSeasonNumberPad(Object MediaObject) {
        int en = GetSeasonNumber(MediaObject);
        return String.format("%02d", en);
    }

    public static String GetFanartTitle(Object MediaObject){
        String Title=sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTitle");
        if (Title==null || Title.equals("")){
            Title=GetMediaTitle(MediaObject);
        }
        return Title;
    }

    //used for searching
    public static String GetTitleLowerCase(Object MediaObject) {
        return GetTitle(MediaObject).toLowerCase();
    }
    
    public static String GetMediaTitle(Object MediaObject) {
        String Title ="";


        if (Title==null || Title.equals("")) {
            Title = sagex.api.ShowAPI.GetShowTitle(MediaObject);
        }
        if (Title==null || Title.equals("")) {
            Title = sagex.api.MediaFileAPI.GetMediaTitle(MediaObject);
        }
        if (Title==null || Title.equals("")) {
            Title = sagex.api.AiringAPI.GetAiringTitle(MediaObject);
        }
        if (Title==null || Title.equals("")) {
            return Const.UnknownName;  // was "Unkown"
        }
        return Title;
    }

    public static int GetShowDuration(Object MediaObject) {
        long duration = sagex.api.ShowAPI.GetShowDuration(MediaObject);
        int durationint = (int) duration;
        if (durationint > 0) {
            durationint = durationint / 60000;
        }
        return durationint;

    }

    public static boolean IsImportedNotPlayon(Object MediaObject) {
        return !IsPlayonFile(MediaObject) && IsImportedTV(MediaObject);
    }

    public static boolean IsPlayonFile(Object MediaObject) {
    	if (sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "Copyright").contains("PlayOn")) {
        	return true;
    	}
    	return false;
    }

    public static String GetPlayonFileType(Object MediaObject) {
    	String Comment = sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "Copyright");
    	String[] SplitString = Comment.split(",");
    	if (SplitString.length == 2)
    	{
    		return SplitString[1];    		
    	}
        return "";
    }

//   public static String GetEpisodeTitle(Object MediaObject){
//      if(MediaObject.getClass().equals(Dividers.SageClass))
//	{
//       String Title  = sagex.api.ShowAPI.GetShowEpisode(MediaObject);
//       if(Title.equals("")){
//		return sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "EpisodeTitle");}
//       return Title;
//	}
//      else{
//       return MediaObject.toString();}}
//     public static String GetEpisodeTitleDivider(Object MediaObject)
//	{
//         String EpisodeTitle=GetEpisodeTitle(MediaObject);
//         if(EpisodeTitle.equals(null)||EpisodeTitle.length()<1){
//         return "Unknown";}
//                 return GetEpisodeTitle(MediaObject).substring(0,1).toUpperCase();
//	}
    public static String GetMovieReleaseYear(Object MediaObject) {

        //System.out.println("No Original Air Date");
        return sagex.api.ShowAPI.GetShowYear(MediaObject);

    }

    public static int GetMediaFileID(Object MediaObject) {
        return sagex.api.MediaFileAPI.GetMediaFileID(MediaObject);
    }

    public static long GetMovieOriginalAirDate(Object MediaObject) {
        String s1 = sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "OriginalAirDate");
        if (s1.length() == 0) {
            //System.out.println("No Original Air Date");
            return 0;
        }
//        return DateConverter.GetDateFromLong(Long.parseLong(s1));
       return Long.parseLong(s1);
    }

    public static long GetOriginalAirDate(Object MediaObject) /*returns the OriginalAiringDate as a long (in java date format) gathered from the metadta
     * 0 if it does not exist or catches an error
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile
     */ {
        String s1 = sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "OriginalAirDate");
        //System.out.println("OriginalAirDateString = '" + OrigAirDateString + "'");

        if (s1.length() == 0) {
            //System.out.println("No Original Air Date");
            return 0;
        } //System.out.println("OriginalAirDateString = '" + OrigAirDateString + "'");
        else {
//                        LOG.debug("OriginalAiringDate="+s1);
            Long l1 = Long.parseLong(s1);

//			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return l1 + GetSeasonEpisodeNumber(MediaObject);
        }





    }

    public static int GetSeasonEpisodeNumber(Object MediaObject) /*
     * returns the SeasonEpisode Number as an integer
     * or 0 if Season Number does not exist
     * 101 (s01e01)
     * 201 (s02e01)
     * 1010 (s10e10)
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */ {
        int sn = GetSeasonNumber(MediaObject);
        int en = GetEpisodeNumber(MediaObject);

        if (sn == 0) {
            return 0;
        } else {
            return sn * 100 + en;
        }
    }

    public static String GetMediaType(Object MediaObject) {
        return sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaType");
    }

    public static boolean IsMediaTypeTV(Object MediaObject) {
        String Type = sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaType");
        if (Type.contains("TV") || sagex.api.MediaFileAPI.IsTVFile(MediaObject)) {
            return true;
        } else {

            return false;
        }
    }

    public static boolean IsImportedTV(Object MediaObject) {

        if (IsMediaTypeTV(MediaObject) && !sagex.api.MediaFileAPI.IsTVFile(MediaObject)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean IsRecordedTV(Object MediaObject) {
        return sagex.api.MediaFileAPI.IsTVFile(MediaObject);
    }

    public static Long GetDateRecorded(Object MediaObject) {
        return sagex.api.AiringAPI.GetAiringStartTime(MediaObject);

    }

    //return the first category ignoring the Movie and Film categories. Return unknow if none
    public static String GetShowCategory(Object MediaObject) {
        return GetAllShowCategories(MediaObject).get(0);
    }
    
//    public static String GetShowCategory(Object MediaObject) {
//        String Cat = sagex.api.ShowAPI.GetShowCategory(MediaObject);
//        LOG.debug("GetShowCategory = '" + Cat + "'");
//        if (Cat.startsWith("Movie") && Cat.contains("/")) {
//
//            Cat = Cat.substring(Cat.indexOf("/"));
//        }
//
//        if (Cat.equals("")) {
//            return "unknown";
//        }
//        if (Cat.contains("and")) {
//            return Cat.substring(0, Cat.indexOf("and") - 1);
//        }
//        if (Cat.contains(",")) {
//
//            return Cat.substring(0, Cat.indexOf(","));
//        }
//        return Cat;
//
//    }

//    public static String[] GetShowCategories(Object MediaObject){
//    return sagex.api.ShowAPI.GetShowCategory(MediaObject).split(",");}

    
    //     public static String GetTimeAdded(Object Title) {
//    // Check to see if date variables have been set
//    if(!DateConverter.IsDateVariableSet){
//    LOG.trace("DateVariablesNot set Go ahead and set them");
//    String First =sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupFirstTime","10080");
//    String Second = sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupSecondTime","20160");
//    String Third = sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupThirdTime","43200");
//    String Fourth = sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupFourthTime","86400");
//    Long LFirst = java.lang.Long.parseLong(First);
//    LFirst = LFirst *60*1000;
//    Long LSecond = java.lang.Long.parseLong(Second);
//    LSecond = LSecond*60*1000;
//    Long LThird = java.lang.Long.parseLong(Third);
//    LThird = LThird*60*1000;
//    Long LFourth = java.lang.Long.parseLong(Fourth);
//    LFourth = LFourth *60*1000;
//        Long CurrTime = System.currentTimeMillis();
//    DateConverter.FirstDateGroup =CurrTime-LFirst;
//    DateConverter.SecondDateGroup=CurrTime-LSecond;
//    DateConverter.ThirdDateGroup=CurrTime-LThird;
//    DateConverter.FourthDateGroup=CurrTime-LFourth;
//    DateConverter.FifthDateGroup =CurrTime-(Long.parseLong(sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupFifthTime","86401"))*60*1000);
//    DateConverter.IsDateVariableSet=true;}
//    LOG.trace("DateVariables Set to="+DateConverter.FirstDateGroup+":");
//    LOG.trace("DateVariables Set to="+DateConverter.SecondDateGroup+":");
//    LOG.trace("DateVariables Set to="+DateConverter.ThirdDateGroup+":");
//    LOG.trace("DateVariables Set to="+DateConverter.FourthDateGroup+":");
//    LOG.trace("DateVariables Set to="+DateConverter.FifthDateGroup+":");
//
//    Long DateAdded = (Long) ClassFromString.GetDateClass("GetOriginalAirDate", Title);
//    if(DateAdded>=DateConverter.FirstDateGroup){
//    return sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupFirstName","New");}
//    else if (DateAdded>=DateConverter.SecondDateGroup){
//    return sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupSecondName","Last Week");}
//    else if (DateAdded>=DateConverter.ThirdDateGroup){
//    return sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupThirdName","30 days");}
//   else if (DateAdded>=DateConverter.FourthDateGroup){
//    return sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupFourthName","60 days");}
//   else{
//
//    return sagex.api.Configuration.GetProperty(SortMethods.PropertyPrefix+"DateGroupFifthName","Older");}
//
//        }


//    public static ArrayList<String> GetAllShowCategories(Object MediaObject) {
//        String[] Cats = sagex.api.ShowAPI.GetShowCategory(MediaObject).split(",");
//        
//        ArrayList<String> AllCats = new ArrayList<String>();
//        for (String curr : Cats) {
//            if (curr.contains("and")) {
//                curr = curr.trim();
//                String[] andsplit = curr.split("and");
//                String cat1 = andsplit[0];
//                cat1 = cat1.trim();
//                String cat2 = andsplit[0];
//                cat1 = cat2.trim();
//                System.out.print("Adding Category==" + cat1 + "!!" + cat2 + "!!");
//                AllCats.add(cat1);
//                AllCats.add(cat2);
//            } else if (curr.equals("")) {
//                AllCats.add("unknown");
//            } else if (curr.startsWith(" ")) {
//                AllCats.add(curr.substring(1));
//            } else {
//                System.out.println("Adding single categories=" + curr + "!!");
//                AllCats.add(curr);
//            }
//        }
//        return AllCats;
//    }

    public static String GetShowCategoriesString(Object MediaObject){
        String retString = "";
        for (String Cat: GetAllShowCategories(MediaObject)){
            if (retString.equals("unknown")){
                //do not add the unknown category
            }else if (retString.equals("")){
                retString = Cat;
            }else{
                retString = retString + " / " + Cat;
            }
        }
        return retString;
    }
    
    //get a list of all the categories with Movies/Film removed and unknown assigned if no category exists
    public static ArrayList<String> GetAllShowCategories(Object MediaObject) {
        //LOG.debug("====== " + GetMediaTitle(MediaObject) + "========");
        //LOG.debug("GetShowCategoriesString = '" + sagex.api.ShowAPI.GetShowCategoriesString(MediaObject) + "'");
        String SplitChars = "[,;/]";
        String[] Cats = sagex.api.ShowAPI.GetShowCategoriesString(MediaObject).split(SplitChars);
        
        ArrayList<String> AllCats = new ArrayList<String>();
        for (String curr : Cats) {
            curr = curr.trim();
            if (curr.contains(" and ")) {
                //curr = curr.trim();
                String[] andsplit = curr.split("and");
                String cat1 = andsplit[0];
                cat1 = cat1.trim();
                String cat2 = andsplit[1];
                cat2 = cat2.trim();
                //LOG.debug("Adding Category ='" + cat1 + "'");
                AllCats.add(cat1);
                //LOG.debug("Adding Category ='" + cat2 + "'");
                AllCats.add(cat2);
            } else if (curr.toLowerCase().equals("movie") || curr.toLowerCase().equals("film")) {
                //do not add as we want to skip these
                //LOG.debug("Skipping 'movie or film' category");
            } else if (curr.equals("")) {
                //LOG.debug("Adding 'unknown' category");
                AllCats.add("unknown");
            } else {
                //LOG.debug("Adding single categories = '" + curr + "'");
                AllCats.add(curr);
            }
        }
        return AllCats;
    }

    public static String GetGenresasString(IMediaResource MediaObject, String Separator){
        String Value = "";
        IMediaResource imediaresource = MediaObject;
        if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
            imediaresource = ImageCache.GetChild(imediaresource, Boolean.FALSE);
        }
        if (imediaresource==null){
            return Value;
        }
        List<String> ListValue = phoenix.metadata.GetGenres(imediaresource);
        if (ListValue.size()>0){
            for (String ListItem : ListValue){
                if (ListItem.equalsIgnoreCase("movie")||ListItem.equalsIgnoreCase("film")){
                    //skip these
                }else{
                    if (Value.equals("")){
                        Value = ListItem;
                    }else{
                        Value = Value + Separator + ListItem;
                    }
                }
            }
        }
        return Value;
    }
    //Convenience method that will convert the incoming object parameter to a IMediaResource type 
    public static String GetGenresasString(Object MediaObject, String Separator){
        return GetGenresasString(Source.ConvertToIMR(MediaObject), Separator);
    }
    
    public static int AiredAgeInDays(Object MediaObject) 
    {
    	return (int)(sagex.api.Utility.Time() - sagex.api.AiringAPI.GetAiringStartTime(MediaObject))/(86400*1000);
    }
    
    public static int RecordedAgeInDays(Object MediaObject) 
    {
    	return (int)(sagex.api.Utility.Time() - sagex.api.AiringAPI.GetRealWatchedStartTime(MediaObject))/(86400*1000);
    }

    //return a consistent Title dependent on the media item and the type
    public static String GetTitle(IMediaResource imediaresource){
        if (imediaresource==null){
            return "";
        }
        if (imediaresource.toString().contains("BlankItem")){
            return "";
        }
        String sType = Source.GetSpecialType(imediaresource);
        String tTitle = imediaresource.getTitle();
        if (sType.equals("tv")){  //return the episode name
            String eTitle = phoenix.metadata.GetEpisodeName(imediaresource);
            if (eTitle==null){
                return tTitle;
            }else if (eTitle.equals("")){
                return tTitle;
            }else{
                return eTitle;
            }
        }
        //see if there is a Disc or Part number to append
        String Disc = sagex.api.MediaFileAPI.GetMediaFileMetadata(imediaresource, "DiscNumber");
        if (Disc.equals("0") || Disc.isEmpty()){
            //do not append the Disc/Part
        }else{
            tTitle = tTitle + " (" + Disc + ")";
        }
        return tTitle;
    }
    public static String GetTitle(Object imediaresource){
        return GetTitle(Source.ConvertToIMR(imediaresource));
    }

    //Series related metadata
    public static String GetSeriesTitle(Object IMR){
        IMediaResource imediaresource = Source.GetTVIMediaResource(IMR);
        if (imediaresource!=null){ 
            String tReturn = phoenix.series.GetTitle(phoenix.media.GetSeriesInfo(phoenix.media.GetMediaFile(imediaresource)));
            //LOG.debug("GetSeriesTitle: series.GetTitle returned '" + tReturn + "' for '" + imediaresource + "'");
            if (tReturn==null){
                LOG.debug("GetSeriesTitle: null found so using GetTitle instead '" + GetTitle(imediaresource) + "' for '" + imediaresource + "'");
                return GetTitle(imediaresource);
            }else if (tReturn.isEmpty()){
                LOG.debug("GetSeriesTitle: empty found so using GetTitle instead '" + GetTitle(imediaresource) + "' for '" + imediaresource + "'");
                return GetTitle(imediaresource);
            }else{
                //LOG.debug("GetSeriesTitle: good return value found '" + tReturn + "' for '" + imediaresource + "'");
                return tReturn;
            }
        }
        return "";
    }

    public static String GetUserCategory(Object IMR){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                return "FOLDER";
            }else{
                String tCat = sagex.api.MediaFileAPI.GetMediaFileMetadata(IMR, "UserCategory");
                if (tCat==null){
                    return "null found";
                }else{
                    return tCat;
                }
            }
        }
        return "";
    }
    
    public static String GetAiredYear(Object IMR){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                //get the range of years
                ViewFolder Folder = (ViewFolder) imediaresource;
                Object[] tList = (Object[]) sagex.api.Database.Sort(phoenix.media.GetAllChildren(Folder), false, "phoenix_metadata_GetOriginalAirDate");
                String firstYear = getAiredYear((IMediaResource)tList[0]);
                String lastYear = getAiredYear((IMediaResource)tList[tList.length-1]);
                if (firstYear.equals(lastYear)){
                    return "Aired in " + firstYear;
                }else{
                    return "Aired " + firstYear + " - " + lastYear;
                }
            }else{
                return "Aired in " + getAiredYear(imediaresource);
            }
        }
        return "";
    }
    private static String getAiredYear(IMediaResource imediaresource){
        //get the aired Year
        Date tReturn = phoenix.metadata.GetOriginalAirDate(imediaresource);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
        return simpleDateformat.format(tReturn);
    }

    //Series related metadata
    public static String GetRunningInfo(Object IMR){
        IMediaResource imediaresource = Source.GetTVIMediaResource(IMR);
        if (imediaresource!=null){ 
            String tReturn = phoenix.series.GetFinaleDate(phoenix.media.GetSeriesInfo(phoenix.media.GetMediaFile(imediaresource)));
            //LOG.debug("GetRunningInfo: GetFinaleDate returned '" + tReturn + "' for '" + imediaresource + "'");
            if (tReturn==null || tReturn.isEmpty()){
                return "Series continuing";
            }else{
                return "Series ended";
            }
        }
        return "";
    }

    //Series related metadata
    public static String GetNetwork(Object IMR){
        IMediaResource imediaresource = Source.GetTVIMediaResource(IMR);
        if (imediaresource!=null){ 
            String tReturn = phoenix.series.GetNetwork(phoenix.media.GetSeriesInfo(phoenix.media.GetMediaFile(imediaresource)));
            //LOG.debug("GetNetwork: GetGetNetwork returned '" + tReturn + "' for '" + imediaresource + "'");
            if (tReturn==null || tReturn.isEmpty()){
                return "";
            }else{
                return tReturn;
            }
        }
        return "";
    }
    
    public static String GetRated(Object IMR){
        IMediaResource imediaresource = Source.GetChildIMediaResource(IMR);
        if (imediaresource!=null){ 
            String tReturn = phoenix.metadata.GetRated(imediaresource);
            //LOG.debug("GetRated: GetRated returned '" + tReturn + "' for '" + imediaresource + "'");
            if (tReturn==null || tReturn.isEmpty() || tReturn.equals("null")){
                return "";
            }else{
                return tReturn;
            }
        }
        return "";
    }
    
    public static String GetDescription(Object IMR){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                imediaresource = ImageCache.GetChild(imediaresource, Boolean.FALSE);
            }
            if (imediaresource!=null){ 
                return phoenix.metadata.GetDescription(imediaresource);
            }
        }
        return "";
    }

    //Series related metadata
    public static String GetSeriesDescription(Object IMR){
        IMediaResource imediaresource = Source.GetTVIMediaResource(IMR);
        if (imediaresource!=null){ 
            String tReturn = phoenix.series.GetDescription(phoenix.media.GetSeriesInfo(phoenix.media.GetMediaFile(imediaresource)));
            //LOG.debug("GetSeriesDescription: GetDescription returned '" + tReturn + "' for '" + imediaresource + "'");
            if (tReturn==null || tReturn.isEmpty()){
                return "";
            }else{
                return tReturn;
            }
        }
        return "";
    }
    
    //Series related metadata
    public static Boolean IsHDTV(Object IMR){
        IMediaResource imediaresource = Source.GetTVIMediaResource(IMR);
        if (imediaresource!=null){ 
            Boolean tReturn = phoenix.metadata.IsHDTV(imediaresource);
            //LOG.debug("IsHDTV: IsHDTV returned '" + tReturn + "' for '" + imediaresource + "'");
            return tReturn;
        }
        return Boolean.FALSE;
    }

    public static Boolean IsWatched(Object IMR){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                //see if ALL the Children are watched by seeing if we find any unwatched ones
                ViewFolder Folder = (ViewFolder) imediaresource;
                List Children = phoenix.media.GetAllChildren(Folder);
                //Object[] tList = (Object[]) sagex.api.Database.Sort(Children, true, "phoenix_metadata_GetOriginalAirDate");
                //Get all the Watched items
                Integer WatchedCount = sagex.api.Utility.Size(sagex.api.Utility.GetSubgroup(sagex.api.Database.GroupByMethod(Children,"phoenix_media_IsWatched"),true));
                //LOG.debug("IsWatched: FOLDER - Watched '" + WatchedCount + "' of '" + Children.size() + "' Items");
                if (Children.size()==WatchedCount){
                    return Boolean.TRUE;
                }
            }else{
                return imediaresource.isWatched();
            }
        }
        return Boolean.FALSE;
    }

    public static void SetWatched(Object IMR){
        ChangeWatched(IMR, Boolean.TRUE);
    }
    public static void ClearWatched(Object IMR){
        ChangeWatched(IMR, Boolean.FALSE);
    }
    public static void ChangeWatched(Object IMR){
        ChangeWatched(IMR, !IsWatched(IMR));
    }
    public static void ChangeWatched(Object IMR, Boolean Value){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                //see if ALL the Children are watched by seeing if we find any unwatched ones
                ViewFolder Folder = (ViewFolder) imediaresource;
                for (Object child:phoenix.media.GetAllChildren(Folder)){
                    if (child instanceof IMediaResource){
                        IMediaResource iChild = (IMediaResource) child;
                        iChild.setWatched(Value);
                    }
                }
            }else{
                imediaresource.setWatched(Value);
            }
        }
    }

    public static Boolean IsArchived(Object IMR){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                //see if ALL the Children are archived by seeing if we find any unarchived ones
                ViewFolder Folder = (ViewFolder) imediaresource;
                List Children = phoenix.media.GetAllChildren(Folder);
                //Get all the Archived items
                Integer ArchivedCount = sagex.api.Utility.Size(sagex.api.Utility.GetSubgroup(sagex.api.Database.GroupByMethod(Children,"phoenix_media_isLibraryFile"),true));
                if (Children.size()==ArchivedCount){
                    return Boolean.TRUE;
                }
            }else{
                return imediaresource.isLibraryFile();
            }
        }
        return Boolean.FALSE;
    }

    public static void SetArchived(Object IMR){
        ChangeArchived(IMR, Boolean.TRUE);
    }
    public static void ClearArchived(Object IMR){
        ChangeArchived(IMR, Boolean.FALSE);
    }
    public static void ChangeArchived(Object IMR){
        ChangeArchived(IMR, !IsArchived(IMR));
    }
    public static void ChangeArchived(Object IMR, Boolean Value){
        IMediaResource imediaresource = Source.ConvertToIMR(IMR);
        if (imediaresource!=null){ 
            if (phoenix.media.IsMediaType( imediaresource , "FOLDER" )){
                //see if ALL the Children are archived by seeing if we find any unarchived ones
                ViewFolder Folder = (ViewFolder) imediaresource;
                for (Object child:phoenix.media.GetAllChildren(Folder)){
                    if (child instanceof IMediaResource){
                        IMediaResource iChild = (IMediaResource) child;
                        iChild.setLibraryFile(Value);
                    }
                }
            }else{
                imediaresource.setLibraryFile(Value);
            }
        }
    }

}
