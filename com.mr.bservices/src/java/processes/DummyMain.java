/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import dataHandlers.LastFMDataHandler;
import dataHandlers.DataClusterHandler;
import dataHandlers.FacebookDataHandler;
import java.util.List;
import java.util.HashMap;
import objectStructures.User;
import objectStructures.UserFacebook;
import objectStructures.Tag;
import com.restfb.*;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.awt.BorderLayout;
import java.util.ArrayList;
import objectStructures.Song;

/**
 *
 * @author Sajith
 */
public class DummyMain {

    public static void main(String[] args) {

        GlobalParam.initParameters();
        LastFMDataHandler.loadPreviousData();
        LastFMDataHandler.initiateUsers();
        LastFMDataHandler.createDataSheet();
        LastFMDataHandler.saveArtistInforamtion();
        LastFMDataHandler.saveTagInforamtion();
        HashMap<String,Song> songList = LastFMDataHandler.songList();
        String userFacebookID = "1255393874475776";
        String name = "Sajith Dananjaya";
        String accessToken = "CAAHiyQnHquQBAArZAn0VNSt3WiPTnqMGfKWhmNsbwSJiJ7V18OCFrncAP8M8C9z9Kq0nve3TRbGkF2Fxr8MR5AtnPlWMfwPzRmEK6DW7Qh0reXEHfDOZBGFhm1oV51zEUCZBlnmO753O3Q1brSBoWMcoBBDZAZAh2ZCeEfGFCvqVbAumkXqlgzWmsDqmJOUxgZD";
//        String userFacebookID = "10204612568952632";
//        String name = "Romesh shanaka";
//        String accessToken = "CAAHiyQnHquQBACmt6o64b7uiuRTdtlZBecYCoCRKWUulmWZAKFCLGRaI40wJW9Bba0PJv9j0PqjMkWjKiZBPyAwPt1fWS6ZBRw9lwnE2FNmWyvjOat0fT6QDtNt1GhcZCvH3IAobI3XHhgOQWsioRTuHTJPLeU3loBsqbZA50HUOEZBZAyArCSfXLbwpn3mAR5aL2iVdprGjfAZDZD";
        User facebookUser = FacebookDataHandler
                .setUserTaste(new UserFacebook(userFacebookID, name, accessToken));
        facebookUser.setUserID(LastFMDataHandler.getNewUserID());
        facebookUser.filterTaste();
        
        
       
        DataClusterHandler temp = new DataClusterHandler();
        temp.buildGraph(GlobalParam.getDataSetFilePath());
        String[] users = temp.getRelatedUsers(facebookUser);
        List<User> tempUsersList = new ArrayList<>();
        
        for(String ID: users){
            tempUsersList.add(LastFMDataHandler.getUserName(ID));
        }
        tempUsersList = LastFMDataHandler.filterUsers(facebookUser, tempUsersList);
        
        
        for(User user: tempUsersList){
            for(String songID:user.getUserSongList()){
                Song tempSong = LastFMDataHandler.getSong(songID);
                System.out.println(tempSong.getArtistName()+" : "+tempSong.getTrackName());
            }
            System.out.println(""); 
        }
        

    }

}
