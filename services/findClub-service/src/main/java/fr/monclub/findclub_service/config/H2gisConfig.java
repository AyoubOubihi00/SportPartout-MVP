package fr.monclub.findclub_service.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
@RequiredArgsConstructor
@Profile({"dev","test"})
public class H2gisConfig {

  private final DataSource dataSource;

  @PostConstruct
  void init() {
    try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
      // Charge toutes les fonctions ST_*
      s.execute("""
        CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL
        FOR "org.h2gis.functions.factory.H2GISFunctions.load"
      """);
      s.execute("CALL H2GIS_SPATIAL()");
    } catch (Exception e) {
      throw new IllegalStateException("Failed to init H2GIS", e);
    }
  }
}
