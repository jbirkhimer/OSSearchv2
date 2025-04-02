package edu.si.ossearch.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.Include;
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author jbirkhimer
 */
@Component
public class TraceRequestFilter extends HttpExchangesFilter {
    /**
     * Create a new {@link HttpExchangesFilter} instance.
     *
     * @param repository the trace repository
     */
    public TraceRequestFilter(HttpExchangeRepository repository) {
        super(repository, Include.defaultIncludes());
    }

    /**
     * Filter all request not containing api in path.
     * @param request current HTTP request
     * @return whether the given request should <i>not</i> be filtered
     * @throws ServletException in case of errors
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return request.getServletPath().matches("api|search"); //.contains("api");
        return request.getServletPath().contains("api") || request.getServletPath().contains("search");
    }
}
