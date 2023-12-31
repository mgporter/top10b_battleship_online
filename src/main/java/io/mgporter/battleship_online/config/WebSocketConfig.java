package io.mgporter.battleship_online.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setHandshakeHandler(new CustomHandshakeHandler()).setAllowedOrigins("http://localhost:3000", "https://mgporter.github.io");
    registry.addEndpoint("/ws").setHandshakeHandler(new CustomHandshakeHandler()).setAllowedOrigins("http://localhost:3000", "https://mgporter.github.io").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/lobby", "/game", "/queue");
    registry.setUserDestinationPrefix("/user");
  }

}
