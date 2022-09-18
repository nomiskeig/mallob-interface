package edu.kit.fallob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class App {
    private static final String UPDATE_VERIFIED = "UPDATE users SET isverified=? WHERE username=?";
    private static final String UPDATE_ADMIN = "UPDATE users SET usertype=? WHERE username=?";

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("This is a helper programm to update users in the fallob database.");
            System.out.println("At least two arguments are required:");
            System.out.println("The first one defines the action. Can be one of the following:");
            System.out.println(" - 'setVerified': Makes the given user verified.");
            System.out.println(" - 'setUnverified': Makes the given user unverified.");
            System.out.println(" - 'setAdmin': Makes the given user an administrator.");
            System.out.println(" - 'setNormalUser': Makes the given user a normal user.\n");
            System.out.println("The second argument is the name of the user to modify.\n");
            System.out.println(
                    "If the default configuration file should not be used, the absolute path of the configuration file to use can be passed as the third argument.");
            System.out.println("Note that the path of the database to modify as well as the username and password for the database are read from the configuarion file.");
            return;
        }

        if (args.length != 3) {
            printError("Incorrect amount of arguments provided.");
            return;
        }
        String pathToConfig = args[2];
        if (!(new File(pathToConfig)).exists()) {
            printError("The configuration file could not be found");
            return;
        }
        String configContents;
        try {

            configContents = Files.readString(Path.of(pathToConfig));
        } catch (IOException e) {
            printError(e.getMessage());
            return;
        }
        String dbUser;
        String dbPassword;
        String databasePath;
        String action = args[0];
        String user = args[1];
        try {
            JSONObject json = new JSONObject(configContents);
            JSONObject dbJson = json.getJSONObject("database");
            dbUser = dbJson.getString("databaseUsername");
            dbPassword = dbJson.getString("databasePassword");
            JSONObject pathJson = json.getJSONObject("paths");
            databasePath = pathJson.getString("databaseBasePath");
        } catch (JSONException e) {
            printError("Could not read the config file. Reason:\n" + e.getMessage());
            return;
        }
        Connection conn;
        if (databasePath.endsWith(".mv.db")) {
            databasePath = databasePath.substring(0, databasePath.length() - 6);
        }
        if (databasePath.startsWith(".")) {
            databasePath = databasePath.substring(2);
            databasePath = Paths.get(databasePath).toAbsolutePath().toString();
        }

        try {
            conn = DriverManager.getConnection("jdbc:h2:" + databasePath + ";IFEXISTS=TRUE", dbUser, dbPassword);
        } catch (SQLException e) {
            printError("Could not establish a connection to the database. Reason:\n" + e.getMessage());
            return;
        }
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users where username = ?");
            statement.setString(1, user);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                printError("User " + user + " could not be found.");
                return;
            }

        } catch (SQLException e) {
            printError("Error: " + e.getMessage());
        }
        try {
            switch (action) {
                case "setAdmin":
                    PreparedStatement statement1 = conn.prepareStatement(UPDATE_ADMIN);
                    statement1.setString(1, "ADMIN");
                    statement1.setString(2, user);
                    statement1.executeUpdate();
                    printSuccess("User " + user + " is now administrator.");
                    break;
                case "setNormalUser":
                    PreparedStatement statement2 = conn.prepareStatement(UPDATE_ADMIN);
                    statement2.setString(1, "NORMAL_USER");
                    statement2.setString(2, user);
                    statement2.executeUpdate();
                    printSuccess("User " + user + " is now a normal user.");
                    break;
                case "setVerified":
                    PreparedStatement statement3 = conn.prepareStatement(UPDATE_VERIFIED);
                    statement3.setBoolean(1, true);
                    statement3.setString(2, user);
                    statement3.executeUpdate();
                    printSuccess("User " + user + " is now verified.");
                    break;
                case "setUnverified":
                    PreparedStatement statement4 = conn.prepareStatement(UPDATE_VERIFIED);
                    statement4.setBoolean(1, false);
                    statement4.setString(2, user);
                    statement4.executeUpdate();
                    printSuccess("User " + user + " is now not verified.");
                    break;
                default:
                    printError(
                            "Error: The only possible actions are 'setAdmin', 'setNormalUser', 'setVerified', 'setUnverified'.");
            }

        } catch (SQLException e) {
            printError("Could update the user. Reason:\n" + e.getMessage());
        }

    }

    private static void printError(String error) {
        System.out.println("\033[0;31m" + error + "\033[0m");

    }
    private static void printSuccess(String success) {
        System.out.println("\033[0;32m"+ success + "\033[0m");
    }
}
