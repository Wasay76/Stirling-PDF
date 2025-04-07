package stirling.software.SPDF.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttemptCounterTest {

    private AttemptCounter counter;

    @BeforeEach
    void setUp() {
        counter = new AttemptCounter();
    }

    @Test
    void constructor_SetsInitialValues() {
        // Test for NULL_RETURNS and PRIMITIVE_RETURNS mutations
        assertEquals(0, counter.getAttemptCount(), "Initial attempt count should be 0");
        assertTrue(counter.getLastAttemptTime() > 0, "Initial last attempt time should be positive");
        assertTrue(counter.getLastAttemptTime() <= System.currentTimeMillis(), 
                "Initial last attempt time should not be in the future");
    }

    @Test
    void increment_IncrementsCountAndUpdatesTime() throws InterruptedException {
        // Get the initial time
        long initialTime = counter.getLastAttemptTime();
        
        // Small delay to ensure time change is detectable
        Thread.sleep(5);
        
        // Test for INCREMENTS and VOID_METHOD_CALLS mutations
        counter.increment();
        assertEquals(1, counter.getAttemptCount(), "Attempt count should be incremented to 1");
        assertTrue(counter.getLastAttemptTime() > initialTime, 
                "Last attempt time should be updated to a later time");
        
        // Increment again and verify
        Thread.sleep(5);
        counter.increment();
        assertEquals(2, counter.getAttemptCount(), "Attempt count should be incremented to 2");
    }

    @Test
    void getAttemptCount_ReturnsCurrentCount() {
        // Test for PRIMITIVE_RETURNS mutations
        assertEquals(0, counter.getAttemptCount(), "Should return initial count of 0");
        
        counter.increment();
        assertEquals(1, counter.getAttemptCount(), "Should return incremented count of 1");
        
        counter.increment();
        assertEquals(2, counter.getAttemptCount(), "Should return incremented count of 2");
    }

    @Test
    void getLastAttemptTime_ReturnsLastTime() throws InterruptedException {
        // Test for PRIMITIVE_RETURNS mutations
        long initialTime = counter.getLastAttemptTime();
        
        Thread.sleep(10);
        counter.increment();
        
        long updatedTime = counter.getLastAttemptTime();
        assertTrue(updatedTime > initialTime, "Should return updated time after increment");
        assertTrue(updatedTime <= System.currentTimeMillis(), "Time should not be in the future");
    }

    @Test
    void shouldReset_ReturnsTrueWhenTimeExceeded() {
        // Test for CONDITIONALS_BOUNDARY, NEGATE_CONDITIONALS, and TRUE_RETURNS/FALSE_RETURNS mutations
        
        // Test exact boundary
        assertFalse(counter.shouldReset(Long.MAX_VALUE), "Should return false with very large time limit");
        
        // Test just at boundary (this is tricky without mocking time)
        long currentDiff = System.currentTimeMillis() - counter.getLastAttemptTime();
        assertFalse(counter.shouldReset(currentDiff + 1), "Should return false when time not yet exceeded");
        assertTrue(counter.shouldReset(currentDiff - 1), "Should return true when time is exceeded");
        //assertEquals(counter.shouldReset(0), true, "Should return true with zero time limit");
        
        // Test with negative value (edge case)
        assertTrue(counter.shouldReset(-1), "Should return true with negative time limit");
    }

    @Test
    void shouldReset_ReturnsFalseWhenTimeNotExceeded() throws InterruptedException {
        // Update the last attempt time
        counter.increment();
        
        // Test with a time limit larger than elapsed time
        assertFalse(counter.shouldReset(10000), "Should return false when time limit not exceeded");
    }

    @Test
    void reset_ResetsCountAndUpdatesTime() throws InterruptedException {
        // Increment a few times
        counter.increment();
        counter.increment();
        long timeBeforeReset = counter.getLastAttemptTime();
        
        // Small delay
        Thread.sleep(5);
        
        // Test for VOID_METHOD_CALLS mutations
        counter.reset();
        assertEquals(0, counter.getAttemptCount(), "Attempt count should be reset to 0");
        assertTrue(counter.getLastAttemptTime() > timeBeforeReset, 
                "Last attempt time should be updated after reset");
    }

    @Test
    void multipleOperations_MaintainsCorrectState() throws InterruptedException {
        // Test sequence of operations to ensure state is maintained correctly
        assertEquals(0, counter.getAttemptCount(), "Initial count should be 0");
        
        counter.increment();
        assertEquals(1, counter.getAttemptCount(), "Count should be 1 after increment");
        
        long timeAfterIncrement = counter.getLastAttemptTime();
        Thread.sleep(5);
        
        counter.reset();
        assertEquals(0, counter.getAttemptCount(), "Count should be 0 after reset");
        assertTrue(counter.getLastAttemptTime() > timeAfterIncrement, 
                "Time should be updated after reset");
        
        counter.increment();
        counter.increment();
        assertEquals(2, counter.getAttemptCount(), "Count should be 2 after two increments");
    }

    @Test
    void currentTimeApproximation() throws InterruptedException {
        // This test verifies that System.currentTimeMillis() is actually called
        // and not replaced with a constant (testing for VOID_METHOD_CALLS mutation)
        long initialSystemTime = System.currentTimeMillis();
        AttemptCounter newCounter = new AttemptCounter();
        
        // Time should be approximately equal to current time
        long timeDiff = Math.abs(newCounter.getLastAttemptTime() - initialSystemTime);
        assertTrue(timeDiff < 100, "Constructor should set time close to current system time");
        
        // Small delay
        Thread.sleep(10);
        
        // Increment and verify time update
        newCounter.increment();
        long incrementTimeDiff = Math.abs(newCounter.getLastAttemptTime() - System.currentTimeMillis());
        assertTrue(incrementTimeDiff < 100, "Increment should update time to current system time");
        
        // Reset and verify time update
        Thread.sleep(10);
        newCounter.reset();
        long resetTimeDiff = Math.abs(newCounter.getLastAttemptTime() - System.currentTimeMillis());
        assertTrue(resetTimeDiff < 100, "Reset should update time to current system time");
    }

    @Test
    void invert_negs_mutations() {
        // This test specifically targets the INVERT_NEGS mutation operator
        // The shouldReset method uses a ">" comparison that could be inverted
        
        // Set up a counter with a known last attempt time
        counter.reset();
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - counter.getLastAttemptTime();
        
        // Test with a time slightly less than the difference
        assertTrue(counter.shouldReset(timeDifference - 10), 
                "Should return true when time limit < elapsed time");
        
        // Test with a time slightly more than the difference
        assertFalse(counter.shouldReset(timeDifference + 10), 
                "Should return false when time limit > elapsed time");
    }
    
    @Test
    void math_mutations() {
        // This test specifically targets the MATH mutation operator
        // The shouldReset method uses subtraction that could be mutated
        
        // Create a counter with a specific last attempt time
        counter.reset();
        
        // Test at exact boundary
        long elapsedTime = System.currentTimeMillis() - counter.getLastAttemptTime();
        
        // These assertions check that the math is correct
        assertTrue(counter.shouldReset(elapsedTime - 1), 
                "Should reset when limit is less than elapsed time");
        assertFalse(counter.shouldReset(elapsedTime + 1), 
                "Should not reset when limit is greater than elapsed time");
    }
}