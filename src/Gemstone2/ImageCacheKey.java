/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone2;

import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import sagex.UIContext;
import sagex.phoenix.metadata.MediaArtifactType;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class ImageCacheKey {

    private String ImagePath = "";
    private Boolean OriginalSize = Boolean.FALSE;
    private MediaArtifactType ArtifactType = null;
    private Object DefaultEpisodeImage = null;
    private String defaultImage = null;
    private String RefreshArea = null;
    private String RefreshKey = null;
    private Boolean KeepFilenameOnKey = Boolean.FALSE;
    private Boolean RefreshAll = Boolean.FALSE;
    private SortedSet<String> UIList = new TreeSet();
    static private final Logger LOG = Logger.getLogger(ImageCache.class);

    public ImageCacheKey() {
        UIList.add(sagex.api.Global.GetUIContextName());
    }

    public ImageCacheKey(String ImagePath) {
        this.ImagePath = ImagePath;
        UIList.add(sagex.api.Global.GetUIContextName());
    }
    
    public ImageCacheKey(String ImagePath, Boolean OriginalSize) {
        this.ImagePath = ImagePath;
        UIList.add(sagex.api.Global.GetUIContextName());
        this.OriginalSize = OriginalSize;
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, MediaArtifactType ArtifactType) {
        this.ImagePath = ImagePath;
        UIList.add(sagex.api.Global.GetUIContextName());
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ArtifactType;
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, String ArtifactType) {
        this.ImagePath = ImagePath;
        UIList.add(sagex.api.Global.GetUIContextName());
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ConvertStringtoMediaArtifactType(ArtifactType);
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, MediaArtifactType ArtifactType, Boolean KeepFilenameOnKey) {
        this.ImagePath = ImagePath;
        UIList.add(sagex.api.Global.GetUIContextName());
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ArtifactType;
        this.KeepFilenameOnKey = KeepFilenameOnKey;
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, String ArtifactType, Boolean KeepFilenameOnKey) {
        this.ImagePath = ImagePath;
        UIList.add(sagex.api.Global.GetUIContextName());
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ConvertStringtoMediaArtifactType(ArtifactType);
        this.KeepFilenameOnKey = KeepFilenameOnKey;
    }

    @Override
    public String toString() {
        return "ImageCacheKey{" + "ImagePath=" + ImagePath + ", OriginalSize=" + OriginalSize + ", ArtifactType=" + ArtifactType + ", Key=" + getKey() + ", DefaultEpisodeImage=" + DefaultEpisodeImage + ", defaultImage=" + defaultImage + " UIList=[" + UIList + "]}";
    }
    
    public String getKey(){
        return ImageCache.GetFanartKey(this.ImagePath, this.OriginalSize, this.KeepFilenameOnKey);
    }

    public MediaArtifactType getArtifactType() {
        return ArtifactType;
    }

    public void setArtifactType(MediaArtifactType ArtifactType) {
        this.ArtifactType = ArtifactType;
    }
    public void setArtifactType(String ArtifactType) {
        this.ArtifactType = ConvertStringtoMediaArtifactType(ArtifactType);
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
    }

    public Boolean getOriginalSize() {
        return OriginalSize;
    }

    public void setOriginalSize(Boolean OriginalSize) {
        this.OriginalSize = OriginalSize;
    }

    public Object getDefaultEpisodeImage() {
        return DefaultEpisodeImage;
    }

    public void setDefaultEpisodeImage(Object DefaultEpisodeImage) {
        this.DefaultEpisodeImage = DefaultEpisodeImage;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }
    
    public boolean containsUI(String UI){
        return UIList.contains(UI);
    }
    public void addUI(String UI){
        UIList.add(UI);
    }

    public SortedSet<String> getUIList() {
        return UIList;
    }

    public String Refresh(){
        //refresh each of the UI's
        String tRefresh = "";
        for (String UI:this.UIList){
            UIContext UIc = new UIContext(UI);
            if (this.HasRefreshAll()){
                sagex.api.Global.Refresh(UIc);
                tRefresh = "All";
            }else if (this.HasRefreshArea()){
                tRefresh = this.getRefreshArea();
                sagex.api.Global.RefreshArea(UIc, tRefresh);
            }else if (this.HasRefreshKey()){
                tRefresh = this.getRefreshKey();
                sagex.api.Global.RefreshAreaForVariable(UIc, "MediaKey", tRefresh);
            }
        }
        return tRefresh;
    }
    
    public void MergeKey(ImageCacheKey inKey){
        //add in all the UI's from the input Key to this Key
        boolean changed = false;
        for (String UI:inKey.getUIList()){
            changed = UIList.add(UI);
        }
        //handle the merge of the refresh settings
        if (inKey.HasRefreshArea()){
            if (!inKey.RefreshArea.equals(this.RefreshArea)){
                changed = true;
                setRefreshAll(Boolean.TRUE);
            }
        }
        if (inKey.HasRefreshKey()){
            if (!inKey.RefreshKey.equals(this.RefreshKey)){
                changed = true;
                setRefreshAll(Boolean.TRUE);
            }
        }
        if (changed){
            LOG.debug("MergeKey: already in the Queue but updated '" + inKey.getKey() + "' defaultImage returned '" + inKey.getDefaultImage() + "' RefreshArea '" + this.RefreshArea + "' RefreshAll '" + this.RefreshAll + "' RefreshKey '" + this.RefreshKey + "' UIList '" + this.UIList + "'");
        }
    }

    public void setRefreshAll(Boolean RefreshAll) {
        this.RefreshAll = RefreshAll;
        this.RefreshArea = null;
        this.RefreshKey = null;
    }

    public String getRefreshArea() {
        return RefreshArea;
    }

    public void setRefreshArea(String RefreshArea) {
        this.RefreshArea = RefreshArea;
    }

    public String getRefreshKey() {
        return RefreshKey;
    }

    public void setRefreshKey(String RefreshKey) {
        this.RefreshKey = RefreshKey;
    }

    public Boolean HasRefreshKey(){
        if (this.RefreshKey==null){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }

    public Boolean HasRefreshArea(){
        if (this.RefreshArea==null){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    
    public Boolean HasRefreshAll(){
        return RefreshAll;
    }
    
    public Boolean IsValidKey(){
        if (this.ImagePath.isEmpty()){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    
    public Boolean HasDefaultEpisodeImage(){
        if (this.DefaultEpisodeImage==null){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }

    public static MediaArtifactType ConvertStringtoMediaArtifactType(String ImageType){
        ImageType = ImageType.toLowerCase();
        if (ImageType.equals("poster")){
            return MediaArtifactType.POSTER;
        }else if (ImageType.equals("banner")){
            return MediaArtifactType.BANNER;
        }else if (ImageType.equals("background")){
            return MediaArtifactType.BACKGROUND;
        }else{
            return MediaArtifactType.POSTER;
        }
    }
    
    
    
}
