package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<String, User> users = new HashMap<>();
        Spark.init();
        Spark.post(
                "/create-user",
                ((request, response) -> {
                    Session session = request.session();

                    String name = request.queryParams("username");
                    String pw = request.queryParams("password");

                    session.attribute("username", name);

                    if (users.get(name) == null) {
                        User user1 = new User();
                        user1.name = name;
                        user1.password = pw;
                        users.put(name, user1);
                        response.redirect("/");
                    }
                    else if (pw.equals(users.get(name).password) && name.equals(users.get(name).name)) {
                        response.redirect("/");
                    }
                    else {
                        return "There was an error";
                    }
                    return "";
                })
        );
        Spark.post(
                "/create-post",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    String post = request.queryParams("userPost");

                    if (!post.isEmpty()) {
                        Post newPost = new Post();
                        newPost.id = users.get(name).posts.size() + 1;
                        newPost.text = post;
                        users.get(name).posts.add(newPost);
                    }
                    response.redirect("/");
                    return "";
                })
        );
        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    if (username == null) {
                        return new ModelAndView(new HashMap(), "not-logged-in.html");
                    }
                    HashMap m = new HashMap();
                    m.put("user", username);
                    m.put("posts", users.get(username).posts);
                    return new ModelAndView(m, "logged-in.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete-post",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    String id = request.queryParams("postid");
                    try {
                        int idNum = Integer.valueOf(id);
                        users.get(name).posts.remove(idNum - 1);
                        for (int i = 0; i < users.get(name).posts.size(); i++) {
                            users.get(name).posts.get(i).id = i + 1;
                        }
                    } catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                })
        );
    }
}
