package soa.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class SearchController {

	@Autowired
	  private ProducerTemplate producerTemplate;

	@RequestMapping("/")
    public String index() {
        return "index";
    }


    @RequestMapping(value="/search")
    @ResponseBody
    public Object search(@RequestParam("q") String q) {
        // Attempt to split the query string with keywords and count
        String[] splitted = q.split("max:");
        // Creation of query headers
        Map<String,Object> headers = new HashMap<String,Object>();
        if(splitted.length>1) {
            // If max count is specified, then the max count is requested to the Twitter API
            String message = splitted[0].trim();
            try {
                // Converts the count from String to Integer
                Integer count = new Integer(splitted[1].trim());
                headers.put("CamelTwitterKeywords",message);
                headers.put("CamelTwitterCount",count);
            } catch(NumberFormatException ex){
                // If max count is not a number, only keywords go in the request
                System.out.println("Warning: not a number specified for twitter counts");
                headers.put("CamelTwitterKeywords",message);
            }
        } else {
            // If there is no max count specified, only keywords go in the request
            headers.put("CamelTwitterKeywords",q);
        }
        return producerTemplate.requestBodyAndHeaders("direct:search", "", headers);
    }
}