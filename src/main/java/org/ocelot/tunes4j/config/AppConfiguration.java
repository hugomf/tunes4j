package org.ocelot.tunes4j.config;

import org.ocelot.tunes4j.Tunes4JLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan("org.ocelot.tunes4j")
@Configuration
@EnableAsync
public class AppConfiguration {
	
    @Bean
    public Tunes4JLauncher tunes4JLauncher() {
	     return new Tunes4JLauncher();
    }
    
}
