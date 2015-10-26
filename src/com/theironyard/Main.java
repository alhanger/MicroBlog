package com.theironyard;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ArrayList<Post> posts = new ArrayList<>();
        User currentUser = new User();
        Spark.staticFileLocation("/public");
        Spark.init();
        Spark.post(
                "/create-user",
                ((request, response) -> {
                    String name = request.queryParams("username");
                    String pw = request.queryParams("password");
                    if (currentUser.name == null) {
                        currentUser.name = name;
                        currentUser.password = pw;
                        response.redirect("/posts");
                    }
                    else if (pw.equals(currentUser.password) && name.equals(currentUser.name)) {
                        response.redirect("/posts");
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
                    String post = request.queryParams("userPost");
                    Post newPost = new Post();
                    newPost.text = post;
                    posts.add(newPost);
                    response.redirect("/posts");
                    return "";
                })
        );
        Spark.get(
                "/posts",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    m.put("user", currentUser.name);
                    m.put("posts", posts);
                    return new ModelAndView(m, "posts.html");
                }),
                new MustacheTemplateEngine()
        );
    }
}
