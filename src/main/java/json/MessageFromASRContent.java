/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

/**
 *
 * @author andreadisst
 */
public class MessageFromASRContent {
    
    String IDRef;
    String language;
    
    public MessageFromASRContent(){
        
    }
    
    public MessageFromASRContent(String IDRef, String language){
        this.IDRef = IDRef;
        this.language = language;
    }
    
    public String getIDRef(){
        return IDRef;
    }
    
    public String getLanguage(){
        return language;
    }
    
}