package br.com.moip.mockkid.configuration;

import br.com.moip.mockkid.model.ConfigRoot;
import br.com.moip.mockkid.model.Configuration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@org.springframework.context.annotation.Configuration
public class ConfigParser {

    private static final Logger logger = LoggerFactory.getLogger(ConfigParser.class);

    private ObjectMapper mapper;

    @Value("${configuration.path}")
    private String configurationPath;

    public ConfigParser() {
        mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public Configurations configurations() {
        return openConfigurations();
    }
    
    private Configurations openConfigurations() {
        Configurations configurations = new Configurations();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(configurationPath);
            extractConfigsFromResources(configurations, resources);
        } catch (IOException ioe) {
            logger.error("Something nasty has happened while trying to read configuration files!", ioe);
            throw new IllegalStateException(ioe);
        }

        printConfigurations(configurations);
        return configurations;
    }

    private void extractConfigsFromResources(Map<String, Configuration> configurations, Resource[] resources) throws IOException {
        for (Resource r : resources) {
            try {
                ConfigRoot configRoot = toConfiguration(r.getInputStream());
                if (configRoot != null)
                    configurations.put(mapKey(configRoot.getConfiguration()), configRoot.getConfiguration());
            } catch (Exception e) {
                logger.warn("Failed reading configuration from file: " + r.getFile().getName() + ", skipping...", e);
            }
        }
    }

    private String mapKey(Configuration configuration) {
        return configuration.getEndpoint().getMethod() + ":" + configuration.getEndpoint().getUrl();
    }

    private ConfigRoot toConfiguration(InputStream in) throws IOException {
        return mapper.readValue(in, ConfigRoot.class);
    }

    private void printConfigurations(Configurations configurations) {
        logger.info("-------------------------");
        logger.info("EXTRACTED CONFIGURATIONS");
        logger.info("-------------------------");
        for (String key : configurations.keySet()) {
            logger.info("KEY = " + key);
            logger.info("CONFIG = " + configurations.get(key));
            logger.info("-------------------------");
        }
    }

}
