package com.quizmaster.util;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter implements Filter {

    public AuthFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    //method that performs filtering.....by default
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {

            // check whether session variable is set
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            HttpSession session = req.getSession(false);
            String uri = req.getRequestURI();
            System.out.println("Requested: "+uri);

            boolean loggedIn = (session!=null) && (session.getAttribute("userid")!=null);   // true if user is logged in
            boolean resourceRequest = uri.contains("javax.faces.resource"); //true if resources requested
            boolean initialRequest = !(uri.endsWith(".xhtml") || uri.endsWith(".jsf"));    //true if uri doesn't end with .xhtml

            //boolean inQuiz= (session!=null) && (session.getAttribute("quizid")!=null);

            if(loggedIn || resourceRequest || initialRequest){
                //executes if user is logged in,resource requested or uri request with no .xhtml(ex:server run initial request:"/")
                if(!resourceRequest || !initialRequest){
                    //only if user is logged in
                    if(uri.contains("login") || uri.contains("signup")){    //redirect to homeuser for these 2 pages
                        res.sendRedirect(req.getContextPath()+"/view/userhome.xhtml");
                    }
                }
                chain.doFilter(request,response);
            } else{
                //no user is logged in
                if(uri.contains("login") || uri.contains("signup") || uri.contains("index")){   //passes for this 3 pages
                    chain.doFilter(request,response);
                }else{
                    res.sendRedirect(req.getContextPath()+"/view/login.xhtml"); //redirect to login for other pages
                }
            }

        }
        catch(Throwable t) {
            System.out.println( t.getMessage());
        }
    } //doFilter

    @Override
    public void destroy() {

    }
}