package com.mifincaapp.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SystemController {

    @GetMapping("/api/system/time")
    public Map<String, String> getSystemTime() {
        Map<String, String> response = new HashMap<>();

        OffsetDateTime nowSystemDefault = OffsetDateTime.now();
        OffsetDateTime nowBogota = OffsetDateTime.now(ZoneId.of("America/Bogota"));

        response.put("zona_jvm", ZoneId.systemDefault().toString());
        response.put("ahora_system_default", nowSystemDefault.toString());
        response.put("ahora_bogota", nowBogota.toString());

        return response;
    }
}
