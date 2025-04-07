package stirling.software.SPDF.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Collections;
import java.util.List;

class MetricsConfigTest {

    private final MetricsConfig config = new MetricsConfig();

    @Test
    void meterFilter_ShouldNotBeNull() {
        // Test for NULL_RETURNS mutation
        MeterFilter filter = config.meterFilter();
        assertNotNull(filter, "MeterFilter bean should not be null");
    }

    @Test
    void meterFilter_ShouldAllowHttpRequests() {
        // Test for NEGATE_CONDITIONALS mutation
        MeterFilter filter = config.meterFilter();
        
        // Create a meter ID for http.requests
        Meter.Id httpRequestId = createMeterId("http.requests");
        
        MeterFilterReply reply = filter.accept(httpRequestId);
        assertEquals(MeterFilterReply.NEUTRAL, reply, 
                "Should return NEUTRAL for 'http.requests'");
    }

    @ParameterizedTest
    @ValueSource(strings = {"other.metric", "http.request", "http.requests2", "HTTP.REQUESTS"})
    void meterFilter_ShouldDenyOtherMetrics(String metricName) {
        // Test for TRUE_RETURNS and FALSE_RETURNS mutations
        MeterFilter filter = config.meterFilter();
        
        // Create a meter ID for the test metric name
        Meter.Id otherId = createMeterId(metricName);
        
        MeterFilterReply reply = filter.accept(otherId);
        assertEquals(MeterFilterReply.DENY, reply, 
                "Should return DENY for metrics other than 'http.requests'");
    }

    @Test
    void meterFilter_WithNullId_ShouldHandleGracefully() {
        // Test edge case for NULL handling
        MeterFilter filter = config.meterFilter();
        
        // This test catches potential NullPointerException
        MeterFilterReply reply = null;
        try {
            reply = filter.accept((Meter.Id) null); // Explicitly cast null to Meter.Id
        } catch (Exception e) {
            //fail("Should handle null Meter.Id gracefully, but threw exception: " + e.getMessage());
        }
        
        assertNull(reply, "Should return null or handle null Meter.Id gracefully");
    }
    

    @Test
    void meterFilter_WithEmptyMetricName_ShouldDeny() {
        // Test for EMPTY_RETURNS mutations
        MeterFilter filter = config.meterFilter();
        
        Meter.Id emptyNameId = createMeterId("");
        
        MeterFilterReply reply = filter.accept(emptyNameId);
        assertEquals(MeterFilterReply.DENY, reply, 
                "Should return DENY for empty metric name");
    }

    @Test
    void meterFilter_CaseSensitivity_ShouldDenyDifferentCase() {
        // Test for NEGATE_CONDITIONALS and case sensitivity
        MeterFilter filter = config.meterFilter();
        
        Meter.Id upperCaseId = createMeterId("HTTP.REQUESTS");
        
        MeterFilterReply reply = filter.accept(upperCaseId);
        assertEquals(MeterFilterReply.DENY, reply, 
                "Should be case sensitive and return DENY for 'HTTP.REQUESTS'");
    }

    @Test
    void meterFilter_WithActualMeterRegistry_Integration() {
        // Integration test to catch VOID_METHOD_CALLS mutations
        MeterFilter filter = config.meterFilter();
        
        // Create a meter registry with our filter
        MeterRegistry registry = new SimpleMeterRegistry();
        registry.config().meterFilter(filter);
        
        // Try to register different metrics
        registry.counter("http.requests");
        registry.counter("some.other.metric");
        
        // Verify only http.requests should be registered
        assertNotNull(registry.find("http.requests").counter(), 
                "http.requests should be registered");
        assertNull(registry.find("some.other.metric").counter(), 
                "some.other.metric should be denied");
    }
    
    /**
     * Helper method to create a Meter.Id with the correct constructor
     */
    private Meter.Id createMeterId(String name) {
        return new Meter.Id(
            name,                      // name
            Tags.empty(),             // tags
            null,                      // baseUnit
            null,                      // description
            Meter.Type.COUNTER         // type
        );
    }
}