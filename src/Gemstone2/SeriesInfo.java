/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Gemstone2;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author SBANTA
 * - 04/04/2012 - updated for Gemstone
 */
public class SeriesInfo {

    public static Map<String,Vector> seriesmap = new HashMap<String,Vector>();

    public static void main(String[] args){
    Object[] seriesmaps=sagex.api.SeriesInfoAPI.GetAllSeriesInfo();
    Map seriest=sagex.api.Database.GroupByMethod(seriesmaps,"GetSeriesTitle");
    Map<String,Vector> tester = seriest;


    System.out.println("Size of map="+tester.isEmpty());
    Object[] Files = sagex.api.MediaFileAPI.GetMediaFiles("TV");
    Object[] FilterFiles = (Object[]) sagex.api.Database.FilterByBoolMethod(Files,"Gemstone_MetadataCalls_IsMediaTypeTV", true);
    for(Object current:FilterFiles){
    Object series=sagex.api.ShowAPI.GetShowSeriesInfo(current);
    String Title=MetadataCalls.GetMediaTitle(current);
    System.out.println("Getting Show="+MetadataCalls.GetMediaTitle(current));
    if(series==null){
    System.out.println("Series is null getting hash");
    if(seriesmap.containsKey(Title)){
    System.out.println("series forund for="+Title);}}}
    }
    }

