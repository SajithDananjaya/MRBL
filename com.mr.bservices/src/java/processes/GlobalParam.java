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
public class GlobalParam {

    private static final String CONFIG_FILE_PATH = "./configs/config";
    private static boolean paramInitiated = false;

    //Key is the param name, Object is param value 
    private static HashMap<String, String> paramMap;

    private GlobalParam() {
    }

    public static void initParameters() {
        paramMap = new HashMap<>();
        BufferedReader file = getConfigFile(CONFIG_FILE_PATH);
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
    public static void initParameters(String filePath) {
        paramMap = new HashMap<>();
        BufferedReader file = getConfigFile(filePath);
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

    private static BufferedReader getConfigFile(String filePath) {
        File tempFile = new File(filePath);
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
        return Integer.parseInt(paramMap.get("artistCountPerUser"));
    }

    /**
     * @return the tagCountPerArtist
     */
    public static int getTagCountPerArtist() {
        return Integer.parseInt(paramMap.get("tagCountPerArtist"));
    }

    /**
     * @return the postCountPerUser
     */
    public static int getPostCountPerUser() {
        return Integer.parseInt(paramMap.get("postCountPerUser"));
    }

    /**
     * @return the dbName
     */
    public static String getDbName() {
        return paramMap.get("dbName");
    }

    /**
     * @return the dbUser
     */
    public static String getDbUser() {
        return paramMap.get("dbUser");
    }

    /**
     * @return the dbPassword
     */
    public static String getDbPassword() {
        return paramMap.get("dbPassword");
    }

    /**
     * @return the baseFBURL
     */
    public static String getBaseFBURL() {
        return paramMap.get("baseFBURL");
    }

    /**
     * @return the baseLastFMURL
     */
    public static String getBaseLastFMURL() {
        return paramMap.get("baseLastFMURL");
    }
    
    /**
     * @return the artistInfoFilePath
     */
    public static String getArtistInfoFilePath() {
        return paramMap.get("artistInfoFilePath");
    }

    /**
     * @return the tagInfoFilePath
     */
    public static String getTagInfoFilePath() {
        return paramMap.get("tagInfoFilePath");
    }

    /**
     * @return the userInfoFilePath
     */
    public static String getUserInfoFilePath() {
        return paramMap.get("userInfoFilePath");
    }

    /**
     * @return the dataSetFilePath
     */
    public static String getDataSetFilePath() {
        return paramMap.get("dataSetFilePath");
    }
    
    /**
     * @return the logFilePath
     */
    public static String getLogFilePath() {
        return paramMap.get("logFilePath");
    }
    
    /**
     * @return the lastFMAPIKey
     */
    public static String getLastFMAPIKey() {
        return paramMap.get("lastFMAPIKey");
    }
    
    /**
     * @return the initialUserCount
     */
    public static int getInitialUserCount() {
        return Integer.parseInt(paramMap.get("initialUserCount"));
    }
    
    /**
     * @return the learningStartBound
     */
    public static int getLearningStartBound() {
        return Integer.parseInt(paramMap.get("learningStartBound"));
    }
    
    /**
     * @return the tagLastFMURL
     */
    public static int getTagLastFMURL() {
        return Integer.parseInt(paramMap.get("tagLastFMURL"));
    }
    
    /**
     * @return the userCountPerNewTag
     */
    public static int getUserCountPerNewTag() {
        return Integer.parseInt(paramMap.get("userCountPerNewTag"));
    }
    
    

}
