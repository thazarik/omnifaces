/*
 * Copyright 2012 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.filter;

import static org.omnifaces.util.Exceptions.unwrap;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.webapp.FacesServlet;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * The {@link FacesExceptionFilter} will solve 2 problems with exceptions thrown in JSF methods.
 * <ol>
 * <li>Mojarra's <code>FacesFileNotFoundException</code> needs to be interpreted as 404.
 * <li>Root cause needs to be unwrapped from {@link FacesException} and {@link ELException} to utilize standard
 * Servlet API error page handling.
 * </ol>
 *
 * <h3>Installation</h3>
 * <p>
 * To get it to run, map this filter on the <code>&lt;servlet-name&gt;</code> of the {@link FacesServlet} in the same
 * <code>web.xml</code>.
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;facesExceptionFilter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;org.omnifaces.filter.FacesExceptionFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;facesExceptionFilter&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;facesServlet&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 *
 * @author Bauke Scholtz
 */
public class FacesExceptionFilter extends HttpFilter {

	@Override
	public void doFilter
		(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain)
			throws ServletException, IOException
	{
		try {
			chain.doFilter(request, response);
		}
		catch (FileNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
		}
		catch (ServletException e) {
			throw new ServletException(unwrap(e.getRootCause()));
		}
	}

}