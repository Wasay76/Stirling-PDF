package stirling.software.SPDF.utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import stirling.software.SPDF.model.api.converters.HTMLToPdfRequest;

public class FileToPdfTest {

    /**
     * This test was added for the COE891 final project
     */
    @Test
    public void testSanitizeZipFilename_AllDefAllUse() {
        // TR1: Null or empty input should return "" immediately (usage at node 1 only).
        assertEquals("", FileToPdf.sanitizeZipFilename(null),
            "TR1: Null entry name should return an empty string");
        assertEquals("", FileToPdf.sanitizeZipFilename("   "),
            "TR1: Empty (whitespace) entry name should return an empty string");

        // TR2: Non-empty string without any path-traversal sequences.
        // The while loop is not executed and only the initial replaceAll calls (definition node 3) are used.
        String inputTR2 = "folder/file.txt";
        String expectedTR2 = "folder/file.txt";
        assertEquals(expectedTR2, FileToPdf.sanitizeZipFilename(inputTR2),
            "TR2: Safe file name should remain unchanged");

        // TR3: A string containing a path-traversal substring (e.g., "../secret").
        // This causes the loop to execute at least once, triggering a redefinition at node 5.
        String inputTR3 = "../secret";
        String expectedTR3 = "secret";
        assertEquals(expectedTR3, FileToPdf.sanitizeZipFilename(inputTR3),
            "TR3: '../' should be removed leaving the safe string");

        // TR4: A string with multiple path-traversal sequences.
        // Example: "../../data/../config" should repeatedly redefine entryName in the loop.
        String inputTR4 = "../../data/../config";
        String expectedTR4 = "data/config";
        assertEquals(expectedTR4, FileToPdf.sanitizeZipFilename(inputTR4),
            "TR4: Multiple '../' sequences should be removed, leaving the expected path");

        // TR5: A string that exercises the final definition at node 6.
        // Example: "C:\\folder\\..\\doc.txt" should have its drive letter and "..\\" removed
        // and then backslashes normalized to forward slashes.
        String inputTR5 = "C:\\folder\\..\\doc.txt";
        String expectedTR5 = "folder/doc.txt";
        assertEquals(expectedTR5, FileToPdf.sanitizeZipFilename(inputTR5),
            "TR5: Should remove drive letters, remove path traversal, and normalize backslashes");
    }

    @Test
    public void testConvertHtmlToPdf() {
        HTMLToPdfRequest request = new HTMLToPdfRequest();
        byte[] fileBytes = new byte[0]; // Sample file bytes (empty input)
        String fileName = "test.html"; // Sample file name indicating an HTML file
        boolean disableSanitize = false; // Flag to control sanitization

        // Expect an IOException to be thrown due to empty input
        Throwable thrown =
                assertThrows(
                        IOException.class,
                        () ->
                                FileToPdf.convertHtmlToPdf(
                                        "/path/", request, fileBytes, fileName, disableSanitize));
        assertNotNull(thrown);
    }

    /**
     * Test sanitizeZipFilename with null or empty input.
     * It should return an empty string in these cases.
     */
    @Test
    public void testSanitizeZipFilename_NullOrEmpty() {
        assertEquals("", FileToPdf.sanitizeZipFilename(null));
        assertEquals("", FileToPdf.sanitizeZipFilename("   "));
    }

    /**
     * Test sanitizeZipFilename to ensure it removes path traversal sequences.
     * This includes removing both forward and backward slash sequences.
     */
    @Test
    public void testSanitizeZipFilename_RemovesTraversalSequences() {
        String input = "../some/../path/..\\to\\file.txt";
        String expected = "some/path/to/file.txt";

        // Print output for debugging purposes
        System.out.println("sanitizeZipFilename " + FileToPdf.sanitizeZipFilename(input));
        System.out.flush();

        // Expect that the method replaces backslashes with forward slashes
        // and removes path traversal sequences
        assertEquals(expected, FileToPdf.sanitizeZipFilename(input));
    }

    /**
     * Test sanitizeZipFilename to ensure that it removes leading drive letters and slashes.
     */
    @Test
    public void testSanitizeZipFilename_RemovesLeadingDriveAndSlashes() {
        String input = "C:\\folder\\file.txt";
        String expected = "folder/file.txt";
        assertEquals(expected, FileToPdf.sanitizeZipFilename(input));

        input = "/folder/file.txt";
        expected = "folder/file.txt";
        assertEquals(expected, FileToPdf.sanitizeZipFilename(input));
    }

    /**
     * Test sanitizeZipFilename to verify that safe filenames remain unchanged.
     */
    @Test
    public void testSanitizeZipFilename_NoChangeForSafeNames() {
        String input = "folder/subfolder/file.txt";
        assertEquals(input, FileToPdf.sanitizeZipFilename(input));
    }
}
