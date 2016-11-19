package Gemstone;

import org.apache.log4j.Logger;
import sagex.phoenix.vfs.IMediaResource;

import java.util.*;

/**
 * Created by jusjoken on 2/20/2016.
 * Provides a single interface to get the New, Last and Next episodes from a list of episodes
 */
public class SeriesNewLastNext {
    static private final Logger LOG = Logger.getLogger(SeriesNewLastNext.class);
    private List childList = null;
    private List unwatchedList = null;
    private Boolean UseRealTimeSort = Boolean.FALSE;
    private Object objNew = null;
    private Object objLast = null;
    private Object objNext = null;
    private Object objRandom = null;

    public SeriesNewLastNext(){
        LOG.debug("NewLastNext cannot be used without passing in a list to work with. Please passing in a Object array or list");
    }

    public SeriesNewLastNext(Object[] Objects){
        if (Objects==null){
            LOG.debug("NewLastNext Null passed in so all values will be null as well");
        }else{
            LOG.debug("NewLastNext Object Array passed in '" + Objects + "' converting to IMR");
            this.childList = new ArrayList();
            for (int i=0; i<Objects.length; i++){
                //LOG.debug("NewLastNext Adding Object '" + Objects[i] + "'");
                //LOG.debug("NewLastNext Adding IMR '" + phoenix.media.GetMediaResource(Objects[i]) + "'");
                this.childList.add(phoenix.media.GetMediaResource(Objects[i]));
                //LOG.debug("NewLastNext Added.");
            }
            Create();
        }
    }

    public SeriesNewLastNext(List ChildList){
        LOG.debug("NewLastNext List passed in '" + ChildList + "'");
        //check to ensure this is a list of IMediaResource items
        if (!ChildList.isEmpty()){
            if (ChildList.get(0) instanceof IMediaResource){
                this.childList = ChildList;
            }else{
                LOG.debug("NewLastNext List does NOT contain IMR objects - converting to IMR");
                this.childList = new ArrayList();
                for (Object objTemp: ChildList){
                    //LOG.debug("NewLastNext Adding Object '" + objTemp + "'");
                    //LOG.debug("NewLastNext Adding IMR '" + phoenix.media.GetMediaResource(objTemp) + "'");
                    this.childList.add(phoenix.media.GetMediaResource(objTemp));
                    //LOG.debug("NewLastNext Added.");
                }
            }
        }
        Create();
    }

    private void Create(){
        if (this.childList==null || this.childList.isEmpty()){
            //do nothing as the default null values will be used
        }else{
            //sort the list be S/E as there is no guarantee that it already is
            Collections.sort(this.childList,new SeasonEpisodeSorter());
            //optionally sort by the realtime watched values
            if (util.GetTrueFalseOption("Utility","NewLastNextModeTimeOrFlag")){
                LOG.debug("NewLastNext logic using sort by realtime watched time");
                UseRealTimeSort = Boolean.TRUE;
                Collections.sort(this.childList,new WatchedSorter());
            }else{
                UseRealTimeSort = Boolean.FALSE;
                LOG.debug("NewLastNext logic using sort by watched flag");
            }
            //reverse the list to find the last watched
            Collections.reverse(this.childList);
            for (Object objItem : this.childList){
                if(phoenix.media.IsWatched(objItem)){
                    LOG.debug("Setting last watched to S" + phoenix.metadata.GetSeasonNumber(objItem) + "E" + phoenix.metadata.GetEpisodeNumber(objItem));
                    objLast = objItem;
                    break;
                }
            }
            if(UseRealTimeSort){
                //Sort is RealTime so now that we have Last Watched resort by S/E
                Collections.sort(this.childList,new SeasonEpisodeSorter());
            }else{
                //Sort is WatchedFlag so reverse the list again so it's in the proper order
                Collections.reverse(this.childList);
            }
            //now determine the Next episode
            if (objLast==null){
                //Next is the first item in the list if nothing has been watched
                objNext = this.childList.get(0);
                LOG.debug("Setting next to first item in the list S" + phoenix.metadata.GetSeasonNumber(objNext) + "E" + phoenix.metadata.GetEpisodeNumber(objNext));
            }else{
                //Next is the next item in the list
                int intLast = this.childList.indexOf(objLast);
                if (intLast<this.childList.size()-1){
                    objNext = this.childList.get(intLast+1);
                    LOG.debug("Setting next to watch to S" + phoenix.metadata.GetSeasonNumber(objNext) + "E" + phoenix.metadata.GetEpisodeNumber(objNext));
                }else{
                    LOG.debug("Setting next to null as all items have been watched");
                }
            }
            //now determine the New episode
            objNew = this.childList.get(this.childList.size()-1);
            if(phoenix.media.IsWatched(objNew)){
                //if the new item has been watched then new should be reset to null
                objNew = null;
                LOG.debug("Setting new to null as all items have been watched");
            }else{
                LOG.debug("Setting new to S" + phoenix.metadata.GetSeasonNumber(objNew) + "E" + phoenix.metadata.GetEpisodeNumber(objNew));
            }
            //get a list of unwatched from Last
            if (objLast==null){
                //as last is null then unwatched is the full list
                unwatchedList = this.childList;
            }else{
                if(objNew==null){
                    //as new is null then all items have been watched so use an empty list
                    unwatchedList = Collections.emptyList();
                }else{
                    //set unwatched to the portion of the childList after Last
                    int intLast = this.childList.indexOf(objLast);
                    unwatchedList = this.childList.subList(intLast+1,this.childList.size()-1);
                }
            }
            //set a random episode of unwatched episodes
            if(unwatchedList.isEmpty()){
                objRandom = null;
                LOG.debug("Setting random to null as all items have been watched");
            }else{
                Random generator = new Random();
                int randomIndex = generator.nextInt( unwatchedList.size() );
                objRandom = this.childList.get(randomIndex);
                LOG.debug("Setting random to S" + phoenix.metadata.GetSeasonNumber(objRandom) + "E" + phoenix.metadata.GetEpisodeNumber(objRandom));
            }

        }

    }

    public List UnwatchedList() {
        return unwatchedList;
    }

    public Object NextItem(Object item) {
        if(item==null){
            LOG.debug("NextItem returning null as item passed in was null");
            return null;
        }
        if(this.childList==null){
            LOG.debug("NextItem returning null as the childList is null");
            return null;
        }
        int intItem = this.childList.indexOf(item);
        if(intItem==-1){
            LOG.debug("NextItem returning null as item passed in was not found");
            return null;
        }else{
            if (intItem<this.childList.size()-1){
                Object retVal = this.childList.get(intItem + 1);
                LOG.debug("NextItem for item S" + phoenix.metadata.GetSeasonNumber(item) + "E" + phoenix.metadata.GetEpisodeNumber(item) + " returning S" + phoenix.metadata.GetSeasonNumber(retVal) + "E" + phoenix.metadata.GetEpisodeNumber(retVal));
                return retVal;
            }else{
                LOG.debug("NextItem returning null as item passed is the last item");
                return null;
            }
        }
    }

    public Object PrevItem(Object item) {
        if(item==null){
            LOG.debug("PrevItem returning null as item passed in was null");
            return null;
        }
        if(this.childList==null){
            LOG.debug("PrevItem returning null as the childList is null");
            return null;
        }
        int intItem = this.childList.indexOf(item);
        if(intItem==-1){
            LOG.debug("PrevItem returning null as item passed in was not found");
            return null;
        }else{
            if (intItem>0){
                Object retVal = this.childList.get(intItem - 1);
                LOG.debug("PrevItem for item S" + phoenix.metadata.GetSeasonNumber(item) + "E" + phoenix.metadata.GetEpisodeNumber(item) + " returning S" + phoenix.metadata.GetSeasonNumber(retVal) + "E" + phoenix.metadata.GetEpisodeNumber(retVal));
                return retVal;
            }else{
                LOG.debug("PrevItem returning null as item passed is the first item");
                return null;
            }
        }
    }

    public Object New() {
        return objNew;
    }

    public Object Last() {
        return objLast;
    }

    public Object Next() {
        return objNext;
    }

    public Object Random() {
        return objRandom;
    }

    public Boolean hasNew(){
        if(objNew==null) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public Boolean hasNext(){
        if(objNext==null) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public Boolean hasRandom(){
        if(objRandom==null) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public Boolean hasLast(){
        if(objLast==null) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    //convenience static function to get next on the fly
    public static Object GetNext(List ChildList){
        SeriesNewLastNext tNLN = new SeriesNewLastNext(ChildList);
        return tNLN.Next();
    }
    public static Object GetNext(Object[] Objects){
        SeriesNewLastNext tNLN = new SeriesNewLastNext(Objects);
        return tNLN.Next();
    }
}
