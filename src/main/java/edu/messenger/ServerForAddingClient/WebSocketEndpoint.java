package edu.messenger.ServerForAddingClient;

import edu.messenger.Database.DatabaseHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebSocket
public class WebSocketEndpoint {
    private static final String REGISTRATION = "^registration:(.*)$";
    private static final String LOGIN = "^login:(.*)$";
    private static final String CHAT = "^createChat:(.*)$";
    private static final String SEND_MESSAGE = "^sendMessage:(.*)$";
    private static final Set<Session> sessionList = Collections.synchronizedSet(new HashSet<>());

    @OnWebSocketConnect
    public void clientConnected(Session session) {
        sessionList.add(session);
        System.out.println("New Client connected" + session.getRemoteAddress().toString());
    }


    @OnWebSocketClose
    public void clientClose(Session session) {
        sessionList.remove(session);
        System.out.println("Client Disconnected" + session.getRemoteAddress().toString());
    }

    @OnWebSocketError
    public void clientError(Throwable throwable) {
        System.out.println("Client disconnected" + throwable.toString());
    }

    @OnWebSocketMessage
    public void clientMessage(Session session, String message) throws SQLException, IOException {
        if (message.matches(REGISTRATION)) {
            String json = message.substring(13);
            JSONObject jsonObject = new JSONObject(json);
            registration(session, jsonObject);
            return;
        }
        if (message.matches(LOGIN)) {
            String json = message.substring(6);
            JSONObject jsonObject = new JSONObject(json);
            logIn(session, jsonObject);
            return;
        }
        if (message.matches(CHAT)) {
            String json = message.substring(11);
            JSONObject jsonObject = new JSONObject(json);
            dialogueRegistration(session, jsonObject);
            return;
        }
        if (message.matches(SEND_MESSAGE)) {
            String json = message.substring(12);
            JSONObject jsonObject = new JSONObject(json);
            changeDialogue(session, jsonObject);
        }
    }

    private void registration(Session session, JSONObject jsonObject) throws SQLException, IOException {
        String nickName = jsonObject.getString("nickname");
        String password = jsonObject.getString("password");
        if (DatabaseHandler.signUpUser(nickName, password, session.getRemoteAddress().toString())) {
            session.getRemote().sendString("Successful registration");
        } else {
            session.getRemote().sendString("This nickname is already exist, try different nickname!");
        }
    }

    private void logIn(Session session, JSONObject jsonObject) throws SQLException, IOException {
        String nickName = jsonObject.getString("nickname");
        String password = jsonObject.getString("password");
        if (DatabaseHandler.logIn(nickName, password)) {
            session.getRemote().sendString("Successfully logged in account");
        } else {
            session.getRemote().sendString("Incorrect password, try again!");
        }
    }

    private void dialogueRegistration(Session session, JSONObject jsonObject) throws SQLException, IOException {
        String user1 = jsonObject.getString("user1");
        String user2 = jsonObject.getString("user2");
        if (DatabaseHandler.signUpChat(user1, user2, "")) {
            session.getRemote().sendString("Successfully created new chat");
        } else {
            session.getRemote().sendString("Char with this user is already exist, try again!");
        }
    }

    private void changeDialogue(Session session, JSONObject jsonObject) throws SQLException, IOException {
        String user1 = jsonObject.getString("user1");
        String user2 = jsonObject.getString("user2");
        if (DatabaseHandler.signUpChat(user1, user2, "")) {
            session.getRemote().sendString("Successfully created new chat");
        } else {
            session.getRemote().sendString("Char with this user is already exist, try again!");
        }
    }
}
