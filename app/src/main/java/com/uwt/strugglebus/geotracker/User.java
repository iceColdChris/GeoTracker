package com.uwt.strugglebus.geotracker;

/**
 * Created by Chris on 4/10/2015.
 */
public class User {
    private final int uid;
    private String email;
    private String password;
    //security question and answer
    private String question;
    private String answer;

    /**
     * Initialize a new user
     * @param uid unique user id
     * @param email user's email. also used fro username
     * @param password the user's password
     * @param question security question
     * @param answer answer to the security question
     */
    public User(final int uid, final String email, final String password, final String question, final String answer) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.question = question;
        this.answer = answer;
    }

    //--------------------------GETTERS AND SETTERS--------------------------------
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param secAnswer
     */
    public void forgotPassword(final String secAnswer) {
        if(secAnswer.equals(answer)) {

        }
    }

}