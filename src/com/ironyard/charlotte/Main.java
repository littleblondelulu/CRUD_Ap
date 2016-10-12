package com.ironyard.charlotte;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:./main");


        Server.createWebServer().start();

        //Create a test for insertUser and selectUser.
        //Create a test for insertEntry and selectEntry.

        createTables(conn);

        Spark.init();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = selectUser(conn, name);

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
                    User user = selectUser(conn, name);
                    //Use the login form to double as a create new account form
                    if (user == null) {
                        user = new User(name, pw);
                        insertUser(conn, name, pw);
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
                    User user = selectUser(conn, name);

                    String wineName = request.queryParams("wineName");
                    String rating = request.queryParams("rating");

                    Wine newWine = new Wine(wineName, rating);
                    insertWine(conn, user, newWine);
                    user.wines.add(newWine);
                    if (newWine == null) {
                        throw new Exception("Didn't get necessary query parameters.");
                    }

                    response.redirect("/");
                    return "";
                })
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

        Spark.get(
                "/update-entry/:id",
                ((request, response) -> {

                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = selectUser(conn, name);

                    int idNum = Integer.valueOf(request.params("id"));

                    HashMap m = new HashMap();
                    m.put("id", idNum);
                    return new ModelAndView(m, "update.html");
                }),

                new MustacheTemplateEngine()
        );

        Spark.post(
                "/update-entry/:id",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = selectUser(conn, name);

                    int idNum = Integer.valueOf(request.params("id"));

                    String wineName = request.queryParams("wineName");
                    String rating = request.queryParams("rating");

                    Wine updatedWine = new Wine(wineName, rating, idNum);

                    updateWine(conn, updatedWine);

                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/delete",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = selectUser(conn, name);

                    int idNum = Integer.valueOf(request.queryParams("deleteID"));

                    deleteWine(conn, idNum);

                    response.redirect("/");
                    return "";
                })
        );

    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS wines (id IDENTITY, user_id int, wineName VARCHAR, rating VARCHAR)");
    }

    //methods to insert and select a single user:
    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            User u = new User(id, name, password);

            u.wines = getWines(u);

            return u;
        }
        return null;
    }

    public static List<Wine> getWines(User user) throws SQLException {
        PreparedStatement stmt = null;

        stmt = conn.prepareStatement("SELECT * from wines where user_id = ?");
        stmt.setInt(1, user.id);

        ResultSet set = stmt.executeQuery();


        List<Wine> results = new ArrayList<>();

        while (set.next()) {
            int id = set.getInt("id");
            String name = set.getString("wineName");
            String rating = set.getString("rating");

            results.add(new Wine(name, rating, id));
        }

        return results;
    }

    //methods to insert and select a single wine:
    public static void insertWine(Connection conn, User user, Wine wine) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO wines VALUES (NULL, ?, ?, ?)");
        stmt.setInt(1, user.id);
        stmt.setString(2, wine.getWineName());
        stmt.setString(3, wine.getRating());
        stmt.execute();
    }

    public static Wine selectWineById(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM wines INNER JOIN users ON wines.user_id = users.id WHERE wines.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String wineName = results.getString("wines.wineName");
            String rating = results.getString("wines.rating");
            return new Wine(wineName, rating);
        }
        return null;
    }

    public static void updateWine(Connection conn, Wine newWine) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE wines set wineName = ?, rating = ? where id = ?");
        stmt.setString(1, newWine.getWineName());
        stmt.setString(2, newWine.getRating());
        stmt.setInt(3, newWine.getId());
        stmt.execute();
    }

    public static void deleteWine(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM wines WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

}

