package stirling.software.SPDF.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public class ErrorUtilsTest {

    @Test
    public void testExceptionToModel() {
        // Create a mock Model
        Model model = new org.springframework.ui.ExtendedModelMap();

        // Create a test exception
        Exception ex = new Exception("Test Exception");

        // Call the method under test
        Model resultModel = ErrorUtils.exceptionToModel(model, ex);

        // Verify the result
        assertNotNull(resultModel);
        assertEquals("Test Exception", resultModel.getAttribute("errorMessage"));
        assertNotNull(resultModel.getAttribute("stackTrace"));
    }

    @Test
    public void testExceptionToModelView() {
        // Create a mock Model
        Model model = new org.springframework.ui.ExtendedModelMap();

        // Create a test exception
        Exception ex = new Exception("Test Exception");

        // Call the method under test
        ModelAndView modelAndView = ErrorUtils.exceptionToModelView(model, ex);

        // Verify the result
        assertNotNull(modelAndView);
        assertEquals("Test Exception", modelAndView.getModel().get("errorMessage"));
        assertNotNull(modelAndView.getModel().get("stackTrace"));
    }

    // NEWLY ADDED TESTS FOR COE891:
    @Test
    public void testExceptionToModel2() {
        // Setup
        Model model = new ConcurrentModel();
        Exception testException = new RuntimeException("Test error message");
        
        // Exercise
        Model resultModel = ErrorUtils.exceptionToModel(model, testException);
        
        // Verify
        assertNotNull(resultModel, "Result model should not be null");
        assertEquals("Test error message", resultModel.getAttribute("errorMessage"), 
                    "Error message should match exception message");
        assertNotNull(resultModel.getAttribute("stackTrace"), 
                    "Stack trace should not be null");
        assertTrue(((String)resultModel.getAttribute("stackTrace")).contains("RuntimeException"), 
                    "Stack trace should contain exception type");
        
        // Test that it's the same model instance (to cover NULL_RETURNS mutation)
        assertSame(model, resultModel, "Should return the same model instance");
    }
    
    @Test
    public void testExceptionToModel_NullMessage() {
        // Setup - exception with null message
        Model model = new ConcurrentModel();
        Exception testException = new NullPointerException();
        
        // Exercise
        Model resultModel = ErrorUtils.exceptionToModel(model, testException);
        
        // Verify
        assertNull(resultModel.getAttribute("errorMessage"), 
                "Error message should be null when exception has no message");
        assertNotNull(resultModel.getAttribute("stackTrace"), 
                "Stack trace should not be null");
    }
    
    @Test
    public void testExceptionToModelView2() {
        // Setup
        Model model = new ConcurrentModel();
        Exception testException = new RuntimeException("Test error message");
        
        // Exercise
        ModelAndView resultModelAndView = ErrorUtils.exceptionToModelView(model, testException);
        
        // Verify
        assertNotNull(resultModelAndView, "Result ModelAndView should not be null");
        assertEquals("Test error message", resultModelAndView.getModel().get("errorMessage"), 
                    "Error message should match exception message");
        assertNotNull(resultModelAndView.getModel().get("stackTrace"), 
                    "Stack trace should not be null");
        assertTrue(((String)resultModelAndView.getModel().get("stackTrace")).contains("RuntimeException"), 
                    "Stack trace should contain exception type");
    }
    
    @Test
    public void testExceptionToModelView_NullMessage() {
        // Setup - exception with null message
        Model model = new ConcurrentModel();
        Exception testException = new NullPointerException();
        
        // Exercise
        ModelAndView resultModelAndView = ErrorUtils.exceptionToModelView(model, testException);
        
        // Verify
        assertNull(resultModelAndView.getModel().get("errorMessage"), 
                "Error message should be null when exception has no message");
        assertNotNull(resultModelAndView.getModel().get("stackTrace"), 
                "Stack trace should not be null");
    }
    
    @Test
    public void testStackTraceGeneration() {
        // This test specifically targets the StringWriter and PrintWriter functionality
        // to ensure mutations in those areas are caught
        
        // Setup
        Exception testException = new RuntimeException("Test message");
        StringWriter sw = new StringWriter();
        testException.printStackTrace(new PrintWriter(sw));
        String expectedStackTrace = sw.toString();
        
        // Exercise via exceptionToModel
        Model model = new ConcurrentModel();
        ErrorUtils.exceptionToModel(model, testException);
        String actualStackTrace1 = (String) model.getAttribute("stackTrace");
        
        // Exercise via exceptionToModelView
        ModelAndView modelAndView = ErrorUtils.exceptionToModelView(model, testException);
        String actualStackTrace2 = (String) modelAndView.getModel().get("stackTrace");
        
        // Verify
        assertEquals(expectedStackTrace, actualStackTrace1, 
                "Stack trace in model should match expected");
        assertEquals(expectedStackTrace, actualStackTrace2, 
                "Stack trace in modelAndView should match expected");
    }
    
    // ================================================================
    // ISP TESTING: Additional tests using Boundary Value Analysis (BVA)
    // ================================================================
    
    @Test
    public void isp_testExceptionToModel_EmptyMessage() {
        // Boundary: Exception with an empty string as message
        Model model = new ConcurrentModel();
        Exception ex = new Exception("");
        Model resultModel = ErrorUtils.exceptionToModel(model, ex);
        assertNotNull(resultModel);
        assertEquals("", resultModel.getAttribute("errorMessage"), "Empty message should return empty string");
        assertNotNull(resultModel.getAttribute("stackTrace"));
    }
    
    @Test
    public void isp_testExceptionToModel_OneCharMessage() {
        // Boundary: Exception with a one-character message
        Model model = new ConcurrentModel();
        Exception ex = new Exception("A");
        Model resultModel = ErrorUtils.exceptionToModel(model, ex);
        assertNotNull(resultModel);
        assertEquals("A", resultModel.getAttribute("errorMessage"), "One-char message should be returned correctly");
        assertNotNull(resultModel.getAttribute("stackTrace"));
        assertTrue(((String) resultModel.getAttribute("stackTrace")).contains("A"), "Stack trace should contain the one-char message");
    }
    
    @Test
    public void isp_testExceptionToModel_LongMessage() {
        // Boundary: Exception with an extremely long message
        Model model = new ConcurrentModel();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("X");
        }
        String longMessage = sb.toString();
        Exception ex = new Exception(longMessage);
        Model resultModel = ErrorUtils.exceptionToModel(model, ex);
        assertNotNull(resultModel);
        assertEquals(longMessage, resultModel.getAttribute("errorMessage"), "Long message should be returned fully");
        assertNotNull(resultModel.getAttribute("stackTrace"));
    }
    
    @Test
    public void isp_testExceptionToModelView_EmptyMessage() {
        // Boundary: Using ModelAndView with an exception that has an empty message
        Model model = new ConcurrentModel();
        Exception ex = new Exception("");
        ModelAndView resultModelAndView = ErrorUtils.exceptionToModelView(model, ex);
        assertNotNull(resultModelAndView);
        assertEquals("", resultModelAndView.getModel().get("errorMessage"), "Empty message should return empty string");
        assertNotNull(resultModelAndView.getModel().get("stackTrace"));
    }
    
    @Test
    public void isp_testExceptionToModelView_OneCharMessage() {
        // Boundary: Using ModelAndView with an exception that has a one-character message
        Model model = new ConcurrentModel();
        Exception ex = new Exception("B");
        ModelAndView resultModelAndView = ErrorUtils.exceptionToModelView(model, ex);
        assertNotNull(resultModelAndView);
        assertEquals("B", resultModelAndView.getModel().get("errorMessage"), "One-char message should be returned correctly");
        assertNotNull(resultModelAndView.getModel().get("stackTrace"));
        assertTrue(((String) resultModelAndView.getModel().get("stackTrace")).contains("B"), "Stack trace should contain the one-char message");
    }
    
    @Test
    public void isp_testExceptionToModelView_LongMessage() {
        // Boundary: Using ModelAndView with an exception that has an extremely long message
        Model model = new ConcurrentModel();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Y");
        }
        String longMessage = sb.toString();
        Exception ex = new Exception(longMessage);
        ModelAndView resultModelAndView = ErrorUtils.exceptionToModelView(model, ex);
        assertNotNull(resultModelAndView);
        assertEquals(longMessage, resultModelAndView.getModel().get("errorMessage"), "Long message should be returned fully");
        assertNotNull(resultModelAndView.getModel().get("stackTrace"));
    }
}
