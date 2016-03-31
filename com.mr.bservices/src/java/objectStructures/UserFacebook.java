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
public class UserFacebook extends User {

    private String name;
    private String accessToken;
    private boolean relearnAvailable; 

    public UserFacebook(String userFacebookID, String name, String accessToken) {
        super.setUserName(userFacebookID);
        this.name = name;
        this.accessToken = accessToken;
        relearnAvailable = false;
    }

    public String getName() {
        return this.name;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
    
    public void setRelearnAvailable(boolean relearnAvailable){
        this.relearnAvailable=relearnAvailable;
    }
    
    public boolean getRelearnAvailable(){
        return relearnAvailable;
    }

}
