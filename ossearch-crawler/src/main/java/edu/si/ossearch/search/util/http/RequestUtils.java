package edu.si.ossearch.search.util.http;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author jbirkhimer
 */
public class RequestUtils {
    private static final String[] IP_HEADER_NAMES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * in service class: String remoteAddress = RequestUtil.getRemoteIP();
     *
     * @return
     */
    public static String getRemoteIP() {

        if (RequestContextHolder.currentRequestAttributes() == null) {
            return "0.0.0.0";
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = Arrays.asList(IP_HEADER_NAMES)
                .stream()
                .map(request::getHeader)
                .filter(h -> h != null && h.length() != 0 && !"unknown".equalsIgnoreCase(h))
                .map(h -> h.split(",")[0])
                .reduce("", (h1, h2) -> h1 + ":" + h2);

        StringJoiner sj = new StringJoiner(", ");
        sj.add(request.getRemoteAddr());
        sj.add(ip);

        return StringUtils.removeEnd(sj.toString().trim(), ",") ;
    }

    public static String getRawQueryString() {
        if (RequestContextHolder.currentRequestAttributes() == null) {
            return "";
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getQueryString();
    }

    public static Map<String, String> getHeaders() {
        if (RequestContextHolder.currentRequestAttributes() == null) {
            return new HashMap<>();
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headers.put(key, request.getHeader(key));
        }
        return headers;
    }


}
