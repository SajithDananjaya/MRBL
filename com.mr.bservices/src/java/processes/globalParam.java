/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Sajith
 */
public class globalParam {

    private static final String CONFIG_FILE_PATH = "./configs/config";
    private static boolean paramInitiated = false;

    //Key is the param name, Object is param value 
    private static HashMap<String, String> paramMap;

    private static String lastFMUserName;
    private static int artistCountPerUser;
    private static int tagCountPerArtist;
    private static int postCountPerUser;

    private static String dbName;
    private static String dbUser;
    private static String dbPassword;

    private static String baseFBURL;
    private static String baseLastFMURL;

    private static String logFilePath;
    private static String artistInfoFilePath;
    private static String tagInfoFilePath;
    private static String userInfoFilePath;
    private static String datSetFilePath;

    private globalParam() {
    }

    public static void initParameters() {
        paramMap = new HashMap<>();
        BufferedReader file = getConfigFile();
        try{
            String textLine = "";
            while((textLine = file.readLine()) != null){
                String[] data = textLine.split(":");
                paramMap.put(data[0], data[1]);
            }
        }catch(IOException e){
            System.err.println(e.toString());
        }

    }

    private static BufferedReader getConfigFile() {
        File tempFile = new File(CONFIG_FILE_PATH);
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(tempFile);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return new BufferedReader(fileReader);
    }

    /**
     * @return the lastFMUserName
     */
    public static String getLastFMUserName() {
        return paramMap.get("lastFMUserName");
    }

    /**
     * @return the artistCountPerUser
     */
    public static int getArtistCountPerUser() {
        return Integer.parseInt(paramMap.get("lastFMUserName"));
    }

    /**
     * @return the tagCountPerArtist
     */
    public static int getTagCountPerArtist() {
        return tagCountPerArtist;
    }

    /**
     * @return the postCountPerUser
     */
    public static int getPostCountPerUser() {
        return postCountPerUser;
    }

    /**
     * @return the dbName
     */
    public static String getDbName() {
        return dbName;
    }

    /**
     * @return the dbUser
     */
    public static String getDbUser() {
        return dbUser;
    }

    /**
     * @return the dbPassword
     */
    public static String getDbPassword() {
        return dbPassword;
    }

    /**
     * @return the baseFBURL
     */
    public static String getBaseFBURL() {
        return baseFBURL;
    }

    /**
     * @return the baseLastFMURL
     */
    public static String getBaseLastFMURL() {
        return baseLastFMURL;
    }

    /**
     * @return the logFilePath
     */
    public static String getLogFilePath() {
        return logFilePath;
    }

    /**
     * @return the artistInfoFilePath
     */
    public static String getArtistInfoFilePath() {
        return artistInfoFilePath;
    }

    /**
     * @return the tagInfoFilePath
     */
    public static String getTagInfoFilePath() {
        return tagInfoFilePath;
    }

    /**
     * @return the userInfoFilePath
     */
    public static String getUserInfoFilePath() {
        return userInfoFilePath;
    }

    /**
     * @return the datSetFilePath
     */
    public static String getDatSetFilePath() {
        return datSetFilePath;
    }

}
