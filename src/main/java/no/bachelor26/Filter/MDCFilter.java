package no.bachelor26.Filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* 
 * Et filter som legger til en traceID, 
 * HTTP-metoden og URI-en til en 
 * innkommende forespørsel 
 */

@Component
public class MDCFilter extends OncePerRequestFilter {

    public void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filter) throws ServletException, IOException{

            try{
                // Legger til en traceID for å se hvilke loggoppføringer som er knyttet sammen
                MDC.put("traceID", UUID.randomUUID().toString().substring(0, 8));

                // HTTP-metoden
                MDC.put("method", req.getMethod());

                // Endepunktet
                MDC.put("endpoint", req.getRequestURI());

                doFilter(req, resp, filter);

            } finally{
                MDC.clear();
            }

    }

}
