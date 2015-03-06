package org.geoint.keyhole.test;

import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;


public class PostRequestClient {
    private static final Logger logger = Logger.getLogger(PostRequestClient.class.getName());
    
    public static void submitPostRequest(String url){
        Client client = ClientBuilder.newClient();
        
        try{
            String xml = null;
            
            
            
            Response response = client.target(url)
                    .request().post(Entity.xml(xml));
            
            System.out.println("response status: " + response.getStatus());
            response.close();
            client.close();
        }catch(Exception e){
                                
        }
    }

}
