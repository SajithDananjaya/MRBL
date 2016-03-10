/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectStructures;
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author Sajith
 */
public class Artist {

    private String artistName;
    private List<Tag> artistTagList;

    private Artist() {
    }

    public Artist(String artistName) {
        this.artistName = artistName;
        this.artistTagList = new ArrayList<>();
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void addArtistTag(Tag tag) {
        this.artistTagList.add(tag);
    }

    public void setArtistTags(List<Tag> artistTagList) {
        this.artistTagList = artistTagList;
    }

    public List<Tag> getArtistTags() {
        return this.artistTagList;
    }

    public String getTagListString() {
        String stringTagList = "";
        if (artistTagList.size() > 0) {
            for (Tag t : artistTagList) {
                stringTagList = stringTagList + "," + t.getTagName();
            }
        }
        return stringTagList;
    }

}
