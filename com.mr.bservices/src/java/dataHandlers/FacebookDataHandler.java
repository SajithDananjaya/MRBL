/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

import java.util.List;
import objectStructures.User;
import objectStructures.UserFacebook;
import java.sql.ResultSet;

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

    public static UserFacebook getUserInformation(String userEmail, String password) {
        UserFacebook tempUser = null;
        String sqlQ = "Select * from user_information "
                + "where "
                + "email='" + userEmail + "' and password='" + password + "'";
        ResultSet rs = AccessDB.getDBConnection().getData(sqlQ);

        try {
            while (rs.next()) {
                String accountID=rs.getString("user_id");
                String displayName=rs.getString("profile_name");
                String accessToken=rs.getString("access_token");
                tempUser = new UserFacebook(accountID, displayName, accessToken);
                tempUser.setRelearnAvailable(rs.getBoolean("learn"));
            }
        } catch (Exception e) {
        }
        return tempUser;
    }

    public static void saveNewUserInformation(User user) {
        UserFacebook tempUser = ((UserFacebook) user);

        String sqlQ = "insert into user_information(user_id , profile_name , "
                + "access_token , token_exdate , reg_date , email , password , "
                + "playlist_track_count , learn)"
                + "values()";
        try {
            AccessDB.getDBConnection().saveData(sqlQ);

        } catch (Exception e) {
        }
    }

}
