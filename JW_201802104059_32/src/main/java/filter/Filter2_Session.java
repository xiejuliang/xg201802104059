package filter;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebFilter(filterName = "Filter 2", urlPatterns = {"/*"})
public class Filter2_Session implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("Filter2 - session begins");
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;
        String path=httpServletRequest.getRequestURI();
        JSONObject message=new JSONObject();
        if (!path.contains("/login")){
            HttpSession session=((HttpServletRequest) request).getSession(false);
            if (session==null||session.getAttribute("currentUser")==null){
                message.put("message","请登录或重新登录");
                response.getWriter().println(message);
                return;
            }
        }
        chain.doFilter(request,response);
        System.out.println("Filter 2 - session ends"+"\n");
    }
}
