package com.example.Blink.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class GeoIpConfig {

    private final GeoDbInitializer initializer;

    public GeoIpConfig(GeoDbInitializer initializer) {
        this.initializer = initializer;
    }

    @Bean
    public DatabaseReader databaseReader() throws IOException {

        return new DatabaseReader.Builder(
                initializer.getGeoDbFile()
        ).build();
    }
}