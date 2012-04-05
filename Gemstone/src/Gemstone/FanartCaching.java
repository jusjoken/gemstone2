/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author SBANTA
 * - 04/04/2012 - updated for Gemstone
 */
public class FanartCaching {

    static private final Logger LOG = Logger.getLogger(FanartCaching.class);
//    public static HashMap<String,HashMap<Object,FanartCacheObject>> FanartCacheMap = new HashMap<String,HashMap<Object,FanartCacheObject>> ();
    public static final Pattern MetaImage = Pattern.compile("id=([0-9]+)\\s");
    public static String CacheLocation = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/CacheLocation", "cache/GemstoneImages/");
//      public static String CacheLocation = "CacheTest/Diamond/";
    public static final String Sep = java.io.File.separator;
    public static String BackdropName = sagex.api.Configuration.GetServerProperty(Const.BaseProp + Const.PropDivider + "Fanart/BackgroundName", "Backdrop.jpg");
    public static String PosterName = sagex.api.Configuration.GetServerProperty(Const.BaseProp + Const.PropDivider + "Fanart/PosterName", "Poster.jpg");
    public static String PosterSize = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/PosterSize", "300x200");
    public static String BannerSize = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/BannerSize", "300x200");
    public static String BackgroundSize = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/BackgroundSize", "1280x720");
    public static Boolean CheckPosterFolderFirst = Boolean.parseBoolean(sagex.api.Configuration.GetServerProperty(Const.BaseProp + Const.PropDivider + "Fanart/CheckPosterFolderFirst", "false"));
    public static Boolean CheckBackgroundFolderFirst = Boolean.parseBoolean(sagex.api.Configuration.GetServerProperty(Const.BaseProp + Const.PropDivider + "Fanart/CheckBackgroundFolderFirst", "false"));
    public static String CurrentlyCachingType = "";
    public static String CurrentlyCaching = "";
    public static int CachingTotal = 0;
    public static int CachingCurrent = 0;
    public static boolean IsCachingActive = false;
    public static int CurrentFanartVersion = 4;

    public static int GetCurrentFanartVersion() {
        return CurrentFanartVersion;
    }

    public static Object[] GetCachingStatus() {
        return new Object[]{CurrentlyCachingType, CurrentlyCaching, CachingTotal, CachingCurrent};
    }

    public static boolean IsCachingActive() {
        return IsCachingActive;
    }

    public static int[] GetBackgroundSize() {
        String[] s = BackgroundSize.split("x");
        return new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])};
    }

    public static void setBackdropName(String Name) {
        BackdropName = Name;
    }

    public static void setPosterName(String Name) {
        PosterName = Name;
    }

    public static void setBackgroundSize() {
        BackgroundSize = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/BackgroundSize", "1920x1080");
    }

    public static int[] GetBackgroundSizeTMB() {
        String[] s = BackgroundSize.split("x");
        return new int[]{Integer.parseInt(s[0]) / 4, Integer.parseInt(s[1]) / 4};
    }

    public static int[] GetBannerSize() {
        String[] s = BannerSize.split("x");
        return new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])};
    }

    public static void setBannerSize() {
        BannerSize = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/BannerSize", "300x200");
    }

    public static int[] GetPosterSize() {
        String[] s = PosterSize.split("x");
        return new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])};
    }

    public static void setPosterSize() {
        PosterSize = sagex.api.Configuration.GetProperty(Const.BaseProp + Const.PropDivider + "Fanart/PosterSize", "300x200");
    }

    public static void DeleteAllCache() {
        File Location = new File(CacheLocation);
        DeleteAllFiles(Location);
        CachingUserRecord.DeleteStoredLocations();

    }

    public static String[] GetAllBackgroundNames() {
        return sagex.api.Configuration.GetServerProperty(Const.BaseProp + Const.PropDivider + "Fanart/BackgroundNames", "background.png;background.jpg;fanart.jpg;fanart.png;backdrop.jpg;backdrop.png").split(";");
    }

    public static String[] GetAllPosterNames() {
        return sagex.api.Configuration.GetServerProperty(Const.BaseProp + Const.PropDivider + "Fanart/PosterNames", "poster.png;poster.jpg;folder.jpg;folder.png;cover.jpg;cover.png").split(";");
    }

    public static void main(String[] args) {
        Object[] mediafiles = sagex.api.MediaFileAPI.GetMediaFiles("DL");
        for (Object curr : mediafiles) {
            //GetCachedFanart(curr, false, "poster");
        }
    }

    public static void RegenerateCachedFanart(Object MediaFile) {

        String id = sagex.phoenix.fanart.FanartUtil.createSafeTitle(MetadataCalls.GetFanartTitle(MediaFile));
        System.out.println("Checking for stored fanart to clear for title=" + id);
        if (CachingUserRecord.HasStoredLocation(id)) {
            System.out.println("RecordExist for Fanart delete and clearing all caching records for diamond");
            ArrayList<File> CachedFanarts = CachingUserRecord.GetAllCacheLocationsForID(id);
            System.out.println("Total number of fanart cached for record=" + CachedFanarts.size());
            for (File curr : CachedFanarts) {
                System.out.println("Deleting Image=" + curr);
//        sagex.api.Utility.UnloadImage(new UIContext(sagex.api.Global.GetUIContextName()),curr.toString());
                curr.delete();

            }
            CachingUserRecord.DeleteStoresForID(id);
        }

    }

//    public static Object GetCachedFanart(Object MediaFile, Boolean series, String Type) {
//        boolean MT = MetadataCalls.IsMediaTypeTV(MediaFile);
//        String id = MetadataCalls.GetFanartTitle(MediaFile);
//        if (Type.equalsIgnoreCase("episode")) {
//            id = sagex.api.ShowAPI.GetShowEpisode(MediaFile) + "-" + MetadataCalls.GetSeasonNumberPad(MediaFile) + "x" + MetadataCalls.GetEpisodeNumberPad(MediaFile);
//        }
//        String storeid = sagex.phoenix.fanart.FanartUtil.createSafeTitle(MetadataCalls.GetFanartTitle(MediaFile));
//        id = MT && !series && !Type.equalsIgnoreCase("Background") && !Type.equalsIgnoreCase("episode") ? sagex.phoenix.fanart.FanartUtil.createSafeTitle(id) + "_season" + MetadataCalls.GetSeasonNumberPad(MediaFile) : sagex.phoenix.fanart.FanartUtil.createSafeTitle(id);
//        String CT = MT ? "TV" : "Movies";
//        String FT = Type.equalsIgnoreCase("Background") ? "" : Type.equalsIgnoreCase("Background_Thumb") ? "TMB" : Type.equalsIgnoreCase("episode") ? "thumb_" : series ? "series_" : MT ? "season_" : "";
//
//        if (!CachingUserRecord.HasStoredLocation(storeid, id + FT + Type.toLowerCase())) {
//            System.out.println("Caching Does Not Exist Yet for=" + storeid);
//            String Dir = CacheLocation + Sep + CT + Sep + FT + Type.toLowerCase() + "s" + Sep;
//            if (Type.equalsIgnoreCase("episode")) {
//                Dir = Dir + storeid + Sep;
//            }
//            if (!new File(Dir).exists()) {
//                new File(Dir).mkdirs();
//            }
//            String CD = Dir + id + ".jpg";
//            File Image = new File(CD);
////        if (Image.exists()) {
////            return Image;
////        }
////        Boolean NoFanart=sagex.api.MediaFileAPI.GetMediaFileMetadata(MediaFile, "HasDiamond"+FT + Type.toLowerCase()).equals("true");
////        if(NoFanart) {
//            Boolean FanartExist = CreateFanart(MediaFile, series, Type, CD);
//            if (FanartExist) {
//                System.out.println("Setting DiamondCache to has Fanart=" + id);
//                CachingUserRecord.setStoredLocation(storeid, id + FT + Type.toLowerCase(), Image.toString());
//            } //        }
//            //        if (Image.exists()) {
//            else {
//                if (sagex.api.MediaFileAPI.IsTVFile(MediaFile)) {
//                    if (!sagex.api.MediaFileAPI.IsFileCurrentlyRecording(MediaFile)) {
//                        System.out.println("Setting Diamond Cache to no Fanart=" + id);
//                        CachingUserRecord.setStoredLocation(storeid, id + FT + Type.toLowerCase(), "false");
//                    }
//                } else {
//                    System.out.println("Setting Diamond Cache to no Fanart=" + id);
//                    CachingUserRecord.setStoredLocation(storeid, id + FT + Type.toLowerCase(), "false");
//                }
//            }
//        }
//
//        String Location = CachingUserRecord.GetStoredLocation(storeid, id + FT + Type.toLowerCase());
//        if (Location.equals("false")) {
//            return null;
//        }
//        return Location;
////        } else {
////            return null;
////        }
//    }

//    public static boolean CreateFanart(Object MediaFile, Boolean series, String Type, String CD) {
//        Object Fanart = null;
//        int[] dims = new int[2];
//        if (Type.equalsIgnoreCase("banner")) {
//            dims = GetBannerSize();
//            Fanart = GetFanartBanner(MediaFile, series);
//        } else if (Type.equalsIgnoreCase("poster")) {
//            dims = GetPosterSize();
//            Fanart = GetFanartPoster(MediaFile, series);
//        } else if (Type.equalsIgnoreCase("episode")) {
//            dims[0] = 400;
//            dims[1] = 222;
//            Fanart = GetEpisodeThumb(MediaFile);
//        } else {
//            Fanart = GetFanartBackground(MediaFile);
//            if (Type.contains("BackgroundThumb")) {
//                dims = GetBackgroundSizeTMB();
//            } else {
//                dims = GetBackgroundSize();
//            }
//        }
//        if (Fanart != null) {
//
//
//            ScaleImage.scale(Fanart.toString(), dims[0], dims[1], CD);
//            return new File(CD).exists();
//        } else {
//            return false;
//        }
//    }

    public static Object GetEpisodeThumb(Object MediaObject) {
        Object Fanart = null;
        Fanart = phoenix.fanart.GetDefaultEpisode(MediaObject);
        return Fanart;
    }

    public static Object GetFanartPoster(Object MediaObject, boolean series) {
        Object Fanart = null;
        if (CheckPosterFolderFirst) {
            Fanart = GetFolderPoster(MediaObject, series);
        }

        if (Fanart == null) {
            Fanart = GetPhoenixPoster(MediaObject, series);
        }

        if (Fanart == null && !CheckPosterFolderFirst) {
            Fanart = GetFolderPoster(MediaObject, series);
        }

        return Fanart;

    }

    public static Object GetPhoenixPoster(Object MediaObject, boolean series) {
        if (phoenix.fanart.HasFanartPoster(MediaObject)) {
            if (series) {
                System.out.println("HashFanart Getting Series");
                return phoenix.fanart.GetFanartArtifact(MediaObject, "tv", null, "poster", null, new HashMap<String, String>());
            } else {
                System.out.println("HashFanart Getting Season");
                return phoenix.fanart.GetFanartPoster(MediaObject);
            }
        } else {
            return null;
        }
    }

    public static Object GetFolderPoster(Object MediaObject, boolean series) {
        File FolderArt = new File(sagex.api.MediaFileAPI.GetParentDirectory(MediaObject) + Sep + PosterName);
        System.out.println("Checking for folder art at=" + FolderArt);
        if (FolderArt.exists()) {
            System.out.println("Folder art exist for file=" + sagex.api.AiringAPI.GetAiringTitle(MediaObject));
            return FolderArt;
        }
        System.out.println("No folder found for file=" + sagex.api.AiringAPI.GetAiringTitle(MediaObject));
        return null;


    }

    public static Object GetFanartBackground(Object MediaObject) {
        Object Fanart = null;
        if (CheckBackgroundFolderFirst) {
            Fanart = GetFolderBackground(MediaObject);
        }

        if (Fanart == null) {
            Fanart = GetPhoenixBackground(MediaObject);
        }

        if (Fanart == null && !CheckBackgroundFolderFirst) {
            Fanart = GetFolderBackground(MediaObject);
        }

        return Fanart;
    }

    public static Object GetPhoenixBackground(Object MediaObject) {

        if (phoenix.fanart.HasFanartBackground(MediaObject)) {
            return phoenix.fanart.GetFanartBackground(MediaObject);
        } else {
            return null;
        }
    }

    public static Object GetFolderBackground(Object MediaObject) {

        File FolderArt = new File(sagex.api.MediaFileAPI.GetParentDirectory(MediaObject) + Sep + BackdropName);
        System.out.println("Checking for folder BG art at=" + FolderArt);
        if (FolderArt.exists()) {
            System.out.println("Folder BG art exist for file=" + sagex.api.AiringAPI.GetAiringTitle(MediaObject));
            return FolderArt;
        }
        System.out.println("No BGFanart folder found for file=" + sagex.api.AiringAPI.GetAiringTitle(MediaObject));
        return null;


    }

    public static Object GetFanartBanner(Object MediaObject, boolean series) {
        if (phoenix.fanart.HasFanartBanner(MediaObject)) {
            if (series) {
                return phoenix.fanart.GetFanartArtifact(MediaObject, "tv", null, "banner", null, new HashMap<String, String>());
            } else {
                return phoenix.fanart.GetFanartBanner(MediaObject);
            }
        }
        return null;
    }

//    public static String GetImageFromMetaImage(String image) {
//        String id = "";
//        // parse a unique id from the sage meta image object
//        Matcher m = MetaImage.matcher(String.valueOf(image));
//        if (m.find()) {
//            id = m.group(1);
//            System.out.println("Using ImageId: " + id + " for Image: " + image);
//        } else {
//            m = MetaImage.matcher(String.valueOf(image));
//            if (m.find()) {
//                id = "MF-" + m.group(1);
//                System.out.println("Using ImageId: " + id + " for Image: " + image);
//            } else {
//                System.out.println("Creating Image Hash from toString() for Image: " + image);
//                id = DigestUtils.md5Hex(String.valueOf(image));
//            }
//        }
//
//        return id;
//    }
    public static Object[] toArray(Object Arr) /*
     * returns an Object Array of a passed in Vector, Array, List
     * (converts to type Object[])
     *
     * @param Array Array, List, Vector, or Map
     * -in case of Map returns an array of the KeySet
     */ {
        try {
            if (Arr.getClass().isArray()) {
                return (Object[]) Arr;
            } else if (Arr instanceof Vector) {
                return ((Vector) Arr).toArray();
            } else if (Arr instanceof List) {
                return ((List) Arr).toArray();
            } else if (Arr instanceof Map) {
                return (((Map) Arr).keySet()).toArray();
            } else {
                System.out.println("toArray(): Unknown Array Type");
                return null;
            }
        } catch (Exception e) {
            System.out.println("toArray(): Failed!");
            return null;
        }
    }

    public static String GetImageName(String Name) {
        return Name.substring(Name.lastIndexOf("\\") + 1, Name.length());
    }

    private static void DeleteAllFiles(File CurFolder) {
        File[] AllFiles = CurFolder.listFiles();
        for (File curr : AllFiles) {
            if (curr.isDirectory()) {
                DeleteAllFiles(curr);
            }

            curr.delete();

        }

    }

//    public static void CacheAllFanart() {
//        IsCachingActive = true;
//        Object[] Media = sagex.api.MediaFileAPI.GetMediaFiles("TVDBL");
//        Object[] TVFiles = (Object[]) sagex.api.Database.FilterByBoolMethod(Media, "Diamond_MetadataCalls_IsMediaTypeTV", true);
//        Object[] MovieFiles = (Object[]) sagex.api.Database.FilterByBoolMethod(Media, "Diamond_MetadataCalls_IsMediaTypeTV", false);
//        System.out.println("GettingFanartForallMovies size=" + MovieFiles.length);
//        CurrentlyCaching = "Movies";
//        CachingTotal = MovieFiles.length;
//        CurrentlyCachingType = "Posters";
//        int i = 1;
//        for (Object curr : MovieFiles) {
//            GetCachedFanart(curr, false, "Poster");
//            CachingCurrent = i;
//            i++;
//        }
//        CurrentlyCachingType = "Backgrounds";
//        CachingCurrent = i;
//        i = 1;
//        for (Object curr : MovieFiles) {
//            GetCachedFanart(curr, false, "Background");
//            CachingCurrent = i;
//            i++;
//        }
//        CurrentlyCachingType = "BackgroundThumb";
//        i = 1;
//        for (Object curr : MovieFiles) {
//            GetCachedFanart(curr, false, "BackgroundThumb");
//            CachingCurrent = i;
//            i++;
//        }
//        i = 0;
//        CurrentlyCaching = "TV";
//        Map<String, Vector> TVGrouped = sagex.api.Database.GroupByMethod(TVFiles, "Diamond_MetadataCalls_GetFanartTitle");
//        Set<String> Shows = TVGrouped.keySet();
//        CachingTotal = Shows.size();
//        for (String curr : Shows) {
//            CurrentlyCachingType = curr + " Series Fanarts";
//            CachingCurrent = i;
//            Vector shows = TVGrouped.get(curr);
//            Object FirstShow = shows.get(0);
//            GetCachedFanart(FirstShow, true, "Poster");
//            GetCachedFanart(FirstShow, true, "Banner");
//            GetCachedFanart(FirstShow, true, "Background");
//            GetCachedFanart(FirstShow, true, "BackgroundThumb");
//            CurrentlyCachingType = curr + " Episode Fanarts";
//            for (Object episode : shows) {
//                GetCachedFanart(episode, false, "Poster");
//                GetCachedFanart(episode, false, "Banner");
//                GetCachedFanart(episode, false, "episode");
//            }
//            i++;
//        }
//
//
//        IsCachingActive = false;
//
//    }
}
