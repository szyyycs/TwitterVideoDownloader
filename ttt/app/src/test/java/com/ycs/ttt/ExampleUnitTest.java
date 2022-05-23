package com.ycs.ttt;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        ArrayList<String> list=new ArrayList<>();
        list.add("11");
        list.add("22");
        list.remove("33");
        assertEquals(list.size(), 2);
    }
}