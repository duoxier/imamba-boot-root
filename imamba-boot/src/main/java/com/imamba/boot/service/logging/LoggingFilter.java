package com.imamba.boot.service.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

public class LoggingFilter extends OncePerRequestFilter {
    static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    static final String REQUEST_PREFIX = "> ";
    static final String RESPONSE_PREFIX = "< ";
    static final String NOTIFICATION_PREFIX = "* ";
    private int DEFAULT_MAX_PAYLOAD_LENGTH = 8192;
    final AtomicLong _id = new AtomicLong(0L);
    private static final Comparator<Entry<String, List<String>>> COMPARATOR = (o1, o2) -> {
        return ((String)o1.getKey()).compareToIgnoreCase((String)o2.getKey());
    };
    private static final Set<MediaType> READABLE_APP_MEDIA_TYPES = new HashSet<MediaType>() {
        {
            this.add(new MediaType("text", "*"));
            this.add(new MediaType("application", "svg+xml"));
            this.add(MediaType.APPLICATION_ATOM_XML);
            this.add(MediaType.APPLICATION_FORM_URLENCODED);
            this.add(MediaType.APPLICATION_JSON);
            this.add(MediaType.APPLICATION_XML);
            this.add(MediaType.APPLICATION_XHTML_XML);
        }
    };

    public LoggingFilter() {
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!logger.isDebugEnabled()) {
            filterChain.doFilter(request, response);
        } else {
            long id = this._id.incrementAndGet();
            RequestWrapper requestToUse = new RequestWrapper(request);
            ResponseWrapper responseToUse = new ResponseWrapper(response);

            try {
                filterChain.doFilter(requestToUse, responseToUse);
            } finally {
                this.logRequest(id, requestToUse);
                this.logResponse(id, responseToUse);
                responseToUse.copyBodyToResponse();
            }

        }
    }

    private void logRequest(long id, RequestWrapper request) {
        StringBuilder b = new StringBuilder();
        this.printRequestLine(b, "Sending client request", id, request.getMethod(), request.getURI());
        this.printPrefixedHeaders(b, id, "> ", (new ServletServerHttpRequest(request)).getHeaders());
        if (this.isReadable(request.getContentType())) {
            this.printEntity(b, request.getContentAsByteArray(), request.getCharacterEncoding());
        }

        this.log(b);
    }

    private void logResponse(long id, ResponseWrapper response) {
        StringBuilder b = new StringBuilder();
        this.printResponseLine(b, "Server responded with a response", id, response.getStatus());
        this.printPrefixedHeaders(b, id, "< ", response.getHeaders());
        if (this.isReadable(response.getContentType())) {
            this.printEntity(b, response.getContentAsByteArray(), response.getCharacterEncoding());
        }

        this.log(b);
    }

    void log(StringBuilder b) {
        if (logger.isDebugEnabled()) {
            logger.info(b.toString());
        }

    }

    private void printEntity(StringBuilder b, byte[] buf, String charset) {
        if (buf.length > 0) {
            int length = Math.min(buf.length, this.DEFAULT_MAX_PAYLOAD_LENGTH);

            String payload;
            try {
                payload = new String(buf, 0, length, charset);
            } catch (UnsupportedEncodingException var7) {
                payload = "[unknown]";
            }

            b.append(payload);
            if (buf.length > this.DEFAULT_MAX_PAYLOAD_LENGTH) {
                b.append("...more...");
            }

            b.append('\n');
        }

    }

    private StringBuilder prefixId(StringBuilder b, long id) {
        b.append(Long.toString(id)).append(" ");
        return b;
    }

    void printRequestLine(StringBuilder b, String note, long id, String method, URI uri) {
        this.prefixId(b, id).append("* ").append(note).append(" on thread ").append(Thread.currentThread().getName()).append("\n");
        this.prefixId(b, id).append("> ").append(method).append(" ").append(uri.toASCIIString()).append("\n");
    }

    void printResponseLine(StringBuilder b, String note, long id, int status) {
        this.prefixId(b, id).append("* ").append(note).append(" on thread ").append(Thread.currentThread().getName()).append("\n");
        this.prefixId(b, id).append("< ").append(Integer.toString(status)).append("\n");
    }

    void printPrefixedHeaders(StringBuilder b, long id, String prefix, HttpHeaders headers) {
        Iterator var6 = this.getSortedHeaders(headers.entrySet()).iterator();

        while(true) {
            while(var6.hasNext()) {
                Entry<String, List<String>> headerEntry = (Entry)var6.next();
                List<?> val = (List)headerEntry.getValue();
                String header = (String)headerEntry.getKey();
                if (val.size() == 1) {
                    this.prefixId(b, id).append(prefix).append(header).append(": ").append(val.get(0)).append("\n");
                } else {
                    StringBuilder sb = new StringBuilder();
                    boolean add = false;
                    Iterator var12 = val.iterator();

                    while(var12.hasNext()) {
                        Object s = var12.next();
                        if (add) {
                            sb.append(',');
                        }

                        add = true;
                        sb.append(s);
                    }

                    this.prefixId(b, id).append(prefix).append(header).append(": ").append(sb.toString()).append("\n");
                }
            }

            return;
        }
    }

    Set<Entry<String, List<String>>> getSortedHeaders(Set<Entry<String, List<String>>> headers) {
        TreeSet<Entry<String, List<String>>> sortedHeaders = new TreeSet(COMPARATOR);
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }

    boolean isReadable(String contentType) {
        if (StringUtils.isBlank(contentType)) {
            return false;
        } else {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            if (mediaType == null) {
                return false;
            } else {
                Iterator var3 = READABLE_APP_MEDIA_TYPES.iterator();

                MediaType readableMediaType;
                do {
                    if (!var3.hasNext()) {
                        return false;
                    }

                    readableMediaType = (MediaType)var3.next();
                } while(!readableMediaType.isCompatibleWith(mediaType));

                return true;
            }
        }
    }
}