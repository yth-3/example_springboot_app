package org.example.sba.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogicTest {
    @Test
    public void test_LogicTest_sum() {
        assertEquals(2, Logic.sum(1, 1));
    }
    
    @Test
    public void test_LogicTest_sum2() {
        assertEquals(3, Logic.sum(1, 2));   
    }
    
    @Test
    public void test_LogicTest_sum3() {
        assertEquals(3, Logic.sum(Logic.sum(1, 1), 1));   
    }
}
