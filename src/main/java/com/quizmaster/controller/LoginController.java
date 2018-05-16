package com.quizmaster.controller;

import com.quizmaster.util.DBConnection;
import com.quizmaster.util.Hashing;
import com.quizmaster.model.Login;
import com.quizmaster.util.SessionManager;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.sql.*;

@ManagedBean(name = "logincontroller")
@SessionScoped
public class LoginController implements Serializable {

    Login login = new Login();

    public LoginController() {
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    // Log in button Action method
    public String performLogIn() {
        if(loginCheck()){
            System.out.println("Log In success");
            //Facemessage for login succesfull
            /* Problem in propagating the Facemessage to the next view
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Log In Successful",null);
            context.addMessage(null,message);
            */
            //get HTTP session and store userId
            HttpSession session = SessionManager.getSession();  //getSession() is a static method
            session.setAttribute("userid",login.getUserId());
            return "userhome";
        } else{
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Invalid Login! Please Try Again!",null);
            context.addMessage(null,message);
            return "login";
        }
    }

    public String performLogout(){
        HttpSession session = SessionManager.getSession();
        session.invalidate();
        return "login";
    }

    private boolean loginCheck() {

        DBConnection dbConnection = new DBConnection();
        Connection con = dbConnection.getConnection();
        Hashing hashing = new Hashing();
        boolean loginSuccess=false;
        //String salt = "";    //empty string..used to indicate user availability and also to fetch the salt if available, at once

        try {

            PreparedStatement pst = con.prepareStatement("select user_salt from qmusers where user_id=?");
            pst.setString(1, login.getUserId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                //isAvailable=true;
                String salt = rs.getString(1);
                String checkHashedPassword=hashing.performHash(login.getUserPassword(),salt);
                PreparedStatement preparedStatement = con.prepareStatement("select user_id from qmusers where user_id=? and user_hashedpassword=?");
                preparedStatement.setString(1,login.getUserId());
                preparedStatement.setString(2,checkHashedPassword);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    loginSuccess=true;
                }
                preparedStatement.close();
            }
            pst.close();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return loginSuccess;
    }

}
