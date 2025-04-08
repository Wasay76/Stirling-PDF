package stirling.software.SPDF.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RequestUriUtilsTest {

    @Test
    public void testIsStaticResource() {
        assertTrue(RequestUriUtils.isStaticResource("/css/styles.css"));
        assertTrue(RequestUriUtils.isStaticResource("/js/script.js"));
        assertTrue(RequestUriUtils.isStaticResource("/images/logo.png"));
        assertTrue(RequestUriUtils.isStaticResource("/public/index.html"));
        assertTrue(RequestUriUtils.isStaticResource("/pdfjs/pdf.worker.js"));
        assertTrue(RequestUriUtils.isStaticResource("/api/v1/info/status"));
        assertTrue(RequestUriUtils.isStaticResource("/some-path/icon.svg"));
        assertFalse(RequestUriUtils.isStaticResource("/api/v1/users"));
        assertFalse(RequestUriUtils.isStaticResource("/api/v1/orders"));
        assertFalse(RequestUriUtils.isStaticResource("/"));
        assertTrue(RequestUriUtils.isStaticResource("/login"));
        assertFalse(RequestUriUtils.isStaticResource("/register"));
        assertFalse(RequestUriUtils.isStaticResource("/api/v1/products"));
    }
    
    // ================================================================
    // ISP TESTING: Additional tests using Boundary Value Analysis (BVA)
    // ================================================================
    
    // ----- ISP Tests for isStaticResource(String contextPath, String requestURI) -----
    
    @Test
    public void isp_testIsStaticResource_WithNonEmptyContext_ExactMatch() {
        // Using a non-empty context path with a matching static resource prefix.
        String contextPath = "app";
        String requestURI = "app/css/style.css"; 
        // Expected: since requestURI starts with "app/css/", this should be true.
        assertTrue(RequestUriUtils.isStaticResource(contextPath, requestURI), 
                   "Should recognize static resource with contextPath and /css/ prefix");
    }
    
    @Test
    public void isp_testIsStaticResource_WithNonEmptyContext_NearMiss() {
        // Near-boundary case: similar to the above but one character off.
        String contextPath = "app";
        String requestURI = "app/cs/style.css"; 
        // "app/cs/" does not match the required "app/css/" so it should return false.
        assertFalse(RequestUriUtils.isStaticResource(contextPath, requestURI),
                    "Should not recognize resource when prefix slightly mismatches");
    }
    
    @Test
    public void isp_testIsStaticResource_RobotsTxt_EmptyContext() {
        // Boundary: With empty context, the method checks if requestURI ends with "robots.txt"
        String contextPath = "";
        String requestURI = "robots.txt";
        assertTrue(RequestUriUtils.isStaticResource(contextPath, requestURI),
                   "With empty context, 'robots.txt' should be recognized as a static resource");
    }
    
    @Test
    public void isp_testIsStaticResource_RobotsTxt_NonEmptyContext() {
        // Boundary: With non-empty context, the method expects the URI to end with contextPath + "robots.txt"
        String contextPath = "app";
        // For contextPath "app", the check becomes requestURI.endsWith("approbots.txt")
        String requestURI = "foo/approbots.txt";
        assertTrue(RequestUriUtils.isStaticResource(contextPath, requestURI),
                   "With non-empty context, URI ending with contextPath + 'robots.txt' should be recognized");
    }
    
    // ----- ISP Tests for isTrackableResource(String contextPath, String requestURI) -----
    
    @Test
    public void isp_testIsTrackableResource_BoundaryJustBelowStaticThreshold() {
        // Boundary: For isTrackableResource, URIs starting with "/js" are NOT trackable.
        // Test one character shorter than "/js" should be trackable.
        String requestURI = "/j"; // one character less than "/js"
        assertTrue(RequestUriUtils.isTrackableResource("", requestURI),
                   "Request URI '/j' should be trackable as it does not meet the '/js' exclusion condition");
    }
    
    @Test
    public void isp_testIsTrackableResource_JustAboveExcluded() {
        // Adjusted near-boundary test: "/jsp" is excluded because it starts with "/js".
        // Instead, we use "/jz", which does not match "/js".
        String requestURI = "/jz";
        assertTrue(RequestUriUtils.isTrackableResource("", requestURI),
                   "Request URI '/jz' should be trackable since it does not match the excluded '/js' prefix");
    }
    
    @Test
    public void isp_testIsTrackableResource_EndsWithBoundary() {
        // For file extensions exclusions, the code checks if the URI starts with "/images"
        // and also checks for endings like ".png". Since any URI starting with "/images" will be excluded,
        // we choose one that is similar but not triggering that startsWith rule.
        String requestURIExcluded = "/images/xyz.png";
        // Expected: excluded, so should be false.
        assertFalse(RequestUriUtils.isTrackableResource("", requestURIExcluded),
                    "Request URI starting with '/images' and ending in '.png' should not be trackable");
        
        // Choosing a URI that is similar but does not start with "/images"
        String requestURINotExcluded = "/imagez/xyz.pnga";
        // Expected: trackable because it doesn't start with the excluded prefix
        // and does not exactly end with ".png".
        assertTrue(RequestUriUtils.isTrackableResource("", requestURINotExcluded),
                   "Request URI '/imagez/xyz.pnga' should be trackable as it does not match exclusion criteria");
    }
    
    @Test
    public void isp_testIsTrackableResource_ContainsSwaggerBoundary() {
        // Test for the "swagger" exclusion rule.
        String requestURI = "/api/swaggerui/index.html";
        // Contains "swagger" and should be excluded.
        assertFalse(RequestUriUtils.isTrackableResource("", requestURI),
                    "Request URI containing 'swagger' should not be trackable");
        
        // Now test a similar URI that does not contain the full string "swagger"
        String requestURI2 = "/api/swagui/index.html";
        assertTrue(RequestUriUtils.isTrackableResource("", requestURI2),
                   "Request URI not containing 'swagger' should be trackable");
    }
}
