/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;
import dataHandlers.LastFMDataHandler;
import dataHandlers.DataClusterHandler;
import dataHandlers.FacebookDataHandler;
import java.util.List;
import java.util.HashMap;
import objectStructures.User;
import objectStructures.UserFacebook;
import objectStructures.Tag;
import com.restfb.*;
/**
 *
 * @author Sajith
 */
public class DummyMain {
    
    public static void main(String[] args){
        GlobalParam.initParameters();
        LastFMDataHandler.loadPreviousData();
        LastFMDataHandler.initiateUsers();
        LastFMDataHandler.createDataSheet();
        DataClusterHandler temp = new DataClusterHandler();
        temp.buildGraph(GlobalParam.getDataSetFilePath());
        
        String userFacebookID="1255393874475776";
		String name = "Sajith Dananjaya";
		String accessToken ="CAAHiyQnHquQBAArZAn0VNSt3WiPTnqMGfKWhmNsbwSJiJ7V18OCFrncAP8M8C9z9Kq0nve3TRbGkF2Fxr8MR5AtnPlWMfwPzRmEK6DW7Qh0reXEHfDOZBGFhm1oV51zEUCZBlnmO753O3Q1brSBoWMcoBBDZAZAh2ZCeEfGFCvqVbAumkXqlgzWmsDqmJOUxgZD";
	
		User facebookUser = FacebookDataHandler
				.setUserTaste(new UserFacebook(userFacebookID, name, accessToken));
		facebookUser.setUserID(LastFMDataHandler.getNewUserID());
		facebookUser.filterTaste();
		
                LastFMDataHandler.createDataSheet();
		temp.buildGraph(GlobalParam.getDataSetFilePath());
		String[] userIDList = temp.getRelatedUsers(facebookUser);
		
		for(String s: userIDList){
			System.out.println(LastFMDataHandler.getUserName(s));
		}
    }
    
}
