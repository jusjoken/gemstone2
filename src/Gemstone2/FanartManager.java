/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.phoenix.metadata.MediaArtifactType;
import sagex.phoenix.metadata.MediaType;
import sagex.phoenix.vfs.IMediaResource;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 * Get a new Object of this class in the STV
 * The default will be poster for either TV Series or Movie dependent on the passed in MediaObject
 * From the STV call setFanartType to another type which will load up the available fanart
 * From the STV call setTVMode to Series or a specific Season number which will load up the available fanart
 *
 */


public class FanartManager {

    static private final Logger LOG = Logger.getLogger(FanartManager.class);
    private static final String ConstSeries = "Series";
    private IMediaResource MediaResource = null;
    private IMediaResource PrimaryMediaResource = null;
    public static enum FanartManagerTypes{TV,MOVIE,NONE};
    public static enum TVModes{SERIES,SEASON};
    private TVModes TVMode = TVModes.SERIES; 
    private FanartManagerTypes FanartManagerType = FanartManagerTypes.NONE;
    private String FanartType = ""; //set within Init to poster as a default
    private List FanartList = Collections.emptyList();
    private List TVModeList = Collections.emptyList();
    private String DefaultFanart = "";
    private String FirstFanart = "";
    private String CurrentSeason = "-1";
    
    public FanartManager(IMediaResource MediaResource){
        this.MediaResource = MediaResource;
        Init();
    }
    public FanartManager(Object MediaResource){
        this.MediaResource = Source.ConvertToIMR(MediaResource);
        Init();
    }
    
    private void Init(){
        //based on the passed in MediaResource determine if a TV Series or Movie Fanart Object
        this.PrimaryMediaResource = this.MediaResource;
        //LOG.debug("Init: PrimaryMediaResource '" + PrimaryMediaResource + "'");
        if (phoenix.media.IsMediaType( this.MediaResource , "FOLDER" )){
            //get the first child and use it to determine the Fanart Type
            PrimaryMediaResource = ImageCache.GetChild(this.MediaResource, Boolean.FALSE);
        }
        //Now determine the Primary Media Resource Type
        if (phoenix.media.IsMediaType( this.PrimaryMediaResource , "TV" )){
            this.FanartManagerType = FanartManagerTypes.TV;
            TVModeList = GetFanartSeasons();
        }else if (phoenix.media.IsMediaType( this.PrimaryMediaResource , "VIDEO" )){
            this.FanartManagerType = FanartManagerTypes.MOVIE;
        }else if (phoenix.media.IsMediaType( this.PrimaryMediaResource , "DVD" )){
            this.FanartManagerType = FanartManagerTypes.MOVIE;
        }else if (phoenix.media.IsMediaType( this.PrimaryMediaResource , "BLURAY" )){
            this.FanartManagerType = FanartManagerTypes.MOVIE;
        }else{
            this.FanartManagerType = FanartManagerTypes.NONE;
        }
        //LOG.debug("Init: FanartManagerType '" + FanartManagerType + "' PrimaryMediaResource '" + PrimaryMediaResource + "'");
        //ImageCache.PrintUserRecord(PrimaryMediaResource.getTitle());
        //default to poster
        setFanartType("poster");
    }

    public String getTitle() {
        String Title = "No Fanart Found";
        if (this.FanartManagerType.equals(FanartManagerTypes.MOVIE)){
            Title = PrimaryMediaResource.getTitle();
        }else if (this.FanartManagerType.equals(FanartManagerTypes.TV)){
            Title = PrimaryMediaResource.getTitle();
        }
        return Title;
    }
    public String getTitleFull() {
        String Title = "No Fanart Found";
        if (this.FanartManagerType.equals(FanartManagerTypes.MOVIE)){
            Title = PrimaryMediaResource.getTitle();
        }else if (this.FanartManagerType.equals(FanartManagerTypes.TV)){
            Title = PrimaryMediaResource.getTitle();
            if (TVMode.equals(TVModes.SEASON)){
                Title = Title + " (Season " + CurrentSeason + ")";
            }
        }
        return Title;
    }

    public List getFanartList() {
        return FanartList;
    }

    public String getFanartType() {
        return FanartType;
    }

    public Boolean IsFanartTypePoster(){
        return this.FanartType.toLowerCase().equals("poster");
    }
    public Boolean IsFanartTypeBanner(){
        return this.FanartType.toLowerCase().equals("banner");
    }
    public Boolean IsFanartTypeBackground(){
        return this.FanartType.toLowerCase().equals("background");
    }

    public String getTVMode() {
        if (!IsTV()){
            return "";
        }
        if (TVMode.equals(TVModes.SERIES)){
            return "Series";
        }else{
            return CurrentSeason;
        }
    }
    public void setTVMode(String NewMode) {
        //get a string from the STV code to indicate Series or a specific Season
        Boolean UpdateSettings = Boolean.FALSE;
        if (NewMode.equals(ConstSeries)){
            //this is a Series Mode
            if (!this.TVMode.equals(TVModes.SERIES)){
                //change to Series Mode and reload settings
                this.TVMode = TVModes.SERIES;
                this.CurrentSeason = "-1";
                UpdateSettings = Boolean.TRUE;
            }
        }else{
            //must be a Season Number
            if (!this.TVMode.equals(TVModes.SEASON)){
                //change to Season Mode and reload settings
                this.TVMode = TVModes.SEASON;
                CurrentSeason = NewMode;
                UpdateSettings = Boolean.TRUE;
            }else{
                //was previously Season so see if this is a different Season
                if (!NewMode.equals(CurrentSeason)){
                    CurrentSeason = NewMode;
                    UpdateSettings = Boolean.TRUE;
                }
            }
        }
        if (UpdateSettings){
            LoadFanartList();
        }
    }

    public void setFanartType(String FanartType) {
        //change the fanart type and then set specific settings related to this change
        if (!this.FanartType.equals(FanartType)){
            this.FanartType = FanartType.toLowerCase();
            //load the list of this fanart type
            LoadFanartList();
        }
    }
    
    private void LoadFanartList(){
        FanartList = Collections.emptyList();
        Map<String,String> faMetadata = null;
        MediaType faMediaType = null;
        Object faMediaObject = null;
        String faMediaTitle = null;
       
        if (IsTV()){
            //LOG.debug("LoadFanartList: TV item found");
            if (TVMode.equals(TVModes.SERIES)){
                //LOG.debug("LoadFanartList: TV SERIES item found");
                faMediaObject = PrimaryMediaResource.getMediaObject();
                faMediaType = MediaType.TV;
                faMetadata = Collections.emptyMap();
            }else{ //must be SEASON
                //LOG.debug("LoadFanartList: TV SEASON item found");
                faMediaObject = PrimaryMediaResource.getMediaObject();
                //faMediaObject = null;
                faMediaType = MediaType.TV;
                faMediaTitle = PrimaryMediaResource.getTitle();
                faMetadata = new HashMap<String,String>();
                faMetadata.put("SeasonNumber",CurrentSeason);
                faMetadata.put("EpisodeNumber","1");
            }
        }else if (IsMovie()){
            //LOG.debug("LoadFanartList: MOVIE item found");
            faMediaObject = PrimaryMediaResource.getMediaObject();
            faMediaType = MediaType.MOVIE;
        }else{ //must be invalid
            LOG.debug("LoadFanartList: Invalid - not TV nor MOVIE");
            return;
        }
        //LOG.debug("LoadFanartList: calling GetFanartArtifacts with faMediaObject'" + faMediaObject + "' faMediaType '" + faMediaType.toString() + "' faMediaTitle '" + faMediaTitle + "' FanartType '" + FanartType + "' faMetadata '" + faMetadata + "'");
        String tMediaType = null;
        if (faMediaType!=null){
            tMediaType = faMediaType.toString();
        }
        String[] tList = phoenix.fanart.GetFanartArtifacts(faMediaObject, tMediaType, faMediaTitle, FanartType, null, faMetadata);
        if (tList!=null){
            FanartList = new ArrayList<String>(Arrays.asList(tList));
            //LOG.debug("LoadFanartList: FanartList '" + FanartList + "' for '" + PrimaryMediaResource.getTitle() + "'");
        }else{
            LOG.debug("LoadFanartList: no '" + FanartType + "' Fanart Found for '" + PrimaryMediaResource.getTitle() + "'");
        }
        //Set the default fanart item if there are any fanart items
        if (!FanartList.isEmpty()){
            //DefaultFanart = ImageCache.GetDefaultArtifact(PrimaryMediaResource, FanartType, faMetadata);
            String NotNeededString = "NotNeededString";  //used in this call as the value does not matter
            //LOG.debug("LoadFanartList: getting default from phoenix");
            //LOG.debug("LoadFanartList: faMediaObject '" + faMediaObject + "'");
            //LOG.debug("LoadFanartList: faMediaType '" + faMediaType + "'");
            //LOG.debug("LoadFanartList: FanartType '" + ImageCacheKey.ConvertStringtoMediaArtifactType(FanartType) + "'");
            //LOG.debug("LoadFanartList: faMetadata '" + faMetadata + "'");
            File defFile = getDefaultArtifactFullPath(phoenix.fanart.GetDefaultArtifact(faMediaObject, faMediaType, NotNeededString, ImageCacheKey.ConvertStringtoMediaArtifactType(FanartType), NotNeededString, faMetadata));
            //LOG.debug("LoadFanartList: defFile '" + defFile + "'");
            if (defFile==null){
                DefaultFanart = null;
            }else{
                DefaultFanart = defFile.toString();
            }
            LOG.debug("LoadFanartList: DegaultFanart '" + DefaultFanart + "' for '" + PrimaryMediaResource.getTitle() + "'");
            //Add the default item (if any) to the TOP of the list - make sure it is also removed from the list
            if (DefaultFanart!=null){
                //LOG.debug("LoadFanartList: DegaultFanart was not null - FanartList '" + FanartList + "'");
                if (FanartList.contains(DefaultFanart)){
                    FanartList.remove(DefaultFanart);
                    FanartList.add(0, DefaultFanart);
                }
            }else{
                //there is NO default so save the first item
                FirstFanart = FanartList.get(0).toString();
                LOG.debug("LoadFanartList: DegaultFanart was null - setting FirstFanart '" + FirstFanart + "'");
            }
        }
    }
    
    private File getDefaultArtifactFullPath(File defFile){
        if (defFile !=null) {
            String defFilePath = defFile.toString();
            try {
                defFilePath = defFile.getCanonicalPath();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(FanartManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            defFile = new File(defFilePath.toString());
            if (defFile.exists() && defFile.isFile()) {
                LOG.debug("getDefaultArtifactFullPath: valid file being returned '" + defFile + "'");
                return defFile;
            }else{
                LOG.debug("getDefaultArtifactFullPath: invalid file or not found - returning null '" + defFile + "'");
                return null;
            }
        }else{
            LOG.debug("getDefaultArtifactFullPath: null passed in so returning null ");
            return null;
        }
    }
    

    public void DeleteFanartItem(String FanartItem){
        //remove from the file system
        File f1 = new File(FanartItem);
        f1.delete();
        RemoveFanartItem(FanartItem, Boolean.FALSE);
        RemoveFanartItem(FanartItem, Boolean.TRUE);
        //ReloadFanartItem();
        //reload the fanart list
        LoadFanartList();
    }
    
    public void RemoveFanartItemFromMemory(String FanartItem){
        RemoveFanartItemFromMemory(FanartItem, Boolean.FALSE);
        RemoveFanartItemFromMemory(FanartItem, Boolean.TRUE);
        //reload the fanart list
        LoadFanartList();
    }
    
    private void RemoveFanartItem(String FanartItem, Boolean OriginalSize){
        //clear caches
        phoenix.fanart.ClearMemoryCaches();
        //remove the fanart item from the cache for this media item
        String tKey = ImageCache.GetFanartKey(FanartItem, OriginalSize);
        //LOG.debug("RemoveFanartItem: Removing Key '" + tKey + "'");
        
        ImageCache.RemoveItemFromCache(tKey);
        Object tImage = phoenix.image.GetImage(tKey, ImageCache.CreateImageTag);
        if (tImage!=null){
            //delete the image from the fanart cache
            //LOG.debug("RemoveFanartItem: deleting '" + tImage + "'");
            File tFile = new File(tImage.toString());
            tFile.delete();
        }
    }

    private void RemoveFanartItemFromMemory(String FanartItem, Boolean OriginalSize){
        //clear caches
        phoenix.fanart.ClearMemoryCaches();
        //remove the fanart item from the cache for this media item
        String tKey = ImageCache.GetFanartKey(FanartItem, OriginalSize);
        //LOG.debug("RemoveFanartItem: Removing Key '" + tKey + "'");
        ImageCache.RemoveItemFromCache(tKey);
    }

    public void SetFanartAsDefault(String FanartItem){
        Map<String,String> faMetadata = null;
        MediaType faMediaType = null;
        Object faMediaObject = null;
        String faMediaTitle = null;
       
        if (IsTV()){
            //LOG.debug("SetFanartAsDefault: TV item found");
            if (TVMode.equals(TVModes.SERIES)){
                //LOG.debug("SetFanartAsDefault: TV SERIES item found");
                faMediaObject = PrimaryMediaResource.getMediaObject();
                faMediaType = MediaType.TV;
                faMetadata = Collections.emptyMap();
            }else{ //must be SEASON
                //LOG.debug("SetFanartAsDefault: TV SEASON item found");
                faMediaObject = PrimaryMediaResource.getMediaObject();
                faMediaType = MediaType.TV;
                faMediaTitle = PrimaryMediaResource.getTitle();
                faMetadata = new HashMap<String,String>();
                faMetadata.put("SeasonNumber",CurrentSeason);
                faMetadata.put("EpisodeNumber","1");
            }
        }else if (IsMovie()){
            //LOG.debug("SetFanartAsDefault: MOVIE item found");
            faMediaObject = PrimaryMediaResource.getMediaObject();
            faMediaType = MediaType.MOVIE;
        }else{ //must be invalid
            LOG.debug("SetFanartAsDefault: Invalid - not TV nor MOVIE");
            return;
        }
        File FanartFile = new File(FanartItem);

        //Add special SetFanartArtifact method to handle SEASON defaults
        phoenix.fanart.SetFanartArtifact(faMediaObject, FanartFile, faMediaType.toString(), faMediaTitle, FanartType, null, faMetadata);
        //removed the following as phoenix core handles this as of 11/04/2012
        //ImageCache.SetFanartArtifact(faMediaObject, FanartFile, faMediaType, faMediaTitle, FanartType, null, faMetadata);
        
        //reload the fanart list
        //LOG.debug("SetFanartAsDefault: DefaultFanart '" + DefaultFanart + "' title '" + this.getTitle() + "'");
        if (this.DefaultFanart!=null){
            RemoveFanartItem(this.DefaultFanart, Boolean.FALSE);
            RemoveFanartItem(this.DefaultFanart, Boolean.TRUE);
        }else{
            if (!FirstFanart.isEmpty()){
                RemoveFanartItem(this.FirstFanart, Boolean.FALSE);
                RemoveFanartItem(this.FirstFanart, Boolean.TRUE);
            }
        }
        //reload the fanart list
        LoadFanartList();
    }

    //this will be a list of modes such as Series,1,2,3 - number representing the Seasons
    public List getTVModeList() {
        return TVModeList;
    }

    private List GetFanartSeasons(){
        List tFanartSeasons = new ArrayList();
        if (!IsTV()){
            LOG.debug("GetFanartSeasons: not valid for non TV Media File '" + PrimaryMediaResource.getTitle() + "'");
            return Collections.emptyList();
        }
        String faMediaTitle = null;
        Object faMediaObject = PrimaryMediaResource.getMediaObject();
        MediaType faMediaType = MediaType.TV;
        Map<String,String> faMetadata = Collections.emptyMap();
        String FanartFolder = null;

        //Get a Series Folder and then get it's parent
        FanartFolder = phoenix.fanart.GetFanartArtifactDir(faMediaObject, faMediaType.toString(), null, "poster", null, faMetadata, Boolean.FALSE);
        if (FanartFolder==null){
            LOG.debug("GetFanartSeasons: no Fanart available for '" + PrimaryMediaResource.getTitle() + "'");
            return Collections.emptyList();
        }
        File FanartFile = new File(FanartFolder);
        if (!FanartFile.exists()){
            LOG.debug("GetFanartSeasons: Fanart Dir not found '" + FanartFolder + "'");
            return Collections.emptyList();
        }
        FanartFile = FanartFile.getParentFile();
        if (FanartFile==null){
            LOG.debug("GetFanartSeasons: Parent not found for '" + FanartFolder + "'");
            return Collections.emptyList();
        }
        //find all the Season Folders
        File[] listOfFiles = FanartFile.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isDirectory()) {
            if (listOfFiles[i].getName().startsWith("Season")){
                String tSeason = listOfFiles[i].getName().substring(7);
                
                Integer tInteger = -1;
                try {
                    tInteger = Integer.valueOf(tSeason);
                } catch (NumberFormatException ex) {
                    //skip as it is still -1
                }
                if (tInteger.equals(-1)){
                    LOG.debug("GetFanartSeasons: Season '" + tSeason + "' could not be converted to an Integer for '" + PrimaryMediaResource.getTitle() + "'");
                    //don't add
                }else{
                    //LOG.debug("GetFanartSeasons: Adding Season '" + tSeason + "' for '" + PrimaryMediaResource.getTitle() + "'");
                    tFanartSeasons.add(tInteger);
                }
            }
          }
        }
        if (!tFanartSeasons.isEmpty()){
            //Now sort the list
            Collections.sort(tFanartSeasons);
            //add Series to the front of the list of Seasons
            //tFanartSeasons.add(0, ConstSeries);
        }
        //LOG.debug("GetFanartSeasons: Seasons found '" + tFanartSeasons + "' for '" + PrimaryMediaResource.getTitle() + "'");
        return tFanartSeasons;
    }
    
    public Integer getTableCols() {
        if (IsFanartTypePoster()){
            return 7;
        }else if (IsFanartTypeBackground()){
            return 3;
        }else if (IsFanartTypeBanner()){
            return 2;
        }else{
            return 7; //assume poster
        }
    }

    public Integer getTableRows() {
        if (IsFanartTypePoster()){
            return 2;
        }else if (IsFanartTypeBackground()){
            return 2;
        }else if (IsFanartTypeBanner()){
            return 3;
        }else{
            return 2; //assume poster
        }
    }

    public Boolean IsTV(){
        if (this.FanartManagerType.equals(FanartManagerTypes.TV)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public Boolean IsMovie(){
        if (this.FanartManagerType.equals(FanartManagerTypes.MOVIE)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    public Boolean IsValid(){
        if (this.FanartManagerType.equals(FanartManagerTypes.NONE)){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    
    public Boolean IsCurrentFanartType(String FanartType){
        if (FanartType.toLowerCase().equals(this.FanartType)){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
    
    public Boolean IsDefault(String FanartItem){
        if (this.DefaultFanart==null){
            //LOG.debug("IsDefault: DefaultFanart = null: CurrentItem '" + FanartItem + "' Default '" + this.DefaultFanart + "'");
            return Boolean.FALSE;
        }
        if (this.DefaultFanart.isEmpty()){
            //LOG.debug("IsDefault: DefaultFanart = isEmpty: CurrentItem '" + FanartItem + "' Default '" + this.DefaultFanart + "'");
            return Boolean.FALSE;
        }
        if (this.DefaultFanart.equals(FanartItem)){
            //LOG.debug("IsDefault: match found: CurrentItem '" + FanartItem + "' Default '" + this.DefaultFanart + "'");
            return Boolean.TRUE;
        }
        //LOG.debug("IsDefault: no match found: CurrentItem '" + FanartItem + "' Default '" + this.DefaultFanart + "'");
        return Boolean.FALSE;
    }
    
    //return a CreateImage object for the specific Fanart Item
    public Object GetImage(String FanartItem){
        if (FanartItem==null){
            LOG.debug("GetImage: null FanartItem passed in - returning null");
            return null;
        }
        ImageCacheKey tKey = new ImageCacheKey(FanartItem,Boolean.FALSE,this.FanartType,Boolean.TRUE);
        return ImageCache.CreateImage(tKey);
    }
    
    public void CacheImage(String FanartItem, Boolean OriginalSize){
        if (FanartItem==null){
            LOG.debug("CacheImage: null FanartItem passed in");
            return;
        }
        Object tImage = null;
        ImageCacheKey tKey = new ImageCacheKey(FanartItem,OriginalSize,this.FanartType,Boolean.FALSE);
        if (OriginalSize){
            ImageCache.SetPreCacheItemType(FanartType + " (FULL)");
        }else{
            ImageCache.SetPreCacheItemType(FanartType);
        }
        ImageCache.SetPreCacheItemLocation(tKey.getImagePath());
        //update counts for the summary view
        if (this.FanartType.equals("poster")){
            if (OriginalSize){
                ImageCache.PreCacheFullPosterItems++;
            }else{
                ImageCache.PreCachePosterItems++;
            }
        }else if (this.FanartType.equals("banner")){
            ImageCache.PreCacheBannerItems++;
        }else if (this.FanartType.equals("background")){
            if (OriginalSize){
                ImageCache.PreCacheFullBackgroundItems++;
            }else{
                ImageCache.PreCacheBackgroundItems++;
            }
        }
        //check if this item is already cached
        tImage = phoenix.image.GetImage(tKey.getKey(), ImageCache.CreateImageTag);
        if (tImage!=null){
            LOG.debug("CacheImage: item already cached '" + tKey.getKey() + "' Image = '" + tImage + "'");
            ImageCache.PreCacheItemsExisted++;
            ImageCache.SetPreCacheItemInfo("Item already cached");
            return;
        }
//        UIContext UIc = new UIContext(sagex.api.Global.GetUIContextName());
//        //based on the ImageType determine the scalewidth to use
//        Integer UIWidth = sagex.api.Global.GetFullUIWidth(UIc);
//        Double scalewidth = 0.2;
//        Double scalepercent = 1.0; //TODO: need to set this from the UI and save in a property
//        if (tKey.getOriginalSize()){
//            scalewidth = 1.0;
//        }else{
//            if (IsFanartTypePoster()){
//                scalewidth = 0.2 * scalepercent;
//            }else if (IsFanartTypeBanner()){
//                scalewidth = 0.6 * scalepercent;
//            }else if (IsFanartTypeBackground()){
//                scalewidth = 0.4 * scalepercent;
//            }else{
//                //use default
//            }
//        }
        Double finalscalewidth = ImageCache.GetScaleWidth(ImageCacheKey.ConvertStringtoMediaArtifactType(FanartType), OriginalSize);
        try {
            tImage = phoenix.image.CreateImage(tKey.getKey(), ImageCache.CreateImageTag, tKey.getImagePath(), "{name: scale, width: " + finalscalewidth + ", height: -1}", false);
        } catch (Exception e) {
            LOG.debug("CacheImage: phoenix.image.CreateImage FAILED - finalscalewidth = '" + finalscalewidth + "' for Type = '" + tKey.getArtifactType().toString() + "' Image = '" + tKey.getImagePath() + "' Error: '" + e + "'");
            ImageCache.PreCacheItemsFailed++;
            return;
        }
        if (tImage==null){
            LOG.debug("CacheImage: CreateImage returned null for FanartItem '" + FanartItem + "'");
            ImageCache.PreCacheItemsFailed++;
            return;
        }else{
            LOG.debug("CacheImage: item added to cache '" + tKey.getKey() + "' Image = '" + tImage + "'");
            ImageCache.SetPreCacheItemInfo("Item added to cache");
            ImageCache.PreCacheItemsCreated++;
        }
        return;
    }
    
    public void CacheEachFanartItem(){
        for (String faType: GetFanartTypes()){
            setFanartType(faType);
            //Cache the first item in the list
            if (!FanartList.isEmpty()){
                if (ImageCache.PreCache(faType)){
                    CacheImage(FanartList.get(0).toString(), Boolean.FALSE);
                }
                if (faType.toLowerCase().equals("background") && ImageCache.PreCacheFullBackgrounds()){
                    //also cache a FullSize background
                    CacheImage(FanartList.get(0).toString(), Boolean.TRUE);
                }
                if (faType.toLowerCase().equals("poster") && ImageCache.PreCacheFullPosters()){
                    //also cache a FullSize poster
                    CacheImage(FanartList.get(0).toString(), Boolean.TRUE);
                }
            }
            if (IsTV()){
                for (Object Season: TVModeList){
                    setTVMode(Season.toString());
                    if (!FanartList.isEmpty()){
                        if (ImageCache.PreCache(faType)){
                            CacheImage(FanartList.get(0).toString(), Boolean.FALSE);
                        }
                        if (faType.toLowerCase().equals("background") && ImageCache.PreCacheFullBackgrounds()){
                            //also cache a FullSize background
                            CacheImage(FanartList.get(0).toString(), Boolean.TRUE);
                        }
                        if (faType.toLowerCase().equals("poster") && ImageCache.PreCacheFullPosters()){
                            //also cache a FullSize poster
                            CacheImage(FanartList.get(0).toString(), Boolean.TRUE);
                        }
                    }
                }
            }
        }
    }
    
    public String[] GetFanartTypes(){
        if (IsMovie()){
            return new String[] {"Poster","Background"};
        }else if (IsTV()){
            return new String[] {"Poster","Banner","Background"};
        }else{
            return new String[0];
        }
    }
    
}
