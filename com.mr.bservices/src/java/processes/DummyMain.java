/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

/**
 *
 * @author Sajith
 */
public class DummyMain {
    
    public static void main(String[] args){
        
        globalParam.initParameters();
        System.out.println(globalParam.getLastFMUserName());
    }
    
}
