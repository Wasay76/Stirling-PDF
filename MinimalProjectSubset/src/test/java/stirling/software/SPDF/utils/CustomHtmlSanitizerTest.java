package stirling.software.SPDF.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CustomHtmlSanitizerTest {

    @Test
    void sanitize_WithNullInput_ReturnsEmptyString() {
        // This test catches NULL_RETURNS mutations
        String result = CustomHtmlSanitizer.sanitize(null);
        assertNotNull(result, "Sanitized result should not be null");
        assertEquals("", result, "Null input should return empty string");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void sanitize_WithEmptyOrBlankInput_ReturnsEmptyOrBlankString(String input) {
        // This test catches EMPTY_RETURNS and NULL_RETURNS mutations
        String result = CustomHtmlSanitizer.sanitize(input);
        assertNotNull(result, "Sanitized result should not be null");
        assertTrue(result.trim().isEmpty(), "Empty or blank input should return empty or blank string");
    }

    @Test
    void sanitize_WithSimpleText_ReturnsSameText() {
        // This test catches VOID_METHOD_CALLS mutations in sanitize
        String input = "This is simple text";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertEquals(input, result, "Simple text should remain unchanged");
    }

    @Test
    void sanitize_WithAllowedHtml_KeepsAllowedElements() {
        // This test catches TRUE_RETURNS and FALSE_RETURNS mutations
        String input = "<p>Paragraph <b>bold</b> <i>italic</i> <a href='https://example.com'>link</a></p>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("<p>"), "Should keep paragraph tag");
        assertTrue(result.contains("<b>"), "Should keep bold tag");
        assertTrue(result.contains("<i>"), "Should keep italic tag");
        assertTrue(result.contains("<a href="), "Should keep link tag");
    }

    @Test
    void sanitize_WithTableElements_KeepsTableElements() {
        // This test catches VOID_METHOD_CALLS for the TABLES sanitizer
        String input = "<table><tr><th>Header</th></tr><tr><td>Data</td></tr></table>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("<table>"), "Should keep table tag");
        assertTrue(result.contains("<tr>"), "Should keep table row tag");
        assertTrue(result.contains("<th>"), "Should keep table header tag");
        assertTrue(result.contains("<td>"), "Should keep table data tag");
    }

    @Test
    void sanitize_WithImageTag_KeepsImageTag() {
        // This test catches VOID_METHOD_CALLS for the IMAGES sanitizer
        String input = "<img src='image.jpg' alt='Test Image'>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("<img"), "Should keep image tag");
        assertTrue(result.contains("src="), "Should keep src attribute");
        assertTrue(result.contains("alt="), "Should keep alt attribute");
    }

    @Test
    void sanitize_WithStyleAttributes_KeepsStyleAttributes() {
        // This test catches VOID_METHOD_CALLS for the STYLES sanitizer
        String input = "<p style='color: red; font-size: 16px;'>Styled text</p>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("style="), "Should keep style attribute");
        assertTrue(result.contains("color"), "Should keep color style");
        assertTrue(result.contains("font-size"), "Should keep font-size style");
    }

    @Test
    void sanitize_WithDisallowedElements_RemovesDisallowedElements() {
        // This test catches NEGATE_CONDITIONALS mutations in the policy builder
        String input = "<div>Safe <script>alert('xss')</script> <noscript>NoScript</noscript></div>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("<div>"), "Should keep div tag");
        assertTrue(result.contains("Safe"), "Should keep safe text");
        assertFalse(result.contains("<script>"), "Should remove script tag");
        assertFalse(result.contains("<noscript>"), "Should remove noscript tag");
        assertFalse(result.contains("alert"), "Should remove script content");
        assertFalse(result.contains("NoScript"), "Should remove noscript content");
    }

    @Test
    void sanitize_WithMaliciousInput_SanitizesCorrectly() {
        // This test catches multiple mutations by verifying complex sanitization
        String input = "<div onclick='evil()'>Text</div><img src='javascript:alert(1)'><iframe src='evil.html'></iframe>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("<div>"), "Should keep div tag");
        assertTrue(result.contains("Text"), "Should keep div content");
        assertFalse(result.contains("onclick"), "Should remove event handlers");
        assertFalse(result.contains("javascript:"), "Should remove javascript URLs");
        assertFalse(result.contains("<iframe"), "Should remove iframe tag");
    }

    @Test
    void sanitize_WithComplexNestedStructure_PreservesStructure() {
        // This test catches mutations related to complex processing
        String input = "<div><p><strong>Title</strong></p><ul><li>Item 1</li><li>Item <em>2</em></li></ul></div>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        assertTrue(result.contains("<div>"), "Should keep div tag");
        assertTrue(result.contains("<p>"), "Should keep paragraph tag");
        assertTrue(result.contains("<strong>"), "Should keep strong tag");
        assertTrue(result.contains("<ul>"), "Should keep unordered list tag");
        assertTrue(result.contains("<li>"), "Should keep list item tag");
        assertTrue(result.contains("<em>"), "Should keep emphasis tag");
    }

    @Test
    void sanitize_IdempotencyTest_MultipleSanitizationsYieldSameResult() {
        // This test catches mutations by ensuring the sanitizer is idempotent
        String input = "<p>Text <b>bold</b> <script>alert(1)</script></p>";
        String firstPass = CustomHtmlSanitizer.sanitize(input);
        String secondPass = CustomHtmlSanitizer.sanitize(firstPass);
        
        assertEquals(firstPass, secondPass, "Multiple sanitizations should yield the same result");
    }

    @Test
    void sanitize_ReturnValueVerification_ReturnsActualSanitizedValue() {
        // This test specifically targets PRIMITIVE_RETURNS mutations
        String input = "<p>Safe</p><script>unsafe</script>";
        String expected = "<p>Safe</p>";
        String result = CustomHtmlSanitizer.sanitize(input);
        
        // The exact output format might vary slightly between OWASP HTML Sanitizer versions,
        // so we're checking content presence rather than exact equality
        assertTrue(result.contains("<p>Safe</p>"), "Should contain sanitized content");
        assertFalse(result.contains("<script>"), "Should not contain script tag");
    }
}