package com.egfavre;

import com.sun.org.apache.xpath.internal.operations.Mod;
import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    static User user;
    static HashMap<String, User> users = new HashMap<>();

    static final String PASSWORD = "password";

    public static void main(String[] args) {
        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap();
                    if (user==null){
                        return new ModelAndView(m, "index.html");
                    }
                    else {
                        m.put("messages", User.messages);
                        return new ModelAndView(m, "messages.html");
                    }
                },
        new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                (request, response) ->{
                    String name = request.queryParams("name");
                    String password = request.queryParams("pass");
                    if (password.equals(PASSWORD)){
                        user = new User(name,password);
                        users.put(name, user);
                        response.redirect("/");
                    }
                    else{
                        throw new Exception("Wrong Password");
                    }
                    Session session = request.session();
                    session.attribute("username", name);
                    return "";
                }
        );

        Spark.post(
                "/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null){
                        throw new Exception("Not Logged In");
                    }
                    String text = request.queryParams("message");
                    Message message = new Message(text);
                    user.messages.add(message);
                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/delete-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null){
                        throw new Exception("Not Logged In");
                    }

                    int id = Integer.valueOf(request.queryParams("id"));
                    User user = users.get(username);
                    if (id <= 0 || id -1 >= user.messages.size()){
                        throw new Exception("invalid id");
                    }
                    user.messages.remove(id-1);
                    response.redirect("/");
                    return "";
                }
        );
    }
}
