package stirling.software.SPDF.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class PropSyncTest {

    @TempDir
    Path tempDir;
    
    private Path englishFile;
    private Path frenchFile;
    private Path germanFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create test files in temp directory
        englishFile = tempDir.resolve("messages_en_GB.properties");
        frenchFile = tempDir.resolve("messages_fr.properties");
        germanFile = tempDir.resolve("messages_de.properties");
        
        // Sample English content
        List<String> englishContent = Arrays.asList(
            "# English properties",
            "greeting=Hello",
            "farewell=Goodbye",
            "welcome=Welcome",
            "# More properties",
            "help=Help"
        );
        
        // Sample French content (missing 'welcome')
        List<String> frenchContent = Arrays.asList(
            "# French properties",
            "greeting=Bonjour",
            "farewell=Au revoir",
            "# More properties",
            "help=Aide"
        );
        
        // Sample German content (only has greeting)
        List<String> germanContent = Arrays.asList(
            "# German properties",
            "greeting=Hallo"
        );
        
        Files.write(englishFile, englishContent, StandardCharsets.UTF_8);
        Files.write(frenchFile, frenchContent, StandardCharsets.UTF_8);
        Files.write(germanFile, germanContent, StandardCharsets.UTF_8);
    }
    
    @Test
    void testLinesToProps() throws IOException {
        // Test the linesToProps method
        List<String> lines = Files.readAllLines(englishFile);
        Map<String, String> props = PropSync.linesToProps(lines);
        
        assertEquals(4, props.size(), "Should parse 4 properties");
        assertEquals("Hello", props.get("greeting"), "Should correctly parse greeting");
        assertEquals("Goodbye", props.get("farewell"), "Should correctly parse farewell");
        assertEquals("Welcome", props.get("welcome"), "Should correctly parse welcome");
        assertEquals("Help", props.get("help"), "Should correctly parse help");
        assertNull(props.get("# English properties"), "Should not include comments");
    }
    
    @Test
    void testSyncPropsWithLines() throws IOException {
        // Get English properties
        List<String> enLines = Files.readAllLines(englishFile);
        Map<String, String> enProps = PropSync.linesToProps(enLines);
        
        // Get French properties
        List<String> frLines = Files.readAllLines(frenchFile);
        Map<String, String> frProps = PropSync.linesToProps(frLines);
        
        // Sync and check result
        List<String> syncedLines = PropSync.syncPropsWithLines(enProps, frProps, enLines);
        
        // Check that existing translations are maintained
        assertTrue(syncedLines.contains("greeting=Bonjour"), "Should keep existing translation");
        assertTrue(syncedLines.contains("farewell=Au revoir"), "Should keep existing translation");
        assertTrue(syncedLines.contains("help=Aide"), "Should keep existing translation");
        
        // Check that missing translations are added with TODO markers
        assertTrue(syncedLines.contains("##########################"), "Should include TODO marker");
        assertTrue(syncedLines.contains("###  TODO: Translate   ###"), "Should include TODO marker");
        assertTrue(syncedLines.contains("welcome=Welcome"), "Should include untranslated property");
        
        // Check that comments are preserved
        assertTrue(syncedLines.contains("# English properties") || syncedLines.contains("# French properties"), 
                "Should preserve comments");
    }
    
    @Test
    void testEndToEndSync() throws IOException {
        // Make the PropSync methods accessible for testing
        // First, make a modified version of main for testing
        PropSync.processFiles(tempDir.toFile(), englishFile.getFileName().toString());
        
        // Verify that French file was updated correctly
        List<String> updatedFrLines = Files.readAllLines(frenchFile);
        String frContent = String.join("\n", updatedFrLines);
        
        assertTrue(frContent.contains("greeting=Bonjour"), "Should keep existing French translation");
        assertTrue(frContent.contains("welcome=Welcome"), "Should add missing English property");
        assertTrue(frContent.contains("TODO: Translate"), "Should mark untranslated sections");
        
        // Verify that German file was updated correctly
        List<String> updatedDeLines = Files.readAllLines(germanFile);
        String deContent = String.join("\n", updatedDeLines);
        
        assertTrue(deContent.contains("greeting=Hallo"), "Should keep existing German translation");
        assertTrue(deContent.contains("farewell=Goodbye"), "Should add missing English property");
        assertTrue(deContent.contains("welcome=Welcome"), "Should add missing English property");
        assertTrue(deContent.contains("help=Help"), "Should add missing English property");
        assertTrue(deContent.contains("TODO: Translate"), "Should mark untranslated sections");
    }
}