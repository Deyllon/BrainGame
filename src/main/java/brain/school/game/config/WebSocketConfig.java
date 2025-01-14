package brain.school.game.config;


import brain.school.game.websocket.GameSocketHandler;
import brain.school.game.websocket.MyWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MyWebSocketHandler myWebSocketHandler;
    private final GameSocketHandler gameSocketHandler;

    public WebSocketConfig(MyWebSocketHandler myWebSocketHandler, GameSocketHandler gameSocketHandler) {
        this.myWebSocketHandler = myWebSocketHandler;
        this.gameSocketHandler = gameSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler, "/ws")
                .setAllowedOrigins("http://localhost:8080");
        registry.addHandler(gameSocketHandler, "/ws/game")
                .setAllowedOrigins("http://localhost:8080");
    }

}
