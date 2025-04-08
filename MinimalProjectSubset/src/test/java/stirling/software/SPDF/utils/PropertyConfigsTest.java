package stirling.software.SPDF.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PropertyConfigsTest {
    private static final String PROP_KEY = "TEST_PROP_KEY";
    private static final String ENV_KEY = "TEST_ENV_KEY"; // Used to simulate env behavior

    @BeforeAll
    public static void clearProps() {
        System.clearProperty(PROP_KEY);
        System.clearProperty(ENV_KEY);
        System.clearProperty("key1");
        System.clearProperty("key2");
        System.clearProperty("strKey1");
        System.clearProperty("strKey2");
        System.clearProperty("missing");
        System.clearProperty("missing1");
        System.clearProperty("missing2");
    }

    // --- getBooleanValue(String key, boolean defaultValue) ---

    @Test
    public void testBooleanValue_propertySet() {
        System.setProperty(PROP_KEY, "true");
        boolean result = PropertyConfigs.getBooleanValue(PROP_KEY, false);
        assertTrue(result); // Path 1: property present
    }

    @Test
    public void testBooleanValue_envSet_simulated() {
        // Simulating environment variable using property for test purposes
        System.setProperty(ENV_KEY, "false");
        boolean result = PropertyConfigs.getBooleanValue(ENV_KEY, true);
        assertFalse(result); // Path 2: fallback to "env"
    }

    @Test
    public void testBooleanValue_noneSet() {
        boolean result = PropertyConfigs.getBooleanValue("NON_EXISTENT_KEY", true);
        assertTrue(result); // Path 3: fallback to default
    }

    // --- getStringValue(String key, String defaultValue) ---

    @Test
    public void testStringValue_propertySet() {
        System.setProperty(PROP_KEY, "value1");
        String result = PropertyConfigs.getStringValue(PROP_KEY, "default");
        assertEquals("value1", result); // Path 1
    }

    @Test
    public void testStringValue_envSet_simulated() {
        System.setProperty(ENV_KEY, "envValue");
        String result = PropertyConfigs.getStringValue(ENV_KEY, "default");
        assertEquals("envValue", result); // Path 2
    }

    @Test
    public void testStringValue_noneSet() {
        String result = PropertyConfigs.getStringValue("NON_EXISTENT_KEY", "default");
        assertEquals("default", result); // Path 3
    }

    // --- getBooleanValue(List<String> keys, boolean defaultValue) ---

    @Test
    public void testBooleanValueList_firstPropertyMatch() {
        System.setProperty("key1", "true");
        List<String> keys = Arrays.asList("key1", "key2");
        boolean result = PropertyConfigs.getBooleanValue(keys, false);
        assertTrue(result); // Path 1: first key match
    }

    @Test
    public void testBooleanValueList_secondEnvMatch_simulated() {
        List<String> keys = Arrays.asList("missing1", ENV_KEY);
        System.setProperty(ENV_KEY, "true"); // simulate env
        boolean result = PropertyConfigs.getBooleanValue(keys, false);
        assertTrue(result); // Path 2/3: second key match
    }

    @Test
    public void testBooleanValueList_noneFound() {
        List<String> keys = Arrays.asList("missing1", "missing2");
        boolean result = PropertyConfigs.getBooleanValue(keys, false);
        assertFalse(result); // Path 4: default returned
    }

    // --- getStringValue(List<String> keys, String defaultValue) ---

    @Test
    public void testStringValueList_firstMatch() {
        System.setProperty("strKey1", "value1");
        List<String> keys = Arrays.asList("strKey1", "strKey2");
        String result = PropertyConfigs.getStringValue(keys, "default");
        assertEquals("value1", result); // Path 1
    }

    @Test
    public void testStringValueList_laterMatch_simulatedEnv() {
        System.setProperty(ENV_KEY, "envVal");
        List<String> keys = Arrays.asList("missing", ENV_KEY);
        String result = PropertyConfigs.getStringValue(keys, "default");
        assertEquals("envVal", result); // Path 2/3
    }

    @Test
    public void testStringValueList_noneMatch() {
        List<String> keys = Arrays.asList("missing1", "missing2");
        String result = PropertyConfigs.getStringValue(keys, "default");
        assertEquals("default", result); // Path 4
    }

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
        // Boundary: For a single key, setting the property to an empty string should
        // return "".
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
        // Boundary: When using a list with one key where the property is an empty
        // string.
        List<String> keys = Arrays.asList("isp.test.singleEmptyString");
        System.setProperty("isp.test.singleEmptyString", "");
        String defaultValue = "default";
        String result = PropertyConfigs.getStringValue(keys, defaultValue);
        // Expect empty string rather than default
        assertEquals("", result);
        System.clearProperty("isp.test.singleEmptyString");
    }
}
