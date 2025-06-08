package com.reallifedeveloper.common.infrastructure.messaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * Utility class used to set the security-related Kafka configuration properties when creating consumers and producers.
 * <p>
 * In particular, it allows reading the truststore from a classpath resource using a {@code classpath:} prefix.
 *
 * @author RealLifeDeveloper
 */
public final class KafkaSecurityConfiguration {

    /**
     * The configuration property to use to set security protocol, e.g., {@code SASL_SSL}.
     */
    public static final String SECURITY_PROTOCOL_CONFIGURATION_KEY = "security.protocol";

    /**
     * The configuration property to use to set the location of the truststore, supports {@code classpath} prefixes.
     */
    public static final String SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY = "ssl.truststore.location";

    /**
     * The configuration property to use to set the password of the truststore.
     */
    public static final String SSL_TRUSTSTORE_PASSWORD_CONFIGURATION_KEY = "ssl.truststore.password";

    /**
     * The configuration property to use to set the SASL mechanism, e.g., {@code PLAIN}.
     */
    public static final String SASL_MECHNISM_CONFIGURATION_KEY = "sasl.mechanism";

    /**
     * The configuration property to use to set the JAAS config.
     */
    public static final String SASL_JAAS_CONFIG_CONFIGURATION_KEY = "sasl.jaas.config";

    /**
     * The configuration property to use to set the default auto commit interval, in milliseconds.
     */
    public static final int DEFAULT_CONSUMER_AUTO_COMMIT_INTERVAL_MS = 1_000;

    /**
     * The configuration property to use to set the default session timeout, in milliseconds.
     */
    public static final int DEFAULT_CONSUMER_SESSION_TIMEOUT_MS = 15_000;

    private static final DefaultResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    private KafkaSecurityConfiguration() {
        // Hide the constructor since this is a utility class containing only static methods.
    }

    /**
     * Sets the value of a configuration property.
     * <p>
     * A value can be set only once for a particular property; trying to reset a value causes an exception
     * to be thrown.
     * <p>
     * There is special handling of the {@value #SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY} configuration property,
     * with support for reading a trust store from the classpath if the configuration value starts with
     * {@code classpath:}.
     * <p>
     * In the case that the {@value #SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY} value starts with {@code classpath:},
     * the trust store is read as a resource using the current thread's context classloader. The store is copied to a
     * temporary file, and the path of the file is used as the value to set.
     *
     * @param configurationKey the configuration key to apply
     * @param configurationValue the configuration value
     * @param configurationProperties the currently set configuration properties
     *
     * @throws IllegalStateException if trying to reset a value
     * @throws IOException if reading the trust store from classpath fails
     */
    public static void applySecurityConfiguration(final String configurationKey, final String configurationValue,
            final Map<String, Object> configurationProperties) throws IOException {

        if (SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY.equals(configurationKey)) {
            applySslTruststoreLocationConfiguration(configurationValue, configurationProperties);
        } else {
            applyConfigurationIfNotAlreadySet(configurationKey, configurationValue, configurationProperties);
        }
    }

    private static void applySslTruststoreLocationConfiguration(final String sslTruststoreConfigurationValue,
            final Map<String, Object> configurationProperties) throws IOException {
        if (sslTruststoreConfigurationValue.startsWith("classpath:")) {
            final Resource jksResource = RESOURCE_LOADER.getResource(sslTruststoreConfigurationValue);

            if (!jksResource.exists()) {
                throw new FileNotFoundException("Kafka trust store not found: " + sslTruststoreConfigurationValue);
            }

            final File tempFile = File.createTempFile("truststore", ".jks");
            Files.copy(jksResource.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            applyConfigurationIfNotAlreadySet(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY, tempFile.getCanonicalPath(),
                    configurationProperties);
        } else {
            applyConfigurationIfNotAlreadySet(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY, sslTruststoreConfigurationValue,
                    configurationProperties);
        }
    }

    private static void applyConfigurationIfNotAlreadySet(String configurationKey, String configurationValue,
            Map<String, Object> configurationProperties) {
        if (configurationProperties.containsKey(configurationKey)) {
            String msg = String.format(
                    "Trying to reset the value of property that has already been set: configurationKey=%s, "
                            + "configurationValue=%s, configurationProperties=%s",
                    configurationKey, configurationValue, configurationProperties);
            throw new IllegalStateException(msg);
        }
        configurationProperties.put(configurationKey, configurationValue);
    }

}
