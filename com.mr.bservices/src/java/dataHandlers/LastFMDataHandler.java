/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataHandlers;

/**
 *
 * @author Sajith
 */
import com.sun.javafx.scene.control.skin.VirtualFlow;
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
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private static HashMap<String, User> initialUsersInfo = new HashMap<>();
    private static HashMap<String, Tag> initailTags = new HashMap<>();
    private static HashMap<String, Artist> learnedArtists = new HashMap<>();

    private static HashMap<String, Song> learnedTracks = new HashMap<>();
    private static List<String> ignoredMbids = new ArrayList<>();

    public static void loadPreviousData() {
        currentTagID = 1;
        initailTags = loadLearnedTag();
        currentTagID = initailTags.size() + 1;
        learnedArtists = loadLearnedArtist();
        loadSavedTracks();
        loadIgnoredTracks();

    }

    public static void initiateUsers() {
        URL url = AccessLastFM.getURL("user.getFriends&"
                + "user=" + GlobalParam.getLastFMUserName()
                + "&limit=" + GlobalParam.getInitialUserCount());
        String userListXML = AccessLastFM.getResponsString(url);
        List<String> userList = AccessLastFM.extractPattern("<name>(.*?)</name>", userListXML);
        initUsers(userList);

    }

    private static void initUsers(List<String> userList) {
        LOGGER.log(Level.INFO, "Learing about users ");
        for (String userName : userList) {
            LOGGER.log(Level.INFO, "Knowing " + userName);
            User tempUser = setUserTaste(userName);
            tempUser.setUserName(userName);
            tempUser.setUserID(currentUserID);
            tempUser.filterTaste();
            tempUser = addUserTracks(tempUser);
            //System.out.println(tempUser.getTasteString(currentTagID-1));
            currentUserID++;
            initialUsers.add(tempUser);
            initialUsersInfo.put(tempUser.getUserID() + "", tempUser);
        }
    }

    public static User setUserTaste(String userName) {
        User tempUser = new UserLastFM(userName);
        URL url = AccessLastFM.getURL("user.getTopArtists&"
                + "user=" + userName + "&"
                + "limit=" + GlobalParam.getArtistCountPerUser());
        String userArtitsList = AccessLastFM.getResponsString(url);
        List<String> artistNameList = AccessLastFM.extractPattern("<name>(.*?)</name>", userArtitsList);
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
            List<String> tagList = AccessLastFM.extractPattern("<name>(.*?)</name>", artistTagListXML);
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
        if (reInitiateGraph) {
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
            List<String> tagList = AccessLastFM.extractPattern("<name>(.*?)</name>", artistTagListXML);
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
                        unknownTags.replace(tagName, tagOccurance + 1);
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

    public static User addUserTracks(User user) {
        try {
            URL tempURL = AccessLastFM.getURL("user.getLovedTracks&"
                    + "user=" + user.getUserName()
                    + "&limit=" + GlobalParam.getNumberOfTracksPerUser());
            String responseString = AccessLastFM.getResponsString(tempURL);
            List<String> mbidList = AccessLastFM.
                    extractPattern("<mbid>(.*?)</mbid>", responseString);
            for (String mbid : mbidList) {
                if (!ignoredMbids.contains(mbid)
                        && !learnedTracks.containsKey(mbid)) {
                    if (getTrackInformation(mbid)) {
                        user.addSong(mbid);
                    }

                } else if (learnedTracks.containsKey(mbid)) {
                    user.addSong(mbid);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "URL for song list is unreachable of broken");
        }
        return user;
    }

    public static boolean getTrackInformation(String mbid) {
        boolean trackInitiated = false;
        try {
            URL tempURL = AccessLastFM.getURL("track.getInfo&mbid=" + mbid);
            String responseString = AccessLastFM.getResponsString(tempURL);
            Song tempSong = new Song();
            tempSong.setTrackName(AccessLastFM.
                    extractPattern("<name>(.*?)</name>",
                            responseString).get(0));
            tempSong.setArtistName(AccessLastFM.
                    extractPattern("<artist>(.*?)</artist>",
                            responseString).get(0));
            tempSong.setTrackURL(AccessLastFM.
                    extractPattern("<url>(.*?)<url>",
                            responseString).get(0));
            String imageURL = "default";
            List<String> imageList = AccessLastFM.
                    extractPattern("<image size=\"large\">(.*?)</image>",
                            responseString);
            if (!imageList.isEmpty()) {
                imageURL = imageList.get(0);
            }
            tempSong.setMbid(mbid);
            tempSong.setImageURL(imageURL);
            learnedTracks.put(mbid, tempSong);
            saveSongInformation(tempSong);
            trackInitiated = true;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                    mbid + " does not belongs to a song");
            saveIgnoredMbid(mbid);
            ignoredMbids.add(mbid);
        }
        return trackInitiated;
    }

    public static void expandUserGraph() {
        System.out.println("New users are been added");
        List<String> tempUserList = new ArrayList<>();
        Set<String> tagNames = unknownTags.keySet();
        for (String tagName : tagNames) {
            int tagOccurance = unknownTags.get(tagName);
            if (tagOccurance >= GlobalParam.getLearningStartBound()) {
                List<String> userNames = getUsersForNewTags(tagName);
                for (String name : userNames) {
                    if (!tempUserList.contains(name)) {
                        tempUserList.add(name);
                    }
                }
            }
        }
        reInitiateGraph = false;
        initUsers(tempUserList);
    }

    public static List<String> getUsersForNewTags(String tagName) {
        List<String> tagUserList = new ArrayList<>();
        try {
            URL url = new URL("http://" + GlobalParam.getTagLastFMURL() + tagName);
            String responsString = AccessLastFM.getResponsString(url);

            Pattern searchPattern = Pattern.compile("href=\"/user/(.*?)\">\\1<");
            Matcher patternMatcher = searchPattern.matcher(responsString);

            while (patternMatcher.find()) {
                String item = patternMatcher.group();
                String name = item.substring(0, item.length() - 1).split(">")[1];
                tagUserList.add(name);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return tagUserList;
    }

    public static int saveSongInformation(Song song) {
        String sqlQ = "INSERT INTO song_information"
                + "(mbid,artist,song,song_url,img_url)"
                + "VALUES('" + song.getMbid() + "','" + song.getArtistName()
                + "','" + song.getTrackName() + "','" + song.getTrackURL()
                + "','" + song.getImageURL() + "')";
        return AccessDB.getDBConnection().saveData(sqlQ);
    }

    public static int saveIgnoredMbid(String mbid) {
        String sqlQ = "INSERT INTO ignored_mbids"
                + "(mbid) "
                + "VALUES('" + mbid + "')";
        return AccessDB.getDBConnection().saveData(sqlQ);
    }

    public static void saveTagInforamtion() {
        BufferedWriter tempWriter = getWriter(GlobalParam.getTagInfoFilePath());
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

    private static void loadSavedTracks() {
        String sqlQ = "Select mbid,artist,song,song_url,img_url from song_information";
        ResultSet rs = AccessDB.getDBConnection().getData(sqlQ);
        try {
            while (rs.next()) {
                Song tempSong = new Song();
                tempSong.setMbid(rs.getString("mbid"));
                tempSong.setArtistName(rs.getString("artist"));
                tempSong.setTrackName(rs.getString("song"));
                tempSong.setTrackURL(rs.getString("song_url"));
                tempSong.setImageURL(rs.getString("img_url"));

                learnedTracks.put(tempSong.getMbid(), tempSong);

            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Loading saved tracks failed");
        }
    }

    private static void loadIgnoredTracks() {
        String sqlQ = "Select mbid from ignored_mbids";
        ResultSet rs = AccessDB.getDBConnection().getData(sqlQ);
        try {
            while (rs.next()) {
                String mbid = rs.getString("mbid");
                ignoredMbids.add(mbid);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Loading ignored tracks failed");
        }
    }

    private static HashMap<String, Tag> loadLearnedTag() {
        String filePath = GlobalParam.getTagInfoFilePath();
        HashMap<String, Tag> tempMap = new HashMap<>();
        BufferedReader dataReader = getReader(filePath);
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

    public static User getUserName(String userID) {
        User user = initialUsersInfo.get(userID);
        return user;
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

    public static HashMap<String, Song> songList() {
        return learnedTracks;
    }

    public static Song getSong(String mbid) {
        return learnedTracks.get(mbid);
    }

    public static List<User> filterUsers(User targetUser, List<User> clusterusers) {
        List<User> filteredUsers = new ArrayList<>();
        HashMap<User, Double> userDistanceScores = new HashMap<>();
        List<Double> distanceSocres = new ArrayList<>();

        for (User user : clusterusers) {
            double score = getDistanceInformation(targetUser, user);
            userDistanceScores.put(user, score);
            distanceSocres.add(score);
        }

        double sumOfScores = 0.0;

        for (Double score : distanceSocres) {
            sumOfScores = sumOfScores + score;
        }
        double avrgScore = sumOfScores / distanceSocres.size();

        System.out.println("avrgScore:"+avrgScore);
        for (User user : userDistanceScores.keySet()) {
            System.out.println(user.getUserName()+" : "+userDistanceScores.get(user));
            if (userDistanceScores.get(user) >= avrgScore) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    public static double getDistanceInformation(User targetUser, User compUser) {

        double distnaceScore = 0.0;
        Set<Tag> targetUserTags = targetUser.getMusicTaste().keySet();
        Set<Tag> compUserTags = compUser.getMusicTaste().keySet();
        List<Tag> commonTags = new ArrayList<>();

        for (Tag t : targetUserTags) {
            if (compUserTags.contains(t)) {
                commonTags.add(t);
            }
        }

        if (commonTags.size() > 0) {
            double tagDistance = (double) commonTags.size() / currentTagID - 1;
            double tagDistance2 = (double) commonTags.size() / targetUserTags.size();

            double sum = 0.0;
            for (Tag t : commonTags) {
                int tCout = targetUser.getMusicTaste().get(t);
                int cCout = compUser.getMusicTaste().get(t);
                int def = tCout - cCout;
                sum = sum + (Math.pow(def, 2));
            }

            distnaceScore = (double) (1 / (1 + sum));

        }
        return distnaceScore;
    }
}
