package stirling.software.SPDF.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PropertyConfigsTest {

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

        // Call the method under test
        boolean result = PropertyConfigs.getBooleanValue(key, defaultValue);

        // Verify the result
        assertEquals(true, result);
    }

    @Test
    public void testGetStringValue_WithKey() {
        // Define key and default value
        String key = "test.key";
        String defaultValue = "default";

        // Call the method under test
        String result = PropertyConfigs.getStringValue(key, defaultValue);

        // Verify the result
        assertEquals("default", result);
    }
    
    // ================================================================
    // ISP TESTING: Additional tests using Boundary Value Analysis (BVA)
    // ================================================================

    @Test
    public void isp_testGetBooleanValue_WithEmptyKeyList() {
        // Boundary: Empty list of keys should return the default value.
        List<String> emptyKeys = Arrays.asList();
        boolean defaultValue = true;
        boolean result = PropertyConfigs.getBooleanValue(emptyKeys, defaultValue);
        assertEquals(defaultValue, result);
    }
    
    @Test
    public void isp_testGetStringValue_WithEmptyKeyList() {
        // Boundary: Empty list of keys should return the default value.
        List<String> emptyKeys = Arrays.asList();
        String defaultValue = "default";
        String result = PropertyConfigs.getStringValue(emptyKeys, defaultValue);
        assertEquals(defaultValue, result);
    }
    
    @Test
    public void isp_testGetBooleanValue_WithEmptyPropertyValue() {
        // Boundary: For a single key, setting the property to an empty string.
        // Since Boolean.valueOf("") returns false, even if the default is true.
        String key = "isp.test.emptyBoolean";
        System.setProperty(key, "");
        boolean defaultValue = true;
        boolean result = PropertyConfigs.getBooleanValue(key, defaultValue);
        assertEquals(false, result);
        System.clearProperty(key);
    }
    
    @Test
    public void isp_testGetStringValue_WithEmptyPropertyValue() {
        // Boundary: For a single key, setting the property to an empty string should return "".
        String key = "isp.test.emptyString";
        System.setProperty(key, "");
        String defaultValue = "default";
        String result = PropertyConfigs.getStringValue(key, defaultValue);
        assertEquals("", result);
        System.clearProperty(key);
    }
    
    @Test
    public void isp_testGetBooleanValue_FalseValue() {
        // Boundary: For a single key, when the property is explicitly set to "false".
        String key = "isp.test.falseBoolean";
        System.setProperty(key, "false");
        boolean defaultValue = true; // Even if default is true, property "false" should yield false.
        boolean result = PropertyConfigs.getBooleanValue(key, defaultValue);
        assertEquals(false, result);
        System.clearProperty(key);
    }
    
    @Test
    public void isp_testGetStringValue_NonEmptyValue() {
        // Boundary: For a single key, with a typical non-empty value.
        String key = "isp.test.nonEmptyString";
        System.setProperty(key, "boundaryValue");
        String defaultValue = "default";
        String result = PropertyConfigs.getStringValue(key, defaultValue);
        assertEquals("boundaryValue", result);
        System.clearProperty(key);
    }
    
    @Test
    public void isp_testGetBooleanValue_SingleKeyEmptyValue() {
        // Boundary: When using a list with one key set to an empty value.
        List<String> keys = Arrays.asList("isp.test.singleEmpty");
        System.setProperty("isp.test.singleEmpty", "");
        boolean defaultValue = false;
        boolean result = PropertyConfigs.getBooleanValue(keys, defaultValue);
        // Expect false because "" is not "true"
        assertEquals(false, result);
        System.clearProperty("isp.test.singleEmpty");
    }
    
    @Test
    public void isp_testGetStringValue_SingleKeyEmptyValue() {
        // Boundary: When using a list with one key where the property is an empty string.
        List<String> keys = Arrays.asList("isp.test.singleEmptyString");
        System.setProperty("isp.test.singleEmptyString", "");
        String defaultValue = "default";
        String result = PropertyConfigs.getStringValue(keys, defaultValue);
        // Expect empty string rather than default
        assertEquals("", result);
        System.clearProperty("isp.test.singleEmptyString");
    }
}
