/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Gemstone2;

import org.apache.log4j.Logger;
import sagex.UIContext;

/**
 *
 * @author SBANTA
 * - 04/04/2012 - updated for Gemstone
 */
public class AutoPlaylist {
    static private final Logger LOG = Logger.getLogger(Source.class);

    public static void MakeTVPlaylistandPlay(Object[] MediaObjects){
        Object NewPlaylist = sagex.api.PlaylistAPI.GetNowPlayingList(new UIContext(sagex.api.Global.GetUIContextName()));
        //LOG.debug("MakeTVPlaylistandPlay: Start Playlist size '" + sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length + "'");
        Object[] PlaylistItems = sagex.api.PlaylistAPI.GetPlaylistItems(new UIContext(sagex.api.Global.GetUIContextName()), NewPlaylist);
        if(PlaylistItems.length>0){
            sagex.api.PlaylistAPI.RemovePlaylist(new UIContext(sagex.api.Global.GetUIContextName()),NewPlaylist);
            NewPlaylist = sagex.api.PlaylistAPI.GetNowPlayingList(new UIContext(sagex.api.Global.GetUIContextName()));
        }
        //LOG.debug("MakeTVPlaylistandPlay: After Playlist size '" + sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length + "'");

        for ( int i=0;i<MediaObjects.length;i++){
            LOG.debug("MakeTVPlaylistandPlay: Adding item '" + MediaObjects[i] + "'");
            sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,MediaObjects[i]);
        }

//                    sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,sagex.api.MediaFileAPI.GetMediaFileForID(31));
//                    sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,sagex.api.MediaFileAPI.GetMediaFileForID(206));
//                    sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,sagex.api.MediaFileAPI.GetMediaFileForID(1275));


        //LOG.debug("MakeTVPlaylistandPlay: End Playlist size '" + sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length + "'");
	api.AddStaticContext("CurPlaylist", NewPlaylist);
        api.ExecuteWidgeChain("BASE-50293");
    }

}


