package edu.sjsu.cmpe.procurement.Jobs;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: shankey
 * Date: 10/28/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Every("5min")
public class Order extends Job{

    ProcurementServiceConfiguration config = new ProcurementServiceConfiguration();

    //String queueName = config.getStompQueueName();


    @Override
    public void doJob() {

        List<String> messages = new ArrayList<String>();

        try {
            messages = takeOrderFromQueue();
        } catch (JMSException e) {

            System.out.println("Problem with reading message :(");
        }

        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource("http://"+config.getApolloHost()+":"+"9000/orders");
            List<Integer> books = new ArrayList<Integer>();
            for(String msg:messages){
                Integer isbn = Integer.parseInt(msg.substring(msg.indexOf(':') + 1));
                System.out.println("ISBN : "+isbn);
             books.add(isbn);
            }

            String input = "{\"id\":\"10418\",\"order_book_isbns\":"+books+"}";
            System.out.println("INPUT: "+input);
            if(!books.isEmpty()){
            ClientResponse response = webResource.type("application/json")
                    .post(ClientResponse.class, input);

            /*if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            } */

            System.out.println("Output from Server .... \n");
            String output = response.getEntity(String.class);
            System.out.println(output);
            }
        } catch (Exception e) {

            e.printStackTrace();

        }



    }




    public List<String> takeOrderFromQueue() throws JMSException {

        List<String> messagesFromQueue = new ArrayList<String>();
        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://" + config.getApolloHost() + ":" + config.getApolloPort());

        Connection connection = factory.createConnection(config.getApolloUser(), config.getApolloPassword());
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new StompJmsDestination(config.getStompQueueName());

        MessageConsumer consumer = session.createConsumer(dest);
        System.out.println("Waiting for messages from " + config.getStompQueueName() + "...");
        long waitUntil=5000;
        while(true) {
            Message msg = consumer.receive(waitUntil);
            if( msg instanceof  TextMessage ) {
                String body = ((TextMessage) msg).getText();

                if( "SHUTDOWN".equals(body)) {
                    break;
                }
                System.out.println("Received message = " + body);
                messagesFromQueue.add(body);
            } else if (msg instanceof StompJmsMessage) {
                StompJmsMessage smsg = ((StompJmsMessage) msg);
                String body = smsg.getFrame().contentAsString();
                System.out.println("Received message = " + body);
                 messagesFromQueue.add(body);
            } else if(msg==null) {
                System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
                break;
            }
            else {
                System.out.println("Unexpected message type: "+msg.getClass());
            }
        }
        connection.close();
    return messagesFromQueue;}


}



