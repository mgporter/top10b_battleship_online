package io.mgporter.battleship_online;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"io.mgporter.battleship_online.controllers", 
                                "io.mgporter.battleship_online.models", 
                                "io.mgporter.battleship_online.packets",
                                "io.mgporter.battleship_online.repositories",
                                "io.mgporter.battleship_online.services"})
public class ProjectConfig {
    


}