package org.example.sba.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogicTest {
    @Test
    public void test_LogicTest_sum() {
        assertEquals(2, Logic.sum(1, 1));
    }
    
    @Test
    public void test_LogicTest_sum() {
        assertEquals(3, Logic.sum(1, 2));   
    }
}
