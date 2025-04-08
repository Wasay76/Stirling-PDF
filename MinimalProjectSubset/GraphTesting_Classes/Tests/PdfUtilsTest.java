package stirling.software.SPDF.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/*
 * Tests for COE891 Project - Edge Coverage for PdfUtils.java
 *
 * Test Requirements for Edge Coverage on hasImages():
 * TR1: Provide input so that pagesToCheck produces a page list with at least one page
 *      where hasImagesOnPage(page) returns true. (This causes immediate return of true.)
 * TR2: Provide input so that pagesToCheck produces a page list with multiple pages, and
 *      where hasImagesOnPage(page) returns false for every page. (The loop iterates through all pages.)
 * TR3: Ensure that the page list has more than one element so that the loop’s back edge is exercised.
 *
 * NOTE: Do not take credit for the classes that were already provided. The following tests build on those classes.
 */
public class PdfUtilsTest {

    // Pre-existing test for textToPageSize()
    @Test
    void testTextToPageSize() {
        assertEquals(PDRectangle.A4, PdfUtils.textToPageSize("A4"));
        assertEquals(PDRectangle.LETTER, PdfUtils.textToPageSize("LETTER"));
        assertThrows(IllegalArgumentException.class, () -> PdfUtils.textToPageSize("INVALID"));
    }

    // Pre-existing test for hasImagesOnPage() using mocked objects.
    @Test
    void testHasImagesOnPage() throws IOException {
        // Mock a PDPage and its resources
        PDPage page = Mockito.mock(PDPage.class);
        PDResources resources = Mockito.mock(PDResources.class);
        Mockito.when(page.getResources()).thenReturn(resources);

        // Case 1 (No images): satisfies default condition.
        Mockito.when(resources.getXObjectNames()).thenReturn(Collections.emptySet());
        assertFalse(PdfUtils.hasImagesOnPage(page));

        // Case 2 (Image found): Using mocked objects to simulate a page with an image.
        Set<COSName> xObjectNames = new HashSet<>();
        COSName cosName = COSName.getPDFName("Im1");
        xObjectNames.add(cosName);

        PDImageXObject imageXObject = Mockito.mock(PDImageXObject.class);
        Mockito.when(resources.getXObjectNames()).thenReturn(xObjectNames);
        Mockito.when(resources.getXObject(cosName)).thenReturn(imageXObject);

        assertTrue(PdfUtils.hasImagesOnPage(page));
    }

    /**
     * Helper method to create a blank page with no images.
     * Used in tests for TR2 and to exercise the loop back edge (TR3).
     */
    private PDPage createBlankPage() {
        PDPage page = new PDPage();
        // Assign an empty PDResources instance so that getXObjectNames() returns an empty set.
        page.setResources(new PDResources());
        return page;
    }

    /**
     * Helper method to create a page that contains one image.
     * This method creates a dummy 1x1 BufferedImage, then creates a real PDImageXObject
     * using LosslessFactory, and finally adds it to the page's resources.
     *
     * This helper is used to satisfy TR1.
     *
     * @param document the PDDocument into which the page will be added.
     * @return the PDPage containing an image.
     */
    private PDPage createPageWithImage(PDDocument document) throws IOException {
        PDPage page = new PDPage();
        PDResources resources = new PDResources();
        page.setResources(resources);
        // Create a dummy 1x1 pixel image.
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        // Create a real PDImageXObject using the document.
        PDImageXObject imageXObject = LosslessFactory.createFromImage(document, dummyImage);
        // Add the image to the page's resources.
        resources.put(COSName.getPDFName("Im1"), imageXObject);
        document.addPage(page);
        return page;
    }

    /**
     * Test for TR2 & TR3.
     * This test creates a document with two blank pages (no images).
     * TR2: Ensures that hasImages() returns false when no pages have images.
     * TR3: Uses two pages to exercise the loop's back edge (iteration over more than one page).
     */
    @Test
    void testHasImagesReturnsFalseWhenNoPagesHaveImages() throws IOException {
        try (PDDocument document = new PDDocument()) {
            // Create two blank pages with no images.
            PDPage page1 = createBlankPage();
            PDPage page2 = createBlankPage();
            document.addPage(page1);
            document.addPage(page2);

            boolean result = PdfUtils.hasImages(document, "0,1");
            assertFalse(result, "hasImages() should return false when none of the pages have images");
        }
    }

    /**
     * Test for TR1.
     * This test creates a document where the first page (index 0) contains an image.
     * When calling hasImages() with "0,1", the method should detect the image immediately
     * and return true.
     */
    @Test
    void testHasImagesReturnsTrueWhenOnePageHasImage() throws IOException {
        try (PDDocument document = new PDDocument()) {
            // Add the page with an image as the first page (index 0).
            createPageWithImage(document);
            // Add a blank page as the second page (index 1) to ensure multiple pages are checked (TR3).
            PDPage blankPage = createBlankPage();
            document.addPage(blankPage);

            // The document now has two pages: index 0 contains an image, index 1 is blank.
            boolean result = PdfUtils.hasImages(document, "0,1");
            assertTrue(result, "hasImages() should return true when at least one page has an image");
        }
    }
}
