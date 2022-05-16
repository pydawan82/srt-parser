package com.pydawan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;

import com.pydawan.srt.SrtParser;
import com.pydawan.srt.SrtToken;

/**
 * Unit test for simple App.
 */
class AppTest {

    @Test
    void parserTest() {
        String input = """
                1
                00:00:53,429 --> 00:00:56,803
                Certains matins, sans savoir pourquoi,

                2
                00:00:58,142 --> 00:01:00,641
                je me réveille en larmes.

                3
                00:01:01,853 --> 00:01:05,269
                Impossible de me rappeler
                ce dont j'ai sans doute rêvé.

                4
                00:01:06,150 --> 00:01:08,274
                - Mais...
                - Mais...

                    """;

        InputStream source = new ByteArrayInputStream(input.getBytes());

        SrtParser parser = new SrtParser(source);
        assertEquals(new SrtToken(1, Duration.ofSeconds(53).plusMillis(429), Duration.ofSeconds(56).plusMillis(803),
                "Certains matins, sans savoir pourquoi,"), parser.next());
    }
}
