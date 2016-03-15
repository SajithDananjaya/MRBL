/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

import java.util.List;
import objectStructures.User;

/**
 *
 * @author Sajith
 */
public class FacebookDataHandler {

    public static User setUserTaste(User user) {
        List<String> artistNameList = AccessFacebook.getArtistList(user);
        artistNameList.addAll(AccessFacebook.getRecentArtistList(user));
        User tempUser = LastFMDataHandler.addUserTagsFacebook(user, artistNameList);
        tempUser.setUserID(LastFMDataHandler.getNewUserID());
        return tempUser;
    }

}
