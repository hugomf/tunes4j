package org.ocelot.tunes4j.config;

import org.ocelot.tunes4j.Tunes4JLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("org.ocelot.tunes4j")
public class AppConfiguration {
	
    @Bean
    public Tunes4JLauncher tunes4JLauncher() {
	     return new Tunes4JLauncher();
    }
    
}
