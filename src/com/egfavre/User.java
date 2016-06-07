package com.egfavre;

import java.util.ArrayList;

/**
 * Created by user on 6/7/16.
 */
public class User {
    String name;
    String pass;
    static ArrayList<Message> messages = new ArrayList<>();

    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
        this.messages = messages;
    }
}
