package ru.prolog.util.window;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WindowColorTest {
    @Test
    public void intConstructorTest() {
        assertEquals(new WindowColor(0x123456), new WindowColor(0x12, 0x34, 0x56));
        assertEquals(0x123456, new WindowColor(0x123456).rgb());
    }
}