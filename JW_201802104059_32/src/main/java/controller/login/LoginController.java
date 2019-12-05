package controller.login;

import com.alibaba.fastjson.JSONObject;
import domain.User;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login.ctl")
public class LoginController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username= request.getParameter("username");
        String password= request.getParameter("password");
        JSONObject message=new JSONObject();
        try {
            User loggedUser= UserService.getInstance().login(username,password);
            if (loggedUser!=null){
                message.put("message","登录成功");
                HttpSession session=request.getSession();
                session.setMaxInactiveInterval(10*60);
                session.setAttribute("currentUser",loggedUser);
                response.getWriter().println(message);
            }else{
                message.put("message","用户名或密码错误");
                response.getWriter().println(message);
            }
        }catch (SQLException e){
            message.put("message","数据库操作异常");
            response.getWriter().println(message);
        }catch (Exception e){
            message.put("message","网络异常");
            response.getWriter().println(message);
            e.printStackTrace();
        }
    }

}
