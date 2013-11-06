package edu.sjsu.cmpe.procurement.Jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.spinscale.dropwizard.jobs.annotations.Every;

import edu.sjsu.cmpe.procurement.domain.Book;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;

import javax.jms.*;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: shankey
 * Date: 10/29/13
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Every("5s")
public class Receive extends Job{

    ProcurementServiceConfiguration config = new ProcurementServiceConfiguration();

   @Override
   public void doJob(){

       try {

           Client client = Client.create();

           WebResource webResource = client
                   .resource("http://"+config.getApolloHost()+":"+"9000/orders/10418");




           Book response = webResource.type("application/json")
                   .get(Book.class);



             //String testop =   response.getProperties(shipped_books);
           System.out.println("Output from Server .... \n");
           // List<Book>  abc = response.getEntity(Book.class);


           //System.out.println(output);
           //JSONArray booksJson = new JSONObject(output).getJSONArray("shipped_books");

           //books = (List<Book>) booksJson.getJSONArray("shipped_books");
           //ObjectMapper mapper = new ObjectMapper();



           //books = (List<Book>)mapper.readValue(booksJson.toString(), Book.class);

           System.out.println(response.getShipped_books());
           for(Book s : response.getShipped_books()){

               String msg = s.getIsbn()+":"+s.getTitle()+":"+s.getCategory()+":"+s.getCoverimage();
               sendMessageToTopic(msg,s.getCategory());
               System.out.println("Message Sent: "+msg);

           }


       } catch (Exception e) {

           e.printStackTrace();

       }


   }

    private void sendMessageToTopic(String message, String category) throws JMSException {

        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://" + config.getApolloHost() + ":" + config.getApolloPort());

        Connection connection = factory.createConnection(config.getApolloUser(), config.getApolloPassword());
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new StompJmsDestination(config.getStompTopicName()+category);
        MessageProducer producer = session.createProducer(dest);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        String data = message;
        TextMessage msg = session.createTextMessage(data);
        msg.setLongProperty("id", System.currentTimeMillis());
        producer.send(msg);




    }


}
