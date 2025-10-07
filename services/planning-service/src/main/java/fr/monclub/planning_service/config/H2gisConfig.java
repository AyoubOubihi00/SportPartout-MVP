package fr.monclub.planning_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Config H2 “simple” (pas H2GIS). 
 */
@Configuration
@Profile({"dev","test"})
public class H2gisConfig{
    
}
