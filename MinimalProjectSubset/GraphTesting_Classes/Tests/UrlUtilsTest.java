package stirling.software.SPDF.utils;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

public class UrlUtilsTest {

    // Original test for getOrigin.
    @Test
    void testGetOrigin() {
        // Mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getScheme()).thenReturn("http");
        Mockito.when(request.getServerName()).thenReturn("localhost");
        Mockito.when(request.getServerPort()).thenReturn(8080);
        Mockito.when(request.getContextPath()).thenReturn("/myapp");

        // Call the method under test
        String origin = UrlUtils.getOrigin(request);

        // Assert the result
        assertEquals("http://localhost:8080/myapp", origin);
    }

    // ======= Added Tests for COE891 to increase line coverage =======

    // Test isPortAvailable for a free port.
    @Test
    void testIsPortAvailable_FreePort() {
        // Choose a port number that is likely free.
        int freePort = 54321;
        // If the port is free, isPortAvailable should return true.
        // (Note: While not guaranteed in all environments, 54321 is likely free during tests.)
        assertTrue(UrlUtils.isPortAvailable(freePort));
    }

    // Test isPortAvailable when the port is occupied.
    @Test
    void testIsPortAvailable_OccupiedPort() throws IOException {
        // Bind a ServerSocket to an ephemeral port, ensuring it is in use.
        try (ServerSocket occupiedSocket = new ServerSocket(0)) {
            int occupiedPort = occupiedSocket.getLocalPort();
            // Since the port is in use, isPortAvailable should return false.
            assertFalse(UrlUtils.isPortAvailable(occupiedPort));
        }
    }

    // Test findAvailablePort when the start port is available.
    @Test
    void testFindAvailablePort_WhenStartPortAvailable() {
        // Choose a high-numbered port that is likely free.
        int startPort = 50000;
        String availablePort = UrlUtils.findAvailablePort(startPort);
        // Since the start port is available, the same port should be returned.
        assertEquals(String.valueOf(startPort), availablePort);
    }

    // Test findAvailablePort when the start port is occupied.
    @Test
    void testFindAvailablePort_WhenStartPortOccupied() throws IOException {
        // Bind a ServerSocket on an ephemeral port to simulate an occupied port.
        try (ServerSocket occupiedSocket = new ServerSocket(0)) {
            int occupiedPort = occupiedSocket.getLocalPort();
            // Call findAvailablePort with the occupied port.
            String availablePortStr = UrlUtils.findAvailablePort(occupiedPort);
            int availablePort = Integer.parseInt(availablePortStr);
            // Verify that the method did not return the occupied port.
            assertNotEquals(occupiedPort, availablePort);
            // And check that the returned port is indeed available.
            assertTrue(UrlUtils.isPortAvailable(availablePort));
            // Ensure that the available port is greater than the occupied one.
            assertTrue(availablePort > occupiedPort);
        }
    }
}
