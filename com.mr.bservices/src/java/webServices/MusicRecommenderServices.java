/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServices;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import objectStructures.UserFacebook;
/**
 *
 * @author Sajith
 */
@WebService(serviceName = "MusicRecommenderServices")
public class MusicRecommenderServices {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    
    @WebMethod(operationName = "validateLogin")
    public UserFacebook validateLogin(@WebParam(name = "userName") String userNames,
            @WebParam(name = "userPassword") String userPassword){
            
            return null;
    }
    
    
    
}
