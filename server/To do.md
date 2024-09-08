[] Apply spring security using Firebase
[] Use a filter, in this case CommonsRequestLoggingFilter
```

    public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
	    @Override
	    protected boolean shouldLog(HttpServletRequest request) {
	        return logger.isDebugEnabled() &&
	               !request.getRequestURL().toString().contains("/static")
	    }
	}
```  
```
@Configuration
public class RequestLoggingFilterConfig {
 
    @Bean
    public CustomRequestLoggingFilter logFilter() {
        CustomRequestLoggingFilter filter
          = new CustomRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(false);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
``` 
In application.properties
``` 
logging.level.<package of your filter>.CustomRequestLoggingFilter=DEBUG or whatever debug level
```