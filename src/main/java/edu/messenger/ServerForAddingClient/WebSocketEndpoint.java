package edu.messenger.ServerForAddingClient;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebSocket
public class WebSocketEndpoint {

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
    public void clientMessage(Session session, String message) throws IOException {
        session.getRemote().sendString(message + "OK");
        System.out.println("Client message" + message);
    }
}
