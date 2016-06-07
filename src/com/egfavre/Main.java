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
                    Session session = request.session();
                    String username = session.attribute("username");
                    HashMap m = new HashMap();
                    if (username==null){
                        return new ModelAndView(m, "index.html");
                    }
                    else {
                        User user = users.get(username);
                        m.put("messages", User.messages);
                        return new ModelAndView(m, "messages.html");
                    }
                },
        new MustacheTemplateEngine()
        );

        Spark.post(
                "/create-user",
                (request, response) ->{
                    String name = request.queryParams("username");
                    String password = request.queryParams("pass");
                    if (name == null || password == null){
                        throw new Exception("Name or pass not sent.");
                    }

                    User user = users.get(name);
                    if (!password.equals(PASSWORD)){
                        throw new Exception("wrong password");
                    }
                    else if (user == null){
                        user = new User(name, password);
                      users.put(name, user);
                    }

                    Session session = request.session();
                    session.attribute("username", name);

                    response.redirect("/");
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
        Spark.post(
                "/edit-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null){
                        throw new Exception("Not Logged In");
                    }

                    int idEdit = Integer.valueOf(request.queryParams("idEdit"));
                    String edits = request.queryParams("edits");


                    User user = users.get(username);
                    if (idEdit <= 0 || idEdit -1 >= user.messages.size()){
                        throw new Exception("invalid id");
                    }
                    Message message = new Message(edits);
                    user.messages.set(idEdit-1, message);
                    response.redirect("/");
                    return "";
                }
        );
    }
}
