package cc.base;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ULoggingFilter implements Filter {

  private FilterConfig filterConfig = null;

  public void destroy() {
    this.filterConfig = null;
  }
  
  public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain)
    throws IOException, ServletException {
    
    // Log user's IP address.
    ServletContext servletContext = filterConfig.getServletContext();
    servletContext.log(request.getRemoteHost());
    chain.doFilter(request, response);
  }

  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }
}
