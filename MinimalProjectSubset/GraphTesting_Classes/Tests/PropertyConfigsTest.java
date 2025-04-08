package stirling.software.SPDF.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PropertyConfigsTest {

    // -------- Original Tests --------

    @Test
    public void testGetBooleanValue_WithKeys() {
        // Define keys and default value
        List<String> keys = Arrays.asList("test.key1", "test.key2", "test.key3");
        boolean defaultValue = false;

        // Set property for one of the keys
        System.setProperty("test.key2", "true");

        // Call the method under test
        boolean result = PropertyConfigs.getBooleanValue(keys, defaultValue);

        // Verify the result
        assertEquals(true, result);
    }

    @Test
    public void testGetStringValue_WithKeys() {
        // Define keys and default value
        List<String> keys = Arrays.asList("test.key1", "test.key2", "test.key3");
        String defaultValue = "default";

        // Set property for one of the keys
        System.setProperty("test.key2", "value");

        // Call the method under test
        String result = PropertyConfigs.getStringValue(keys, defaultValue);

        // Verify the result
        assertEquals("value", result);
    }

    @Test
    public void testGetBooleanValue_WithKey() {
        // Define key and default value
        String key = "test.key";
        boolean defaultValue = true;

        // Call the method under test without setting the property.
        boolean result = PropertyConfigs.getBooleanValue(key, defaultValue);

        // Verify the result returns the default value.
        assertEquals(true, result);
    }

    @Test
    public void testGetStringValue_WithKey() {
        // Define key and default value
        String key = "test.key";
        String defaultValue = "default";

        // Call the method under test without setting the property.
        String result = PropertyConfigs.getStringValue(key, defaultValue);

        // Verify the result returns the default value.
        assertEquals("default", result);
    }

    // -------- Additional Tests for Increased Line Coverage (COE891) --------

    @Test
    public void testGetBooleanValue_WithKey_PropertySetToFalse() {
        // This test verifies that when a property is set to "false", the method returns false.
        String key = "test.boolean";
        boolean defaultValue = true;
        System.setProperty(key, "false");

        boolean result = PropertyConfigs.getBooleanValue(key, defaultValue);
        assertEquals(false, result);
    }

    @Test
    public void testGetStringValue_WithKey_PropertySet() {
        // This test verifies that when a property is set to a non-default value, it is returned.
        String key = "test.string";
        String defaultValue = "default";
        System.setProperty(key, "customValue");

        String result = PropertyConfigs.getStringValue(key, defaultValue);
        assertEquals("customValue", result);
    }

    @Test
    public void testGetBooleanValue_WithKeys_AllMissing() {
        // This test verifies that if none of the provided keys are found in system properties or environment,
        // the default value is returned.
        List<String> keys = Arrays.asList("missing.key1", "missing.key2");
        boolean defaultValue = false;

        boolean result = PropertyConfigs.getBooleanValue(keys, defaultValue);
        assertEquals(false, result);
    }

    @Test
    public void testGetStringValue_WithKeys_AllMissing() {
        // This test verifies that if none of the provided keys are found, the default string is returned.
        List<String> keys = Arrays.asList("missing.key1", "missing.key2");
        String defaultValue = "defaultString";

        String result = PropertyConfigs.getStringValue(keys, defaultValue);
        assertEquals("defaultString", result);
    }

    @Test
    public void testGetBooleanValue_WithKeys_SecondKeyExists() {
        // This test verifies that if the first key is missing but the second key is found, its value is returned.
        List<String> keys = Arrays.asList("missing.key1", "test.boolean.defaultFalse", "missing.key3");
        boolean defaultValue = true;
        // Set the property on the second key to "false".
        System.setProperty("test.boolean.defaultFalse", "false");

        boolean result = PropertyConfigs.getBooleanValue(keys, defaultValue);
        assertEquals(false, result);
    }

    @Test
    public void testGetStringValue_WithKeys_SecondKeyExists() {
        // This test verifies that if the first key is missing but the second key is found,
        // the method returns the property value of the second key.
        List<String> keys = Arrays.asList("missing.key1", "test.string.custom", "missing.key3");
        String defaultValue = "defaultValue";
        // Set the property on the second key.
        System.setProperty("test.string.custom", "foundValue");

        String result = PropertyConfigs.getStringValue(keys, defaultValue);
        assertEquals("foundValue", result);
    }

    @Test
    public void testGetBooleanValue_WithKey_DefaultFalseWhenNotSet() {
        // This test verifies that when no property is set for a given key and the default is false,
        // the method properly returns false.
        String key = "nonexistent.boolean";
        boolean defaultValue = false;

        boolean result = PropertyConfigs.getBooleanValue(key, defaultValue);
        assertEquals(false, result);
    }

    // -------- Cleanup --------

    @AfterEach
    public void tearDown() {
        // Clear any system properties that were set during testing
        System.clearProperty("test.key1");
        System.clearProperty("test.key2");
        System.clearProperty("test.key3");
        System.clearProperty("test.key");
        System.clearProperty("test.boolean");
        System.clearProperty("test.string");
        System.clearProperty("test.boolean.defaultFalse");
        System.clearProperty("test.string.custom");
        System.clearProperty("nonexistent.boolean");
    }
}
