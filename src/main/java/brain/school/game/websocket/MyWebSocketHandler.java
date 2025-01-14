package brain.school.game.websocket;

import brain.school.game.config.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;


@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, String> sessionEmailMap = new HashMap<>();

    @Autowired
    TokenService tokenService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> headers = session.getHandshakeHeaders().toSingleValueMap();

        String token = headers.get("authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            var login = tokenService.validateToken(token);
            if (login != null) {
                sessionEmailMap.put(session.getId(), login);
                sessions.add(session);
            } else {
                session.close();
            }
        } else {
            session.close();
        }

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String email = sessionEmailMap.get(session.getId());

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage (email + ": " + payload));
            }
        }
    }
}
