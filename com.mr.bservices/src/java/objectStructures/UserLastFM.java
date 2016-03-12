/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectStructures;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sajith
 */
public class UserLastFM extends User{

    List<Song> favoriteSongs;

    private UserLastFM() {
    }

    public UserLastFM(String username) {
        super.setUserName(username);
        favoriteSongs = new ArrayList<>();
    }

    public void addSong(Song song) {
        this.favoriteSongs.add(song);
    }

    public List<Song> getSongsList() {
        return favoriteSongs;
    }

}
