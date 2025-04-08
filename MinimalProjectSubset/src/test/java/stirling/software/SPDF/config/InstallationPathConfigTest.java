package stirling.software.SPDF.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.junit.jupiter.api.Test;

public class InstallationPathConfigTest {

    // ================================================================
    // ISP TESTING: Additional tests using Boundary Value Analysis (BVA)
    // ================================================================
    
    @Test
    public void isp_testGetLogPath_DefaultConfiguration() {
        // With no STIRLING_PDF_DESKTOP_UI property set,
        // the default BASE_PATH is: "." + File.separator.
        // Therefore, LOG_PATH is computed as BASE_PATH + "logs" + File.separator.
        String expected = "." + File.separator + "logs" + File.separator;
        assertEquals(expected, InstallationPathConfig.getLogPath(),
                "Default log path should match expected boundary value when using default BASE_PATH.");
    }
    
    @Test
    public void isp_testGetSettingsPath_DefaultConfiguration() {
        // With the default configuration:
        // BASE_PATH = "." + File.separator;
        // CONFIG_PATH = BASE_PATH + "configs" + File.separator, so expected CONFIG_PATH is:
        // "." + File.separator + "configs" + File.separator.
        // Then, SETTINGS_PATH = CONFIG_PATH + "settings.yml".
        String expected = "." + File.separator + "configs" + File.separator + "settings.yml";
        assertEquals(expected, InstallationPathConfig.getSettingsPath(),
                "Default settings path should match expected boundary value when using default BASE_PATH.");
    }
}
