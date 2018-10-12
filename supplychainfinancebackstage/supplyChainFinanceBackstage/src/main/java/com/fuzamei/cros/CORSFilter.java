/**  
 * @Title: CORSFilter.java
 * @Package: com.fuzamei.cros
 * @Description: TODO 
 * @author: Ma Amin
 * @date: 2017-10-13 下午2:33:06
 */
package com.fuzamei.cros;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @file_name: CORSFilter.java
 * @Description: TODO
 * @author: Ma Amin
 * @date: 2017-10-13 下午2:33:06
 * @version 1.0.0
 */
public class CORSFilter implements Filter {
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, OPTIONS, POST, PUT, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers","Authorization, Content-Type, X-Requested-With");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");//能让前端在response中获取header中的参数，具体能获取哪个参数，取决于后面的value值
		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}