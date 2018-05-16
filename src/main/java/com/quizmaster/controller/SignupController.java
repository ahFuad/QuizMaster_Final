package com.quizmaster.controller;

import com.quizmaster.util.DBConnection;
import com.quizmaster.util.Hashing;
import com.quizmaster.model.Signup;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.sql.*;

@ManagedBean(name = "signupcontroller")
@SessionScoped
public class SignupController {
    Signup signup = new Signup();

    public SignupController() {
    }

    public Signup getSignup() {
        return signup;
    }

    public void setSignup(Signup signup) {
        this.signup = signup;
    }

    public String performSignup(){
        if(signup.getUserPassword().equals(signup.getUserPasswordRepeat())){
            if (!checkExistence()){
                if(signup.getUserEmail().contains("@")){
                    Hashing hashing = new Hashing();
                    //password encryption
                    String salt=hashing.generateRandomSalt();
                    String hashedPassword=hashing.performHash(signup.getUserPassword(),salt);
                    System.out.println("hashedPassword: "+hashedPassword);  //for checking
                    DBConnection dbconnection = new DBConnection();
                    Connection con = dbconnection.getConnection();
                    try{
                        //using prepared statement for value insertion in database
                        PreparedStatement pst = con.prepareStatement("insert into qmusers" +
                                "(user_id,user_email,user_hashedpassword,user_salt) " +
                                "values(?,?,?,?)");
                        pst.setString(1,signup.getUserId());
                        pst.setString(2,signup.getUserEmail());
                        pst.setString(3,hashedPassword);
                        pst.setString(4,salt);
                        pst.executeUpdate();
                        System.out.println("Data inserted..");  //for checking
                        System.out.println("Signup completed...Redirect to login page");
                        pst.close();
                        return "login"; //redirect to login page
                    }catch(Exception e){
                        e.printStackTrace();
                        System.out.println("sign up failed due to some exception");
                        return "signup";    //reload the signup page
                    } finally {
                        try {
                            //always gets executed
                            con.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else{
                    System.out.println("email doesn't contain pattern '% @ %'...so Not valid"); //for checking
                    FacesContext context = FacesContext.getCurrentInstance();
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Not a valid Email",null);
                    context.addMessage(null,message);
                    return "signup";
                }

            } else{
                System.out.println("user already exists");  //for checking
                FacesContext context=FacesContext.getCurrentInstance();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"User ID/Email already exists",null);
                context.addMessage(null,message);
                return "signup";
            }

        } else{
            System.out.println("password doesn't match");
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Password doesn't match",null);
            context.addMessage(null,message);
            return "signup";    //reload the signup page
        }
    }
    private boolean checkExistence(){
        DBConnection dbConnection = new DBConnection();
        Connection con = dbConnection.getConnection();
        PreparedStatement pst=null;
        boolean isExist=false;
        try {
            pst = con.prepareStatement("select user_id from qmusers where user_id=? or user_email=?");
            pst.setString(1,signup.getUserId());
            pst.setString(2,signup.getUserEmail());
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                isExist=true;
                System.out.println("Id/Email exists..so no sign up.." +
                        "reidrect to login page");  //for checking
            }
        } catch (SQLException e) {
            e.printStackTrace();
            isExist=true; //cant check the database due to some exception..so no signup allowed
        } finally {
            //safe to put inside finally
            try {
                if(pst!=null){pst.close();}
                if(con!=null){con.close();}

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isExist;
    }

}
