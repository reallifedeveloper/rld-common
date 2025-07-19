package com.reallifedeveloper.common.infrastructure.messaging;

import static com.reallifedeveloper.common.infrastructure.messaging.KafkaSecurityConfiguration.SASL_MECHNISM_CONFIGURATION_KEY;
import static com.reallifedeveloper.common.infrastructure.messaging.KafkaSecurityConfiguration.SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY;
import static com.reallifedeveloper.common.infrastructure.messaging.KafkaSecurityConfiguration.applySecurityConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class KafkaSecurityConfigurationTest {

    private static final String TRUSTSTORE_PASSWORD = "reallifedeveloper";
    private static final String TRUSTSTORE_ALIAS = "rld";

    @Test
    public void applySecurityConfigurationStoresTheProperty() throws Exception {
        Map<String, Object> props = new HashMap<>();
        applySecurityConfiguration(SASL_MECHNISM_CONFIGURATION_KEY, "foo", props);
        assertEquals(1, props.size());
        assertEquals("foo", props.get(SASL_MECHNISM_CONFIGURATION_KEY));
    }

    @Test
    public void applySecurityConfigurationTwiceWithSamePropertyShouldFail() throws Exception {
        Map<String, Object> props = new HashMap<>();
        applySecurityConfiguration(SASL_MECHNISM_CONFIGURATION_KEY, "foo", props);
        Exception e = assertThrows(IllegalStateException.class,
                () -> applySecurityConfiguration(SASL_MECHNISM_CONFIGURATION_KEY, "bar", props));
        assertEquals("Trying to reset the value of property that has already been set: configurationKey=sasl.mechanism, "
                + "configurationValue=bar, configurationProperties=" + props, e.getMessage());
    }

    @Test
    public void applySecurityConfigurationWithTruststoreLocationSupportsClasspathValue() throws Exception {
        Map<String, Object> props = new HashMap<>();
        applySecurityConfiguration(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY, "classpath:/kafka/test.pfx", props);
        assertEquals(1, props.size());
        assertNotNull(props.get(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY));
        String pathToTruststore = props.get(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY).toString();
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        try (InputStream is = new FileInputStream(pathToTruststore)) {
            keyStore.load(is, TRUSTSTORE_PASSWORD.toCharArray());
            Certificate rldCert = keyStore.getCertificate(TRUSTSTORE_ALIAS);
            assertNotNull(rldCert);
        }
    }

    @Test
    public void applySecurityConfigurationWithTruststoreLocationFailsIfClasspathResourceDoesNotExist() throws Exception {
        Map<String, Object> props = new HashMap<>();
        Exception e = assertThrows(FileNotFoundException.class,
                () -> applySecurityConfiguration(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY, "classpath:/no_such_resource", props));
        assertEquals("Kafka trust store not found: classpath:/no_such_resource", e.getMessage());
    }

    @Test
    public void applySecurityConfigurationWithTruststoreLocationSupportsNonClasspathValue() throws Exception {
        Map<String, Object> props = new HashMap<>();
        applySecurityConfiguration(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY, "/kafka/test.pfx", props);
        assertEquals(1, props.size());
        assertEquals("/kafka/test.pfx", props.get(SSL_TRUSTSTORE_LOCATION_CONFIGURATION_KEY));
    }
}
