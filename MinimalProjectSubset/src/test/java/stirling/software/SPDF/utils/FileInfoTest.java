package stirling.software.SPDF.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

public class FileInfoTest {

    @ParameterizedTest(name = "{index}: fileSize={0}")
    @CsvSource({
        "0, '0 Bytes'",
        "1023, '1023 Bytes'",
        "1024, '1.00 KB'",
        "1048575, '1024.00 KB'", // Do we really want this as result?
        "1048576, '1.00 MB'",
        "1073741823, '1024.00 MB'", // Do we really want this as result?
        "1073741824, '1.00 GB'"
    })
    void testGetFormattedFileSize(long fileSize, String expectedFormattedSize) {
        FileInfo fileInfo =
                new FileInfo(
                        "example.txt",
                        "/path/to/example.txt",
                        LocalDateTime.now(),
                        fileSize,
                        LocalDateTime.now().minusDays(1));

        assertEquals(expectedFormattedSize, fileInfo.getFormattedFileSize());
    }
    
    // ================================================================
    // ISP TESTING: Additional tests using Boundary Value Analysis (BVA)
    // ================================================================
    
    @Test
    public void isp_testGetFilePathAsPath_EmptyFilePath() {
        // Boundary: Test with an empty file path.
        FileInfo fileInfo = new FileInfo("test.txt", "", LocalDateTime.now(), 100, LocalDateTime.now().minusDays(1));
        // Expect that an empty filePath returns an empty string from Paths.get("")
        assertEquals("", fileInfo.getFilePathAsPath().toString(), "Empty filePath should result in an empty Path string");
    }
    
    @Test
    public void isp_testGetFormattedModificationDate_Boundary() {
        // Boundary: Use a known date value (Unix epoch) to test the formatting method.
        LocalDateTime epoch = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        FileInfo fileInfo = new FileInfo("test.txt", "/path/to/test.txt", epoch, 500, LocalDateTime.now().minusDays(1));
        assertEquals("1970-01-01 00:00:00", fileInfo.getFormattedModificationDate(), "Modification date should format to '1970-01-01 00:00:00'");
    }
    
    @Test
    public void isp_testGetFormattedCreationDate_Boundary() {
        // Boundary: Use a high boundary date value for creation date.
        LocalDateTime highBoundary = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        FileInfo fileInfo = new FileInfo("test.txt", "/path/to/test.txt", LocalDateTime.now(), 500, highBoundary);
        assertEquals("9999-12-31 23:59:59", fileInfo.getFormattedCreationDate(), "Creation date should format to '9999-12-31 23:59:59'");
    }
}
