package org.rabbit.configuration;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nine rabbit
 */
@Component
public class CrossFilter implements Filter {

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpServletResponse.setHeader("Access-Control-Allow-Headers", "accept,content-type");
    httpServletResponse.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,DELETE,PUT");
    httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    httpServletResponse.setHeader("Pragma", "no-cache"); 
    httpServletResponse.setDateHeader("Expires", 0); 
    chain.doFilter(request, httpServletResponse);

  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
  }
}
