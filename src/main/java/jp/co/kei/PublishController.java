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
import com.amazonaws.services.sns.model.PublishRequest;

/**
 * Publish the requested message 
 * 
 * @author keichanzahorumon
 *
 */
@Controller
public class PublishController {
	
	private static final Logger logger = LoggerFactory.getLogger(PublishController.class);
	@Autowired
	private String topicArn;
	
	/**
	 * Publish the requested message
	 * 
	 * @param locale
	 * @param model
	 * @param message
	 */
	@RequestMapping(value = "/publish", method = RequestMethod.GET)
	public void publish(Locale locale, Model model, @RequestParam("message") String message) {

		AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());		                           
		snsClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
		
		// Publish a message
		PublishRequest publishRequest = new PublishRequest(topicArn, message);
		snsClient.publish(publishRequest);
		
		logger.info("Published a message.Message=" + message);
	}
}