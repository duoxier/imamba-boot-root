package com.imamba.boot.service.logging;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

public class RequestWrapper extends ContentCachingRequestWrapper {
    private HttpHeaders headers;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            Enumeration headerNames = this.getHeaderNames();

            while(headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                Enumeration headerValues = this.getHeaders(headerName);

                while(headerValues.hasMoreElements()) {
                    String headerValue = (String)headerValues.nextElement();
                    this.headers.add(headerName, headerValue);
                }
            }
        }

        return this.headers;
    }

    public URI getURI() {
        try {
            StringBuffer url = this.getRequestURL();
            String query = this.getQueryString();
            if (StringUtils.hasText(query)) {
                url.append('?').append(query);
            }

            return new URI(url.toString());
        } catch (URISyntaxException var3) {
            throw new IllegalStateException("Could not get HttpServletRequest URI: " + var3.getMessage(), var3);
        }
    }
}