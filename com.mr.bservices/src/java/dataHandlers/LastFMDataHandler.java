/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

import java.net.URL;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import org.w3c.dom.Document;
import java.util.logging.Logger;
import objectStructures.Tag;
import objectStructures.User;
import objectStructures.Song;
import objectStructures.Artist;
import objectStructures.UserLastFM;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import processes.GlobalParam;
import processes.LogFactory;

/**
 *
 * @author Sajith
 */
public class LastFMDataHandler {

    private static final Logger LOGGER
            = LogFactory.getNewLogger(AccessLastFM.class.getName());

    private static int currentTagID = 1;
    private static int currentUserID = 1;
    private static boolean reInitiateGraph = false;
    private static List<String> newUsers = new ArrayList<>();
    private static HashMap<String, Integer> unknownTags = new HashMap<>();
    private static List<User> initialUsers = new ArrayList<>();
    private static HashMap<String, String> initialUsersInfo = new HashMap<>();
    private static HashMap<String, Tag> initailTags = new HashMap<>();
    private static HashMap<String, Artist> learnedArtists = new HashMap<>();

    public static void loadPreviousData() {
        currentTagID = 1;
        initailTags = loadLearnedTag();
        currentTagID = initailTags.size() + 1;
        learnedArtists = loadLearnedArtist();

    }

    public static void initiateUsers() {
        URL url = AccessLastFM.getURL("user.getFriends&"
                + "user=" + GlobalParam.getLastFMUserName()
                + "&limit=" + GlobalParam.getInitialUserCount());
        String userListXML = AccessLastFM.getResponsString(url);
        List<String> userList = AccessLastFM.extractPattern("<name>(.*?)</name>", userListXML, 4);
        for (String userName : userList) {
            System.out.println("Learning of user " + userName);
            User tempUser = setUserTaste(userName);
            tempUser.setUserName(userName);
            tempUser.setUserID(currentUserID);
            tempUser.filterTaste();
            //System.out.println(tempUser.getTasteString(currentTagID-1));
            currentUserID++;
            initialUsers.add(tempUser);
            initialUsersInfo.put(tempUser.getUserID() + "", userName);
        }
    }

    public static User setUserTaste(String userName) {
        User tempUser = new UserLastFM(userName);
        URL url = AccessLastFM.getURL("user.getTopArtists&"
                + "user=" + userName + "&"
                + "limit=" + GlobalParam.getArtistCountPerUser());
        String userArtitsList = AccessLastFM.getResponsString(url);
        List<String> artistNameList = AccessLastFM.extractPattern("<name>(.*?)</name>", userArtitsList, 4);
        return addUserTagsLastFM(tempUser, artistNameList);
    }

    public static User addUserTagsLastFM(User user, List<String> artistNameList) {
        for (String artistName : artistNameList) {
            //System.out.println(artistName);
            List<Tag> artistTags = getLastFMUserArtistInformation(artistName);
            for (Tag tag : artistTags) {
                user.setMusicTaste(tag);
            }
        }
        return user;
    }

    public static List<Tag> getLastFMUserArtistInformation(String artistName) {
        if (!learnedArtists.containsKey(artistName)) {
            // System.out.println("Learning about "+artistName);
            URL url = AccessLastFM.getURL("artist.getTopTags&"
                    + "artist=" + artistName + "&"
                    + "limit=" + GlobalParam.getTagCountPerArtist());
            String artistTagListXML = AccessLastFM.getResponsString(url);
            List<String> tagList = AccessLastFM.extractPattern("<name>(.*?)</name>", artistTagListXML, 4);
            Artist tempArtist = new Artist(artistName);
            for (String tagName : tagList) {
                if (!initailTags.containsKey(tagName)) {
                    Tag tempTag = new Tag(currentTagID, tagName);
                    initailTags.put(tagName, tempTag);
                    currentTagID++;
                }
                tempArtist.addArtistTag(initailTags.get(tagName));
            }
            learnedArtists.put(artistName, tempArtist);
        }
        return learnedArtists.get(artistName).getArtistTags();
    }

    public static User addUserTagsFacebook(User user, List<String> artistNameList) {
        for (String artistName : artistNameList) {
            //System.out.println(artistName);
            List<Tag> artistTags = getFBUserArtistInformation(artistName);
            for (Tag tag : artistTags) {
                user.setMusicTaste(tag);
            }
        }
        if(reInitiateGraph){
            expandUserGraph();
        }
        return user;
    }

    public static List<Tag> getFBUserArtistInformation(String artistName) {
        if (!learnedArtists.containsKey(artistName)) {
            // System.out.println("Learning about "+artistName);
            URL url = AccessLastFM.getURL("artist.getTopTags&"
                    + "artist=" + artistName + "&"
                    + "limit=" + GlobalParam.getTagCountPerArtist());
            String artistTagListXML = AccessLastFM.getResponsString(url);
            List<String> tagList = AccessLastFM.extractPattern("<name>(.*?)</name>", artistTagListXML, 4);
            Artist tempArtist = new Artist(artistName);
            for (String tagName : tagList) {
                if (!initailTags.containsKey(tagName)) {
                    if (unknownTags.containsKey(tagName)) {
                        int tagOccurance = unknownTags.get(tagName);
                        if (tagOccurance >= GlobalParam.getLearningStartBound()) {
                            Tag tempTag = new Tag(currentTagID, tagName);
                            initailTags.put(tagName, tempTag);
                            currentTagID++;
                            tempArtist.addArtistTag(initailTags.get(tagName));
                            reInitiateGraph = true;
                        }
                        unknownTags.replace(tagName, tagOccurance+1);
                    } else {
                        unknownTags.put(tagName, 1);
                    }
                } else {
                    tempArtist.addArtistTag(initailTags.get(tagName));
                }
            }
            learnedArtists.put(artistName, tempArtist);
        }
        return learnedArtists.get(artistName).getArtistTags();
    }
    
    public static void expandUserGraph(){
        
        System.out.println("Learing about tags :");
        
        for(String tagName: unknownTags.keySet()){
            if(unknownTags.get(tagName)>=GlobalParam.getLearningStartBound()){
                System.out.println("Learning about : "+tagName);
            }
        }
    }

    public static void saveTagInforamtion() {
        BufferedWriter tempWriter = getWriter(GlobalParam.getTagInfoFilePath());
        LOGGER.log(Level.WARNING, "File path may be invalid for saving tags information");
        try {
            for (String tagName : initailTags.keySet()) {
                Tag t = initailTags.get(tagName);
                String data = t.getTagID() + "," + t.getTagName();
                // System.err.println(data);
                tempWriter.write(data);
                tempWriter.newLine();
            }
            tempWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for saving tags is invalid", e);
        }
    }

    public static void saveUserInformation() {
        BufferedWriter tempWriter = getWriter("./learnedUsers.txt");
        LOGGER.log(Level.WARNING, "File path may be invalid for saving user information");
        try {
            for (User user : initialUsers) {
                String data = user.getUserID() + ","
                        + user.getUserName()
                        + user.getTasteString(currentTagID - 1);
                // System.err.println(data);
                tempWriter.write(data);
                tempWriter.newLine();
            }
            tempWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for saving users is invalid", e);
        }
    }

    public static void saveArtistInforamtion() {
        BufferedWriter tempWriter = getWriter(GlobalParam.getArtistInfoFilePath());
        LOGGER.log(Level.WARNING, "File path may be invalid for saving user information");
        try {
            for (String artistName : learnedArtists.keySet()) {
                try {
                    Artist artist = learnedArtists.get(artistName);
                    String data = artist.getArtistName() + artist.getTagListString();
                    // System.err.println(data);
                    tempWriter.write(data);
                    tempWriter.newLine();
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Saving " + artistName + " failed");
                }
            }
            tempWriter.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for saving artists is invalid", e);
        }
    }

    private static HashMap<String, Tag> loadLearnedTag() {
        String filePath = GlobalParam.getTagInfoFilePath();
        HashMap<String, Tag> tempMap = new HashMap<>();
        BufferedReader dataReader = getReader(filePath);
        LOGGER.log(Level.WARNING, "File path may be invalid for learning tags information");
        try {
            String dataLine = "";

            while ((dataLine = dataReader.readLine()) != null) {
                String[] data = dataLine.split(",");
                // System.out.println(dataLine);
                int tagID = Integer.parseInt(data[0]);
                String tagName = data[1];
                Tag tempTag = new Tag(tagID, tagName);
                // System.out.println(tempTag.getTagID());
                tempMap.put(tagName, tempTag);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for learning tags is invalid", e);
        }

        return tempMap;

    }

    private static HashMap<String, Artist> loadLearnedArtist() {
        String filePath = GlobalParam.getArtistInfoFilePath();
        HashMap<String, Artist> tempMap = new HashMap<>();
        BufferedReader dataReader = getReader(filePath);
        LOGGER.log(Level.WARNING, "File path may be invalid for learning artists information");
        try {
            String dataLine = "";

            while ((dataLine = dataReader.readLine()) != null) {
                String[] data = dataLine.split(",");
                Artist tempArtist = new Artist(data[0]);
                // System.out.println(dataLine);
                if (data.length > 1) {
                    for (int index = 1; index < data.length; index++) {
                        if (initailTags.containsKey(data[index])) {
                            Tag tag = initailTags.get(data[index]);
                            tempArtist.addArtistTag(tag);
                        }
                    }
                }
                tempMap.put(tempArtist.getArtistName(), tempArtist);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for learning artists is invalid", e);
        }

        return tempMap;

    }

    public static void createDataSheet() {
        BufferedWriter tempWriter = getWriter(GlobalParam.getDataSetFilePath());
        LOGGER.log(Level.WARNING, "File path may be invalid for saving dataset information");
        try {
            tempWriter.write("@relation dataSet");
            tempWriter.newLine();
            tempWriter.newLine();

            tempWriter.write("@attribute userID numeric");
            tempWriter.newLine();

            for (int index = 0; index < currentTagID - 1; index++) {
                tempWriter.write("@attribute tag" + index + " numeric");
                tempWriter.newLine();
            }

            tempWriter.newLine();
            tempWriter.write("@data");
            tempWriter.newLine();

            for (User user : initialUsers) {
                tempWriter.write(user.getUserID() + user.getTasteString(currentTagID - 1));
                tempWriter.newLine();
            }
            tempWriter.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path for saving dataset is invalid", e);
        }
    }

    private static BufferedWriter getWriter(String filePath) {
        File tempFile = new File(filePath);
        BufferedWriter bufferedWriter = null;
        LOGGER.log(Level.WARNING, "File path may be invalid ");
        try {
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(tempFile.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path is invalid", e);
        }
        return bufferedWriter;
    }

    private static BufferedReader getReader(String filePath) {
        File tempFile = new File(filePath);
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(tempFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File path is invalid", e);
        }
        return new BufferedReader(fileReader);
    }

    public static String getUserName(String userID) {
        String userName = initialUsersInfo.get(userID);
        return userName;
    }

    public static int getNewUserID() {
        int tempID = currentUserID;
        currentUserID++;
        return tempID;
    }

    public static int getTagCount() {
        return (currentTagID - 1);
    }

    public static HashMap<String, Artist> getKnownArtist() {
        return learnedArtists;
    }

    public static HashMap<String, Tag> getKnownTags() {
        return initailTags;
    }

    public static List<User> getUserList() {
        return initialUsers;
    }

}
