package io.mgporter.battleship_online;

import java.util.Arrays;
import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.mgporter.battleship_online.config.StompPrincipal;
import io.mgporter.battleship_online.models.Message;
import io.mgporter.battleship_online.models.MessageType;
import io.mgporter.battleship_online.models.Player;
import io.mgporter.battleship_online.services.LobbyService;

@Aspect
@Component
@EnableAspectJAutoProxy
public class PlayerAspect {

  private final SimpMessagingTemplate messagingTemplate;
  private final Logger logger = Logger.getLogger(PlayerAspect.class.getName());

  public PlayerAspect(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }
  
  @Around("execution(* io.mgporter.battleship_online.controllers.JoinController.joinGame*(..))")
  public Object checkGameJoinConditions(ProceedingJoinPoint joinPoint) throws Throwable {

    /* Intercept te joinGame methods and check if the player is allowed to join the room. */
    System.out.println("From INSIDE ASPECT");

    Object[] arguments = joinPoint.getArgs();
    Message message = (Message) arguments[0];
    StompPrincipal principal = (StompPrincipal) arguments[1];
    System.out.println(Arrays.asList(arguments));

    // Check if the player is already in a gameroom.
    if (principal.isInRoom()) {
      System.out.println("sending REJECTEDJOIN");
      Message rejectionMessage = Message.fromPrincipalAndType(principal, MessageType.REJECTEDJOIN_ALREADY_IN_GAME);
      messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/lobby", rejectionMessage);
      return arguments;
    }

    Object returnedValue = joinPoint.proceed();

    return returnedValue;
  }

  @Around("execution(* io.mgporter.battleship_online.controllers..*.*(..))")
  public Object logginAspect(ProceedingJoinPoint joinPoint) throws Throwable {

    String methodName = joinPoint.getSignature().getName();
    Object[] arguments = joinPoint.getArgs();

    logger.info(methodName + " called with arguments: " + Arrays.asList(arguments));

    return joinPoint.proceed();
  }

}
