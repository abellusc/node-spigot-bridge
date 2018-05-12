/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.junit.Test;

import net.timluq.mc.nodespigotbridge.Encodings;

import static org.junit.Assert.*;

import java.io.IOException;

public class LibraryTest {
    @Test public void testEscapeString() throws IOException {
        assertEquals("EscapeString.escape of an empty string should return empty string", "", Encodings.escapeString(new StringBuilder(), "").toString());
        String t1 = "abc 123 efg 567.";
        assertEquals("EscapeString.escape of a normal latin string should return equal result", t1, Encodings.escapeString(new StringBuilder(), t1).toString());
        assertEquals("EscapeString.escape of a newline should be correctly escaped", "\\n", Encodings.escapeString(new StringBuilder(), "\n").toString());
        assertEquals("EscapeString.escape of a string containing newline should be correctly escaped", "test\\n123", Encodings.escapeString(new StringBuilder(), "test\n123").toString());
    }
}
