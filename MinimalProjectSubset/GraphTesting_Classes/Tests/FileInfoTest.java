package stirling.software.SPDF.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FileInfoTest {

    // This parameterized test covers various file size thresholds.
    // It tests that getFormattedFileSize returns the expected value for Bytes, KB, MB, and GB.
    @ParameterizedTest(name = "fileSize: {0}")
    @CsvSource({
            "0, '0 Bytes'",
            "500, '500 Bytes'",
            "1023, '1023 Bytes'",
            "1024, '1.00 KB'",
            "1536, '1.50 KB'",
            "1048576, '1.00 MB'",
            "1572864, '1.50 MB'",
            "1073741824, '1.00 GB'",
            "1610612736, '1.50 GB'"
    })
    void testGetFormattedFileSize(long fileSize, String expectedFormattedSize) {
        LocalDateTime now = LocalDateTime.now();
        FileInfo fileInfo = new FileInfo(
                "example.txt",
                "/path/to/example.txt",
                now,
                fileSize,
                now.minusDays(1)
        );
        assertEquals(expectedFormattedSize, fileInfo.getFormattedFileSize());
    }

    // This test ensures that getFilePathAsPath correctly converts the filePath string to a Path.
    @Test
    void testGetFilePathAsPath() {
        String filePath = "/some/path/file.txt";
        FileInfo fileInfo = new FileInfo(
                "file.txt",
                filePath,
                LocalDateTime.now(),
                1024,
                LocalDateTime.now()
        );
        Path expectedPath = Paths.get(filePath);
        assertEquals(expectedPath, fileInfo.getFilePathAsPath());
    }

    // This test validates that getFormattedModificationDate returns the correctly formatted string.
    @Test
    void testGetFormattedModificationDate() {
        LocalDateTime modificationDate = LocalDateTime.of(2023, 4, 7, 15, 30, 10);
        FileInfo fileInfo = new FileInfo(
                "test.txt",
                "/test/path",
                modificationDate,
                2048,
                LocalDateTime.now()
        );
        String expected = modificationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals(expected, fileInfo.getFormattedModificationDate());
    }

    // This test validates that getFormattedCreationDate returns the correctly formatted string.
    @Test
    void testGetFormattedCreationDate() {
        LocalDateTime creationDate = LocalDateTime.of(2023, 4, 6, 10, 20, 30);
        FileInfo fileInfo = new FileInfo(
                "test.txt",
                "/test/path",
                LocalDateTime.now(),
                4096,
                creationDate
        );
        String expected = creationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertEquals(expected, fileInfo.getFormattedCreationDate());
    }
}
