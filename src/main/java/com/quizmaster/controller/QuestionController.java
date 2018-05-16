package com.quizmaster.controller;

import com.quizmaster.model.Question;
import com.quizmaster.model.Topic;
import com.quizmaster.util.DBConnection;
import com.quizmaster.util.SessionManager;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SessionScoped
@ManagedBean (name = "questioncontroller")
public class QuestionController {

    Topic topic = new Topic();  //model
    Question question = new Question(); //model
    HashMap<Integer,Question> questionSet = new HashMap<>();    //to store the questions and submitted answers
    Integer answerCorrect;
    Integer obtainedMarks;

    public QuestionController() {
    }

    //getter and setters
    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(Integer answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    public Integer getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(Integer obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public HashMap<Integer, Question> getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(HashMap<Integer, Question> questionSet) {
        this.questionSet = questionSet;
    }

    //actionListener method to retrieve the selected Topic
    public void selectedTopic(ActionEvent event){

        //all this to retrieve the button id which will be used to run sql
        //the usual process event.getComponent().getClientId() returns the whole structural ID
        //in this case that is in the form of form-id:component-id
        //that is no use to us
        Map<String, Object> btnAttributes = event.getComponent().getAttributes();
        topic.setTopicSelection((String) btnAttributes.get("id"));
        System.out.println("Topic fetched from view: "+topic.getTopicSelection());

    }

    //to fetch the questions from database and put it onto the HashMap
    public String setQuestions(){

        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        Integer hashmapKey = 1; //for setting hashmap key
        boolean loadSuccess = false;

        try {

            PreparedStatement pst = connection.prepareStatement("select * from (select question_id,question_desc,option_1,option_2,option_3,option_4,answer " +
                    "from questions where topic=? order by dbms_random.value) where rownum<=5");
            pst.setString(1,topic.getTopicSelection());
            ResultSet rs = pst.executeQuery();
            System.out.println(rs); //for checking
            while(rs.next()){
                loadSuccess=true;
                Question ques = new Question();
                ques.setQuestion_id(rs.getInt(1));
                ques.setQuestion_desc(rs.getString(2));
                ques.setOption_1(rs.getString(3));
                ques.setOption_2(rs.getString(4));
                ques.setOption_3(rs.getString(5));
                ques.setOption_4(rs.getString(6));
                ques.setAnswer(rs.getString(7));

                questionSet.put(hashmapKey++,ques); ///to put the fetched questions into Hashmap

                //System.out.println(hashmapKey+ques.getQuestion_desc()); //for checking

            }
            pst.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close(); //closing the connection
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if for some reason the questions failed to load from the database
        if(!loadSuccess){
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL,"No Data Found on Database",null);
            context.addMessage(null,message);
            return "topic";
        }

        //for checking
        //System.out.println(questionSet.size());
        //System.out.println(questionSet.get(2).getQuestion_desc());

        return "quiz";  //for navigation using faces-config.xml
    }

    public String evaluate(){
        //for checking
        System.out.println("submitted 1: "+questionSet.get(1).getSubmittedAnswer());
        System.out.println("submitted 2: "+questionSet.get(2).getSubmittedAnswer());

        //for checking
        System.out.println("correct 1: "+questionSet.get(1).getAnswer());
        System.out.println("correct 2: "+questionSet.get(2).getAnswer());

        //initialize correct answer to zero
        answerCorrect=0;

        // loop to evaluate all the answers
        for(int i=1;i<=5;i++){
            if(questionSet.get(i).getSubmittedAnswer().equals(questionSet.get(i).getAnswer())){
                answerCorrect+=1;
            }
        }
        obtainedMarks=answerCorrect*10;
        System.out.println("Total Correct answer: "+answerCorrect);
        System.out.println("Marks: "+obtainedMarks+" out of 50");

        return "result";
    }

}
