/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Gemstone;

import sagex.UIContext;

/**
 *
 * @author SBANTA
 * - 04/04/2012 - updated for Gemstone
 */
public class AutoPlaylist {

    public static void MakeTVPlaylistandPlay(Object[] MediaObjects){
     Object NewPlaylist = sagex.api.PlaylistAPI.GetNowPlayingList(new UIContext(sagex.api.Global.GetUIContextName()));
    Object[] PlaylistItems = sagex.api.PlaylistAPI.GetPlaylistItems(new UIContext(sagex.api.Global.GetUIContextName()), NewPlaylist);
    if(PlaylistItems.length>0){
    sagex.api.PlaylistAPI.RemovePlaylist(new UIContext(sagex.api.Global.GetUIContextName()),NewPlaylist);
    NewPlaylist = sagex.api.PlaylistAPI.GetNowPlayingList(new UIContext(sagex.api.Global.GetUIContextName()));

    }


		for ( int i=0;i<MediaObjects.length;i++){
                    System.out.println("Adding Trailer"+MediaObjects[i]);
		 sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,MediaObjects[i]);
		}

//                    sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,sagex.api.MediaFileAPI.GetMediaFileForID(31));
//                    sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,sagex.api.MediaFileAPI.GetMediaFileForID(206));
//                    sagex.api.PlaylistAPI.AddToPlaylist(NewPlaylist,sagex.api.MediaFileAPI.GetMediaFileForID(1275));


		 System.out.println("Playlist Size Before pass="+sagex.api.PlaylistAPI.GetPlaylistItems(NewPlaylist).length);
	api.AddStaticContext("CurPlaylist", NewPlaylist);
        api.ExecuteWidgeChain("BASE-50293");
    }

    }


