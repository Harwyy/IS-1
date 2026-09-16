package ru.itmo.secureapi.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class Sanitizer {
    public String clean(String value) {
        return Jsoup.clean(value == null ? "" : value, Safelist.none());
    }
}
