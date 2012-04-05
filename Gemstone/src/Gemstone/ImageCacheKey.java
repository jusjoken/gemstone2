/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

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

    public ImageCacheKey() {
    }

    public ImageCacheKey(String ImagePath) {
        this.ImagePath = ImagePath;
    }
    
    public ImageCacheKey(String ImagePath, Boolean OriginalSize) {
        this.ImagePath = ImagePath;
        this.OriginalSize = OriginalSize;
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, MediaArtifactType ArtifactType) {
        this.ImagePath = ImagePath;
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ArtifactType;
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, String ArtifactType) {
        this.ImagePath = ImagePath;
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ConvertStringtoMediaArtifactType(ArtifactType);
    }
    public ImageCacheKey(String ImagePath, Boolean OriginalSize, String ArtifactType, Boolean KeepFilenameOnKey) {
        this.ImagePath = ImagePath;
        this.OriginalSize = OriginalSize;
        this.ArtifactType = ConvertStringtoMediaArtifactType(ArtifactType);
        this.KeepFilenameOnKey = KeepFilenameOnKey;
    }

    @Override
    public String toString() {
        return "ImageCacheKey{" + "ImagePath=" + ImagePath + ", OriginalSize=" + OriginalSize + ", ArtifactType=" + ArtifactType + ", Key=" + getKey() + ", DefaultEpisodeImage=" + DefaultEpisodeImage + ", defaultImage=" + defaultImage + '}';
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

    public void setRefreshAll(Boolean RefreshAll) {
        this.RefreshAll = RefreshAll;
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
