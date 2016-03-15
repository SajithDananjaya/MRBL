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

/**
 *
 * @author Sajith
 */
public class DummyMain {

    public static void main(String[] args) {

        GlobalParam.initParameters();
        //LastFMDataHandler.loadPreviousData();
        LastFMDataHandler.initiateUsers();
//        String userFacebookID = "1255393874475776";
//        String name = "Sajith Dananjaya";
//        String accessToken = "";
//
//        User facebookUser = FacebookDataHandler
//                .setUserTaste(new UserFacebook(userFacebookID, name, accessToken));
//        facebookUser.setUserID(LastFMDataHandler.getNewUserID());
//        facebookUser.filterTaste();

    }

}
