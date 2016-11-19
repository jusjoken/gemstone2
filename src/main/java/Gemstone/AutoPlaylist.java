/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Gemstone;

import org.apache.log4j.Logger;
import sagex.UIContext;

import java.util.List;

/**
 *
 * @author SBANTA
 * - 04/04/2012 - updated for Gemstone...jusjoken
 * - 02/29/2016 - updated to better handle a list from a View result...jusjoken
 */
public class AutoPlaylist {
    static private final Logger LOG = Logger.getLogger(Source.class);

    public static void MakeTVPlaylistandPlay(List MediaList){
        //get the now playing playlist
        Object NewPlaylist = sagex.api.PlaylistAPI.GetNowPlayingList(new UIContext(sagex.api.Global.GetUIContextName()));
        //LOG.debug("MakeTVPlaylistandPlay: Start Playlist size '" + sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length + "'");
        Object[] PlaylistItems = sagex.api.PlaylistAPI.GetPlaylistItems(new UIContext(sagex.api.Global.GetUIContextName()), NewPlaylist);
        //empty the now playing playlist if there are any items
        if(PlaylistItems.length>0){
            sagex.api.PlaylistAPI.RemovePlaylist(new UIContext(sagex.api.Global.GetUIContextName()),NewPlaylist);
            NewPlaylist = sagex.api.PlaylistAPI.GetNowPlayingList(new UIContext(sagex.api.Global.GetUIContextName()));
        }
        //add the passed in list to the now playing playlist
        //LOG.debug("MakeTVPlaylistandPlay: After Playlist size '" + sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length + "'");
        for ( int i=0;i<MediaList.size();i++){
            LOG.debug("MakeTVPlaylistandPlay: Adding item '" + MediaList.get(i) + "'");
            sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,phoenix.media.GetMediaObject(MediaList.get(i)));
        }
        //tell the STV to play the nowplaying playlist
        //LOG.debug("MakeTVPlaylistandPlay: End Playlist size '" + sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length + "'");
	    api.AddStaticContext("CurPlaylist", NewPlaylist);
        api.ExecuteWidgeChain("BASE-50293");
    }

}


