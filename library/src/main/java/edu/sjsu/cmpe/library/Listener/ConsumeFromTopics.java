package edu.sjsu.cmpe.library.Listener;



import com.yammer.dropwizard.lifecycle.ServerLifecycleListener;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

import org.eclipse.jetty.server.Server;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import java.net.MalformedURLException;
import java.net.URL;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.api.resources.BookResource;




/**
 * Created with IntelliJ IDEA.
 * User: shankey
 * Date: 10/30/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Every("2s")
public class ConsumeFromTopics implements ServerLifecycleListener{

    private final String user;
    private final String password;
    private final String apolloPort;
    private final String host;
    private final String libraryName;
    private BookRepositoryInterface bookRepository;

    public ConsumeFromTopics(String user,
                             String password,
                             String host,
                             String apolloPort,
                             String libraryName,
                             BookRepositoryInterface bookRepository){
        this.user = user;
        this.password = password;
        this.apolloPort = apolloPort;
        this.host = host;
        this.libraryName = libraryName;
        this.bookRepository = bookRepository;


    }

    LibraryServiceConfiguration config = new LibraryServiceConfiguration();
    //BookRepository bookRepository = new BookRepository();


    @Override
    public void serverStarted(Server server) {
    StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
    factory.setBrokerURI("tcp://" + config.getApolloHost() + ":" + config.getApolloPort());

        Connection connection = null;
        try {
            connection = factory.createConnection(config.getApolloUser(), config.getApolloPassword());

        connection.start();
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Destination dest = null;
    //if(config.getLibraryName().equals("library-a")){
        dest = new StompJmsDestination(config.getStompTopicName());
    //} else{
      //  dest = new StompJmsDestination(config.getStompTopicName()+"*");
    //}

            System.out.println("Destination: "+dest);
    MessageConsumer consumer = session.createConsumer(dest);
    System.currentTimeMillis();
    System.out.println("Waiting for messages...");
    while(true) {
        Message msg = consumer.receive();
        if( msg instanceof  TextMessage ) {
            String body = ((TextMessage) msg).getText();

            System.out.println("Received message = " + body);
            saveNewBook(body);

        } else if (msg instanceof StompJmsMessage) {
            StompJmsMessage smsg = ((StompJmsMessage) msg);
            String body = smsg.getFrame().contentAsString();

            System.out.println("Received message = " + body);
            saveNewBook(body);
            break;

        } else {
            System.out.println("Unexpected message type: "+msg.getClass());
            break;
        }
    }
    connection.close();
        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
}

       void saveNewBook(String message){
           System.out.println("Message: "+message);
           Book newBook = new Book();
           String[] bookArray = message.split(":");
           newBook.setIsbn(Long.parseLong(bookArray[0]));
           newBook.setTitle(bookArray[1]);
           newBook.setCategory(bookArray[2]);
           try {

               newBook.setCoverimage(new URL(bookArray[3]+":"+bookArray[4]));

           } catch (MalformedURLException e) {
               e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
           }

           System.out.println("Book : "+ newBook);
           bookRepository.updateBook(newBook);


       }




        //To change body of implemented methods use File | Settings | File Templates.

}
