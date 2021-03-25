package com.miklene.frequencygenerator;

import com.miklene.frequencygenerator.util.FrequencyCounter;
import com.miklene.frequencygenerator.util.LinearFrequencyCounter;
import com.miklene.frequencygenerator.util.LogarithmicFrequencyCounter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testLogarithmic(){

    }

    @Test
    public void testLinear(){
        FrequencyCounter counter = new LinearFrequencyCounter(0,100);
        double expected = 50;
        double actual = counter.countFrequency(50);
        assertEquals(expected,actual, 0);
        counter = new LinearFrequencyCounter(10,100);
        expected = 60;
        actual = counter.countFrequency(50);
        assertEquals(expected,actual, 0);
    }
}