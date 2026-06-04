package com.example.Blink.config;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class GeoDbInitializer {

    private File geoDbFile;

    @PostConstruct
    public void init() throws Exception {

        InputStream is = new ClassPathResource("geo/dbip-country-lite-2026-06.mmdb")
                .getInputStream();

        File tempFile = File.createTempFile("geoip", ".mmdb");
        tempFile.deleteOnExit();

        Files.copy(is, tempFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        this.geoDbFile = tempFile;
    }

    public File getGeoDbFile() {
        return geoDbFile;
    }
}