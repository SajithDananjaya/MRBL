/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectStructures;

import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Sajith
 */
public abstract class User {

    private int userID;
    private String userName;
    private HashMap<Tag, Integer> musicTaste= new HashMap<>();;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return this.userID;
    }

    public void setMusicTaste(Tag tag) {
        if (this.musicTaste.containsKey(tag)) {
            int tagCount = musicTaste.get(tag);
            musicTaste.replace(tag, tagCount + 1);
        } else {
            musicTaste.put(tag, 1);
        }
    }

    public int getTagCount(String tagName) {
        return musicTaste.get(tagName);
    }

    public HashMap<Tag, Integer> getMusicTaste() {
        return this.musicTaste;
    }

    public void filterTaste() {
        HashMap<Tag, Integer> tempTasteMap = new HashMap<>();
        for (Tag t : this.musicTaste.keySet()) {
            int tagCount = this.musicTaste.get(t);
            if (tagCount > 2) {
                tempTasteMap.put(t, tagCount);
            }
        }
        this.musicTaste = tempTasteMap;
    }

    public String getTasteString(int totalTagCount) {
        String[] tasteArray = new String[totalTagCount];
        Arrays.fill(tasteArray, "0");
        for (Tag t : musicTaste.keySet()) {
            tasteArray[t.getTagID() - 1] = musicTaste.get(t) + "";
        }
        return arrayToString(tasteArray);
    }

    private String arrayToString(String[] tasteArray) {
        String array = "";
        for (String s : tasteArray) {
            array = array + "," + s;
        }
        return array;
    }
    
    public void printMusicTaste(){
        for(Tag t: musicTaste.keySet()){
            System.out.println(t.getTagName()+" : "+musicTaste.get(t));
        }
    }

}
