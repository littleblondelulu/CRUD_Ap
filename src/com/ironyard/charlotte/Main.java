package com.ironyard.charlotte;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

import static spark.Spark.get;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
//     users.put("adam", new User("ARod", "2832"));
//      wines.add(new Wine(wines.size(), id(wines.size), "wineName", "5 stars");
       // users.put("Alice", new User(("Alice"), "2345"));
        User lulu   = new User("lulu", "2345");

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();

                    String name = session.attribute("userName");

                    User user = users.get(name);

                    HashMap m = new HashMap();

                    if (user == null) {

                        return new ModelAndView(m, "login.html");

                    } else {

                        m.put("userName", name);

                        m.put("wines", user.wines);

                    return new ModelAndView(m, "home.html");

                    }

                }),

                new MustacheTemplateEngine()
        );


        //User authentication
        Spark.post(
                "/login",
                ((request, response) -> {

                    String name = request.queryParams("userName");
                    String pw = request.queryParams("password");
                    User user = users.get(name);

                    //Use the login form to double as a create new account form
                    if (user == null) {
                        user = new User(name, pw);
                        users.put(name, user);
                    }

                    //Request a session to log the user activity while logged in
                    Session session = request.session();
                    session.attribute("userName", name);

                    //Restrict access if pw entered does not match the value stored in the user object
                    if (!user.getPassword().equals(pw)) {
                        session = request.session();
                        session.invalidate();
                        response.redirect("/");
                    }

                    response.redirect("/");
                    return "";

                })
        );


        Spark.post(
                "/create",
                ((request, response) -> {
                    //Start session and assign it to user
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);


                    String wineName = request.queryParams("wineName");
                    String rating = request.queryParams("rating");

                    Wine newWine = new Wine(wineName, rating);
                    user.wines.add(newWine);

                    if (newWine == null) {
                        throw new Exception("Didn't get necessary query parameters.");
                    }
//
                    response.redirect("/");
                    return "";

//
//                    String referrer = request.headers("Referrer");
//                    response.redirect(referrer != null ? referrer : "/");
//                    response.redirect(referrer != null ? referrer : "/");
//                    response.redirect("/");
//                    return "";

            })
        );

        Spark.post(
                "/logout",
                ((request, response) ->{
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(
                "/update-wine/:id",
                ((request, response) -> {

                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);

                    int idNum = Integer.valueOf(request.params("updateID"));

                    HashMap m = new HashMap();

                    m.put("id", idNum);

                    m.put("name", user.name);

                    m.put("wines", user.wines);

                    return new ModelAndView(m, "/?update/idNum={{id}}");

                }),
                new MustacheTemplateEngine()
        );


        Spark.post(
            "/update-entry/:id",
            ((request, response) -> {
                Session session = request.session();
                String name = session.attribute("userName");
                User user = users.get(name);

                int idNum = Integer.valueOf(request.queryParams("updateID"));

                String wineName = request.queryParams("wineName");
                String rating = request.queryParams("rating");
                Wine updatedWine = new Wine(wineName, rating);

                user.wines.set(idNum, updatedWine);

                response.redirect(request.headers("home.html"));
                return "";

            })

        );

    Spark.post(
            "/delete-wine/:id",
            ((request, response) -> {
                Session session = request.session();
                String name = session.attribute("userName");
                User user = users.get(name);

                int idNum = Integer.valueOf(request.params("deleteID"));

                user.wines.remove(idNum);

                response.redirect("/");
                return "";

            })
    );


    }
}
