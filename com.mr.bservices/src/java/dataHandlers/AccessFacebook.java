/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

import objectStructures.User;
import objectStructures.UserFacebook;
import processes.GlobalParam;
import processes.LogFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.net.URLConnection;

import com.restfb.Version;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import com.restfb.Connection;
import com.restfb.types.Post;
import com.restfb.types.Page;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient.AccessToken;

/**
 *
 * @author Sajith
 */
public class AccessFacebook {

    private static final String BASE_URL = "https://" + GlobalParam.getBaseFBURL();
    private static final Logger LOGGER
            = LogFactory.getNewLogger(AccessLastFM.class.getName());

    public static String extendAccessToken(UserFacebook user) {
        FacebookClient fbClient = new DefaultFacebookClient(user.getAccessToken(), 
                Version.LATEST);
        AccessToken extendedAccessToken = fbClient.obtainExtendedAccessToken(GlobalParam.getFacebookAppID()
                , GlobalParam.getFacebookAppSecret());
        return extendedAccessToken.getAccessToken();
    }

    public static List<String> getArtistList(User user) {
        UserFacebook tempUser = ((UserFacebook) user);
        int artistCount = 0;
        int maxArtistCount = GlobalParam.getArtistCountPerUser();
        FacebookClient fbClient
                = new DefaultFacebookClient(tempUser.getAccessToken(), Version.LATEST);
        List<String> artistList = new ArrayList<>();
        Connection<Post> respons
                = fbClient.fetchConnection(tempUser.getUserName() + "/Music", Post.class,
                        Parameter.with("limit", GlobalParam.getArtistCountPerUser()));
        for (List<Post> page : respons) {
            for (Post post : page) {
                artistList.add(post.getName());
                artistCount++;
            }
            break;
        }
        return artistList;
    }

    public static List<String> getRecentArtistList(User user) {
        UserFacebook tempUser = ((UserFacebook) user);
        FacebookClient fbClient
                = new DefaultFacebookClient(tempUser.getAccessToken(), Version.LATEST);
        List<Post> musicActivities = new ArrayList<>();
        Connection<Post> respons
                = fbClient.fetchConnection(tempUser.getUserName() + "/feed", Post.class,
                        Parameter.with("limit", GlobalParam.getPostCountPerUser()));
        for (List<Post> page : respons) {
            for (Post p : page) {
                if (p.getStory() != null && p.getStory().contains(" listening to ")) {
                    musicActivities.add(p);
                }
            }
            break;
        }
        return extractArtistFromPost(musicActivities, user);
    }

    private static List<String> extractArtistFromPost(List<Post> postList, User user) {
        List<String> recentArtistList = new ArrayList<>();
        int postCount = 0;
        int maxPostCount = GlobalParam.getPostCountPerUser();
        LOGGER.log(Level.WARNING, "Some music information may not contain artist information");
        for (Post post : postList) {
            String stringURL = BASE_URL + post.getId() + "/attachments?access_token=" + ((UserFacebook) user).getAccessToken();
            try {
                URL url = new URL(stringURL);
                URLConnection respons = url.openConnection();
                String stringRespons = responsToString(respons);
                JSONArray jArray = new JSONArray(stringRespons.substring(8, stringRespons.length() - 1));
                JSONObject dataObject = jArray.getJSONObject(0);
                recentArtistList.add(dataObject.getString("description"));
                postCount++;
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Responce for " + post.getId() + " is broken or unavailable");
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "Post " + post.getId() + " dose not contains any aritist information");
            }
        }
        return recentArtistList;
    }

    private static String responsToString(URLConnection respons) {
        String stringRespons = "";
        try {
            stringRespons = new Scanner(respons.getInputStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Resopons converstion failed", e);
        }
        return stringRespons;
    }

}
