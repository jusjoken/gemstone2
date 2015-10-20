package Gemstone2;

import java.util.Arrays;
import java.util.Random;
        
import org.apache.log4j.Logger;
import sagex.api.Database;
import sagex.api.MediaFileAPI;
import sagex.api.AiringAPI;
import sagex.api.ShowAPI;
import sagex.api.PlaylistAPI;
import sagex.api.Utility;

/**
 *
 * @author SBANTA
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */

public class airing
{
    static private final Logger LOG = Logger.getLogger(airing.class);
    public static String GetAiringTitle(Object MediaObject)
    //Returns the airing title for the MediaObject.  If the MediaObject is a TV file it returns GetAiringTitle().
    //If it is not a TV file it returns the "MediaTitle" from the custom metadata fields. Returns "Unknown"
    //if the title is not known.
    {
        //String MediaTitle = new String();	
        String MediaTitle;	
        if (MediaFileAPI.IsTVFile(MediaObject)) {
            MediaTitle = AiringAPI.GetAiringTitle(MediaObject);		
        }
        else {
            MediaTitle = MetadataCalls.GetMediaTitle(MediaObject);
        }	
        if (MediaTitle.length() == 0) {
            return "Unknown";
        }
        else {
            return MediaTitle;
        }
    }		
	
//	public static String GetShowEpisode(Object MediaObject)
//	{
//		if (MediaFileAPI.IsTVFile(MediaObject)) {
//			return ShowAPI.GetShowEpisode(MediaObject);
//		}
//		else {
//			String EpisodeTitle = MetadataCalls.GetEpisodeTitle(MediaObject);
//
//			if (EpisodeTitle.length() == 0) {
//				return MediaFileAPI.GetMediaTitle(MediaObject);
//			}
//			return EpisodeTitle;
//		}
//	}

    public static String GetShowEpisode(Object MediaObject){
        if (MediaObject==null){
            return "";
        }
        MediaObject = phoenix.media.GetSageMediaFile(MediaObject);
        return ShowAPI.GetShowEpisode(MediaObject);
    }
    
    public static String GetShowDescription(Object MediaObject){
        if (MediaObject==null){
            return "";
        }
        MediaObject = phoenix.media.GetSageMediaFile(MediaObject);
        return ShowAPI.GetShowDescription(MediaObject);
    }
    
    public static long GetOriginalAiringDate(Object MediaObject) 
    {
        if (MediaFileAPI.IsTVFile(MediaObject)){
            return ShowAPI.GetOriginalAiringDate(MediaObject);
        } else{
            return MetadataCalls.GetOriginalAirDate(MediaObject);
        }
    }
	
    public static String GetAiringTitlePostpend(Object MediaObject)
    {
        String s1 = airing.GetAiringTitle(MediaObject);
        String s2 = s1.toLowerCase();

        if (s2.startsWith("the ") || s2.startsWith("the.") || s2.startsWith("the_"))
        {
            s1 = s1.substring(4) + ", The";
        }
        if (s2.startsWith("a ") || s2.startsWith("a.") || s2.startsWith("a_"))
        {
            s1 = s1.substring(2) + ", A";
        }
        if (s2.startsWith("an ") || s2.startsWith("an.") || s2.startsWith("an_"))
        {
            s1 = s1.substring(3) + ", An";
        }
        return s1;
    }
	
    public static Object MakePlaylist(Object MediaObjects, String NewPlaylistName)
    /* 
     * returns a Sage Playlist object containing the airings in MediaObjects
     * If NewPlaylistName already exists, it will be removed without prompt and recreated.
     * 
     * @param MediaObjects, a sage MediaFile, Airing, or Show Object in an Array, list, or vector
     * @param NewPlaylistName, string for the title of the new playlist
     */
    {
        LOG.debug("MakePlaylist: START");

        Object[] AllPlaylists = PlaylistAPI.GetPlaylists();

        Object[] MediaObjectsArray = FanartCaching.toArray(MediaObjects);

        for (Object TempPlaylist : AllPlaylists)
        {
            String TempPlaylistName = PlaylistAPI.GetName(TempPlaylist);
            //System.out.println("TempPlaylist: '" + TempPlaylistName + "'");

            if (TempPlaylistName.equals(NewPlaylistName)){
                //System.out.println("Removing TempPlaylist: '" + TempPlaylistName + "'");
                PlaylistAPI.RemovePlaylist(TempPlaylist);
            }
        }

        //System.out.println("Adding Playlist: '" + NewPlaylistName +"'");
        Object NewPlaylist = PlaylistAPI.AddPlaylist(NewPlaylistName);

        for ( Object MediaObject : MediaObjectsArray){
            LOG.debug("MakePlaylist: Adding To Playlist: '" + MediaFileAPI.GetMediaTitle(MediaObject)+"::"+ MediaFileAPI.GetMediaFileID(MediaObject)+"'");
            PlaylistAPI.AddToPlaylist(NewPlaylist, AiringAPI.GetMediaFileForAiring(MediaObject));
        }
        LOG.debug("MakePlaylist: END");
        return NewPlaylist;
    }
	
    public static boolean IsWatchedPartial(Object MediaObject)
    /* 
     * returns true if the MediaObjects  the IsWatched() flag is false and it has been partially watched.
     * 
     * @param MediaObject, a sage MediaFile, Airing, or Show object
     */
    {
        if (!AiringAPI.IsWatched(MediaObject) && ( AiringAPI.GetWatchedDuration(MediaObject) != 0 ) ){
            return true;
        } else {
            return false;
        }
    }
	
    /*
     * Given an array of Airings will return the last watched (in real time) object.
     * 
     * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     */
    public static Object GetLastWatched(Object MediaObjects){
        if (MediaObjects==null){
            LOG.debug("GetLastWatched: null passed in");
            return null;
        }
        MediaObjects = Database.Sort(MediaObjects, true, "Gemstone_airing_GetRealWatchedStartTime");
        if(phoenix.media.IsWatched(Utility.GetElement(MediaObjects, 0))){
            return Utility.GetElement(MediaObjects, 0);
        }else{
            //return null if there are no watched items
            return null;
        }
    }

    public static Long GetRealWatchedStartTime(Object MediaObject){
        return sagex.api.AiringAPI.GetRealWatchedStartTime(phoenix.media.GetMediaObject(MediaObject));
    }

    /*
     * Given an array of Airings will return the next episode to watch by AiringDate (in real time).
     * Returns Null if Airings not found after last watched. (end of series)
     * 
     * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     */
    public static Object GetNextShow(Object MediaObjects){
        //LOG.debug("GetNextShow: for '" + MediaObjects + "'");
        if (MediaObjects==null){
            LOG.debug("GetNextShow: null passed in");
            return null;
        }
        Object LastWatched = airing.GetLastWatched(MediaObjects);
        if (LastWatched==null){
            //nothing has been watched so return the first media item as the next
            MediaObjects = Database.Sort(MediaObjects, false, "phoenix_metadata_GetOriginalAirDate");
            LOG.debug("GetNextShow: no items watched so returning the first item in OAD order '" + Utility.GetElement(MediaObjects, 0) + "'");
            return Utility.GetElement(MediaObjects, 0);
        }else{
            return GetShowNextPrev(MediaObjects, LastWatched, Boolean.TRUE);
        }
    }

    /*
     * Given an array of Airings will return the Newest episode by AiringDate.
     * Returns Null if invalid or all airings are watched
     * 
     * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     */
    public static Object GetNewShow(Object MediaObjects){
        if (MediaObjects==null){
            LOG.debug("GetNewShow: null passed in - returning null");
            return null;
        }
        if (Utility.Size(MediaObjects)==0){
            LOG.debug("GetNewShow: empty item list passed in - returning null");
            return null;
        }
        MediaObjects = Database.Sort(MediaObjects, false, "phoenix_metadata_GetOriginalAirDate");
        Object Newest = Utility.GetElement(MediaObjects, Utility.Size(MediaObjects)-1);
        if(phoenix.media.IsWatched(Newest)){
            LOG.debug("GetNewShow: Newest item is watched - returning null '" + Newest + "'");
            return null;
        }else{
            LOG.debug("GetNewShow: Newest '" + Newest + "'");
            return Newest;
        }
    }
    
    /*
     * Given an array of Airings will return the Next or Previous episode by AiringDate (in real time).
     * Returns Null if Airings not found after or before Current Show. (end or begin of series hit)
     * 
     * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     * @param CurrentShow - sage MediaFile, Airing, or Show Object.
     * @param DirectionNext - true gets Next, false gets Previous.
     */
    public static Object GetShowNextPrev(Object MediaObjects, Object CurrentShow, Boolean DirectionNext){
        if (MediaObjects==null){
            LOG.debug("GetShowNextPrev: null passed in");
            return null;
        }
        MediaObjects = Database.Sort(MediaObjects, false, "phoenix_metadata_GetOriginalAirDate");
        //MediaObjects = Database.Sort(MediaObjects, false, "Gemstone_MetadataCalls_GetOriginalAirDate");
        int index = Utility.FindElementIndex(MediaObjects, CurrentShow);

        if (index==-1){
            //the current show was not found so return the first item as either the next or previous
            LOG.debug("GetShowNextPrev: current show was not found so returning the first item '" + Utility.GetElement(MediaObjects, 0) + "'");
            return Utility.GetElement(MediaObjects, 0);
        }
        
        if(DirectionNext){
            if(index+1 >= Utility.Size(MediaObjects)){	
                LOG.debug("GetShowNextPrev: NEXT - Current Show is the last show so returning null as there is no NEXT");
                return null;
            }else{
                LOG.debug("GetShowNextPrev: NEXT returning item (" + (index+1) + ") '" + Utility.GetElement(MediaObjects, index+1) + "'");
                return Utility.GetElement(MediaObjects, index+1);
            }
        }else{
            if(index == 0){	
                LOG.debug("GetShowNextPrev: PREV - Current Show is the first show so returning null as there is no PREV");
                return null;
            }else{
                LOG.debug("GetShowNextPrev: PREV returning item (" + (index-1) + ") '" + Utility.GetElement(MediaObjects, index-1) + "'");
                return Utility.GetElement(MediaObjects, index-1);
            }
        }
    }

    /*
     * Given an array of Airings will return a random selection to watch by AiringDate (in real time).
     * Returns Null if Airings not found after last watched. (end of series)
     * 
     * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     */
    public static Object GetNextShowRandom(Object MediaObjects){
        if (MediaObjects==null){
            LOG.debug("GetNextShowRandom: null passed in");
            return null;
        }
        Object Unwatched = airing.GetShowsFromLastWatched(MediaObjects);
        if(Utility.Size(Unwatched)== 0){
            LOG.debug("GetNextShowRandom: No unwatched shows so returning null");
            return null;
        }else{
            //get a random index item from the list of unwatched shows
            Random generator = new Random();
            int randomIndex = generator.nextInt( Utility.Size(Unwatched) );
            LOG.debug("GetNextShowRandom: returning item (" + randomIndex + ") of (" + Utility.Size(Unwatched) + ") unwatched items");
            return Utility.GetElement(Unwatched, randomIndex);
        }
    }

    /* 
     * given an array of Airings will return a subarray sorted by original airing date 
     * where the first element of the array is the last watched episode 
     * (or the next episode if IsWatched() = true).  
     * Subsequent elements are all "later" episodes (as defined by flux_api_GetOriginalAiringDate).
     * Episodes "before" the last watched are truncated (again as defined by flux_api_GetOriginalAiringDate).
     * 
     * @param Arr - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     * Presumably grouped into AiringTitle and/or a filtered by a specific season.
     */
    public static Object[] GetShowsFromLastWatched(Object MediaObjects){
        if (MediaObjects==null){
            LOG.debug("GetShowsFromLastWatched: null passed in");
            return null;
        }
        Object LastWatched = airing.GetLastWatched(MediaObjects);
        if (LastWatched==null){
            LOG.debug("GetShowsFromLastWatched: no items have been watched so returning all items '" + MediaObjects + "'");
            return FanartCaching.toArray(MediaObjects);
        }else{
            Object NextWatch = airing.GetNextShow(MediaObjects);
            MediaObjects = Database.Sort(MediaObjects, false, "phoenix_metadata_GetOriginalAirDate");
            Object[] Arr0 = FanartCaching.toArray(MediaObjects);
            int	elementlocation = Utility.FindElementIndex(Arr0, NextWatch);
            LOG.debug("GetShowsFromLastWatched: returning items '" + elementlocation + "' to '" + Arr0.length + "' [" + Arrays.copyOfRange(Arr0, elementlocation, Arr0.length) + "]");
            return Arrays.copyOfRange(Arr0, elementlocation, Arr0.length);
        }
    }

    public static Object[] GetShowsFromShow(Object MediaObjects,Object MediaObject)
    /*
     * Given an array of Airings and a member of Airing will return the remaining shows
     * (1st element = the passed Airing)
     * with  in array after current show.
     * 
     * Returns Null if Airings not found after current show. (end of series)
     * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
     * @param MediaObject=sage object of current show to start from
     */
    {
        MediaObjects = Database.Sort(MediaObjects, false, "Gemstone_MetadataCalls_GetOriginalAirDate");
        int elementlocation = Utility.FindElementIndex(MediaObjects, MediaObject);
        Object[] Arr0 = FanartCaching.toArray(MediaObjects);

        if(elementlocation > Arr0.length){
            return null;
        }else{
            return Arrays.copyOfRange(Arr0, elementlocation, Arr0.length);
        }
    }
}