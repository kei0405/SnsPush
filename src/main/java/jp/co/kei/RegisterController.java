package jp.co.kei;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;

/**
 * Register the token requested from the device to publish later on.
 * 
 * @author keichanzahorumon
 *
 */
@Controller
public class RegisterController {
	
	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
	@Autowired
	private String topicArn;
	@Autowired
	private String appArn;
	
	/**
	 * Register the token to the SNS.
	 *  
	 * @param locale
	 * @param model
	 * @param token
	 */
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public void register(Locale locale, Model model, @RequestParam("token") String token) {
		
		if (token == null || token.length() != 64) {
			logger.warn("token must be 64bit. token=" + token);
		}

		AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());		                           
		snsClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
		
		// Create endpoint from the requested token
		CreatePlatformEndpointRequest cpeReq = new CreatePlatformEndpointRequest();
		cpeReq.setPlatformApplicationArn(appArn);
		cpeReq.setToken(token);

		CreatePlatformEndpointResult result = snsClient.createPlatformEndpoint(cpeReq);
		logger.info("Succeeded at creating Endpointarn.EndpointArn=" + result.getEndpointArn());

		// Subscribe to Topic
		SubscribeRequest subscribeRequest = new SubscribeRequest();
		subscribeRequest.setTopicArn(topicArn);
		subscribeRequest.setProtocol("application");
		subscribeRequest.setEndpoint(result.getEndpointArn());

		SubscribeResult subscribeResult = snsClient.subscribe(subscribeRequest);
		logger.info("Succeeded at subscribing to a Topic.SubscriptionArn=" + subscribeResult.getSubscriptionArn());
	}
}