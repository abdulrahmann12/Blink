package com.example.Blink.url_click.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
public class GeoIpService {

    private final DatabaseReader reader;

    public GeoIpService(DatabaseReader reader) {
        this.reader = reader;
    }

    public String getCountry(String ip) {
        if (ip == null || ip.isBlank()) {
            return "UNKNOWN";
        }
        if (ip.equals("127.0.0.1") || ip.equals("::1")) {
            return "LOCAL";
        }
        try {
            InetAddress address = InetAddress.getByName(ip);

            CountryResponse response = reader.country(address);

            return response.getCountry().getIsoCode();

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}