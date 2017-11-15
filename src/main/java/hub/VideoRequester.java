/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hub;

/**
 *
 * @author andreadisst
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import json.Attachment;
import json.Header;
import json.IncidentReport;
import json.MessageFromVA;
import json.MessageToVA;
import json.VideoAnalyzed;
import json.VideoAnalyzedBody;
import mykafka.Bus;

public class VideoRequester extends Thread{
    Socket soc;
    DataInputStream din;
    DataOutputStream dout;
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    String over = "Msg from VA received";
    IncidentReport incidentReport;
    Attachment attachment;
    Bus bus = new Bus();
    
    public VideoRequester(IncidentReport incidentReport, Attachment attachment){
        this.incidentReport = incidentReport;
        this.attachment = attachment;
    }
    
    @Override
    public void run()
    {
        MessageToVA newMessageToVA = new MessageToVA(attachment.getAttachmentTimeStampUTC(), attachment.getAttachmentURL());
        String request = gson.toJson(newMessageToVA);
        
        try{      
            
            soc = new Socket(Configuration.video_IP, Configuration.video_port);  
            
            din = new DataInputStream(soc.getInputStream());
            dout = new DataOutputStream(soc.getOutputStream());
            
            sendMessage(request);

            readMessage();

            String response = readMessage();
            MessageFromVA messageFromVA = gson.fromJson(response, MessageFromVA.class);
            
            sendMessage(over);
            
            VideoAnalyzedBody videoAnalyzedBody = new VideoAnalyzedBody(attachment.getAttachmentTimeStampUTC() , incidentReport.getBody().getPosition(), attachment.getAttachmentURL(), messageFromVA.getVidAnalyzed(), messageFromVA.getVidAnalysis());
            Header header = incidentReport.getHeader();
            header.setTopicName(Configuration.video_analyzed);
            VideoAnalyzed videoAnalyzed = new VideoAnalyzed(header, videoAnalyzedBody);
            String message = gson.toJson(videoAnalyzed);
            bus.post(Configuration.video_analyzed, message);
            
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(VideoRequester.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try{
                din.close();
                dout.close();
                soc.close();
            }catch(IOException e){
                System.out.println("Error: " + e);
            }
        }
    }
    
    private void sendMessage(String msg)
    {
        try{
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            dout.write(bytes);
            dout.flush();
            System.out.println("client> " + msg);
        }
        catch(IOException e){
            System.out.println("Error: " + e);
        }
    }
    
    private String readMessage()
    {
        String response = "";
        try {
            byte b[] = new byte[1024];
            din.read(b);
            response = new String(b, StandardCharsets.UTF_8);
            response = response.replaceAll("\u0000.*", "");
            System.out.println("server> " + response); 
        } catch (IOException ex) {
            Logger.getLogger(VideoRequester.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
}