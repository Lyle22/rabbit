package org.rabbit.configuration;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CrsfFilter implements Filter {

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
