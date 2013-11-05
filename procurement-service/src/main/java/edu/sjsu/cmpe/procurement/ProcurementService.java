package edu.sjsu.cmpe.procurement;

import de.spinscale.dropwizard.jobs.JobsBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;

public class ProcurementService extends Service<ProcurementServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        System.out.println("Args "+ args[0]+" "+args[1]);
	new ProcurementService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ProcurementServiceConfiguration> bootstrap) {
	bootstrap.setName("procurement-service");
    bootstrap.addBundle(new JobsBundle("edu.sjsu.cmpe.procurement.Jobs"));
    }

    @Override
    public void run(ProcurementServiceConfiguration configuration,
	    Environment environment) throws Exception {
	String queueName = configuration.getStompQueueName();
	String topicName = configuration.getStompTopicName();
	log.debug("Queue name is {}. Topic is {}", queueName, topicName);

	// TODO: Apollo STOMP Broker URL and login
        String user = configuration.getApolloUser();
        String password = configuration.getApolloPassword();
        String host = configuration.getApolloHost();
        String apolloPort = configuration.getApolloPort();

       /* final Client client = new JerseyClientBuilder().using(config.getJerseyClientConfiguration())
                .using(environment)
                .build();
        environment.addResource(new ExternalServiceResource(client));
           */


    }
}
