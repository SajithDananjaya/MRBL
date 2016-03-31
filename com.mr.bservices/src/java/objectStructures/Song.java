/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectStructures;

/**
 *
 * @author Sajith
 */
public class Song {
    
    private String mbid;
    private String artistName;
    private String trackName;
    private String imageURL;
    private String trackURL;
    
    /**
     * @return the mbid
     */
    public String getMbid() {
        return mbid;
    }

    /**
     * @param mbid the mbid to set
     */
    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    /**
     * @return the artistName
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * @param artistName the artistName to set
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /**
     * @return the trackName
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     * @param trackName the trackName to set
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    /**
     * @return the imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param imageURL the imageURL to set
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @return the trackURL
     */
    public String getTrackURL() {
        return trackURL;
    }

    /**
     * @param trackURL the trackURL to set
     */
    public void setTrackURL(String trackURL) {
        this.trackURL = trackURL;
    }

    
}
