package brain.school.game.websocket;

import brain.school.game.config.TokenService;
import brain.school.game.model.User;
import brain.school.game.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;


@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private final Map<String, Set<WebSocketSession>> gameRoom = new HashMap<>();

    private final Map<WebSocketSession, String> sessionToRoom = new HashMap<>();

    private final Map<WebSocketSession, Integer> sessionPoints = new HashMap<>();

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String gameRoomId = findRoomWithSpace();

        if (gameRoomId == null) {

            gameRoomId = createNewRoom();
        }

        gameRoom.get(gameRoomId).add(session);

        sessionToRoom.put(session, gameRoomId);
        sessionPoints.put(session, 0);

        session.sendMessage(new TextMessage("Você entrou na sala " + gameRoomId));


        if (gameRoom.get(gameRoomId).size() == 2) {
            for (WebSocketSession player : gameRoom.get(gameRoomId)) {
                if (player.isOpen()) {
                    player.sendMessage(new TextMessage("A partida começou na sala " + gameRoomId));
                }
            }
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        Map<String, String> headers = session.getHandshakeHeaders().toSingleValueMap();

        String token = headers.get("authorization").substring(7);
        String login  = tokenService.validateToken(token);
        System.out.println("Mensagem login: " + login);
        User user = userRepository.findByEmail(login);
        String payload = message.getPayload();
        System.out.println("Mensagem recebida: " + payload);



        String roomId = sessionToRoom.get(session);
        if (roomId != null) {
            WebSocketSession topSession = null;
            int maxPoints = 0;
            Set<WebSocketSession> playersInRoom = gameRoom.get(roomId);
            if ("c".equals(payload)) {
                // Atualiza os pontos do jogador
                sessionPoints.put(session, sessionPoints.get(session) + 10);
            }

            for (WebSocketSession player : playersInRoom) {
                Integer points = sessionPoints.getOrDefault(player, 0);
                if (points > maxPoints) {
                    maxPoints = points;
                    topSession = player;
                }
            }

            if ("fim".equals(payload) && topSession != null) {
                for (WebSocketSession player : playersInRoom) {
                    if (player.isOpen()) {
                        player.sendMessage(new TextMessage("Jogador com mais pontos: " + topSession.getId() + " com " + maxPoints + " pontos!"));
                        player.sendMessage(new TextMessage("Jogador com mais pontos: " + login + " com " + maxPoints + " pontos."));
                    }
                }
            }

        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        gameRoom.values().forEach(room -> room.remove(session));
        System.out.println("Jogador desconectado: " + session.getId());
    }


    private String findRoomWithSpace() {

        for (Map.Entry<String, Set<WebSocketSession>> entry : gameRoom.entrySet()) {
            if (entry.getValue().size() < 2) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String createNewRoom() {
        String roomId = "room-" + (gameRoom.size() + 1);
        gameRoom.put(roomId, new HashSet<>());
        return roomId;
    }
}
