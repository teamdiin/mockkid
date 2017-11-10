package br.com.moip.mockkid.configuration;


import br.com.moip.mockkid.model.ConfigRoot;
import br.com.moip.mockkid.model.Configuration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class ConfigParser {

    private ObjectMapper mapper;

    public ConfigParser() {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public Map<String, Configuration> configurations() {
        return openConfigurations();
    }
    
    private Map<String, Configuration> openConfigurations() {
        Map<String, Configuration> configurations = new HashMap<>();

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:configuration/*.yaml");
            extractConfigsFromResources(configurations, resources);
        } catch (IOException ioe) {
            System.out.println("Something nasty has happened while trying to read configuration files!");
            ioe.printStackTrace();
            throw new IllegalStateException(ioe);
        }

        printConfig(configurations);
        return configurations;
    }

    private void extractConfigsFromResources(Map<String, Configuration> configurations, Resource[] resources) throws IOException {
        for (Resource r : resources) {
            try {
                ConfigRoot configRoot = toConfiguration(r.getInputStream());
                if (configRoot != null)
                    configurations.put(mapKey(configRoot.getConfiguration()), configRoot.getConfiguration());
            } catch (Exception e) {
                System.out.println("Failed reading configuration from file: " + r.getFile().getName() + ", skipping...");
                e.printStackTrace();
            }
        }
    }

    private String mapKey(Configuration configuration) {
        return configuration.getEndpoint().getMethod() + ":" + configuration.getEndpoint().getUrl();
    }

    private ConfigRoot toConfiguration(InputStream in) throws IOException {
        return mapper.readValue(in, ConfigRoot.class);
    }

    private void printConfig(Map<String, Configuration> configurations) {
        System.out.println("-------------------------");
        System.out.println("EXTRACTED CONFIGURATIONS:");
        for (String key : configurations.keySet()) {
            System.out.println();
            System.out.println("KEY= " + key);
            System.out.println("CONFIG= " + configurations.get(key));
        }
        System.out.println("-------------------------");
    }

}