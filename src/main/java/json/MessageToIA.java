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
public class MessageToIA {
    
    MessageToIAContent message;
    
    public MessageToIA(){
        
    }
    
    public MessageToIA(String URL, String incidentType, String attachmentTimeStampUTC){
        this.message = new MessageToIAContent(URL, incidentType, attachmentTimeStampUTC);
    }
    
}
