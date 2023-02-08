package kr.wadiz.platform.api.nicepay.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@RequiredArgsConstructor
@Component
@ConfigurationProperties("wadiz")
public class WadizConfig {
    private Map<String, Map<String, String>> service;
}
