package com.pydawan.srt;

import java.io.InputStream;
import java.time.Duration;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.javatuples.Pair;

/**
 * A class that parses SRT files.
 */
public class SrtParser {

    private static final String DURATION_DELIMITER = " --> ";
    private static final Pattern DURATION_FORMAT = Pattern.compile("([0-9]+):([0-9]+):([0-9]+),([0-9]+)");

    private Scanner scanner;

    /**
     * Constructs a new SrtParser allowing to parse the given input stream.
     * 
     * @param source The input stream to parse.
     */
    public SrtParser(InputStream source) {
        this.scanner = new Scanner(source);
    }

    /**
     * Parses the input stream and returns a new Srt token.
     * 
     * @return The next Srt token.
     */
    public SrtToken next() {
        int order = parseOrder();
        Pair<Duration, Duration> duration = parseDurations();
        String text = parseText();
        return new SrtToken(order, duration.getValue0(), duration.getValue1(), text);
    }

    public boolean hasNext() {
        return scanner.hasNextInt();
    }

    /**
     * Parses the input stream and returns a stream of Srt tokens.
     * 
     * @return A stream of Srt tokens.
     */
    public Stream<SrtToken> stream() {
        if (!hasNext())
            return Stream.empty();

        return Stream.iterate(next(), (token) -> hasNext(), (token) -> next());
    }

    private int parseOrder() {
        String line = scanner.nextLine();
        int order = Integer.parseInt(line);

        return order;
    }

    private Pair<Duration, Duration> parseDurations() {
        String line = scanner.nextLine();
        String[] parts = line.split(DURATION_DELIMITER);

        Duration start = parseDuration(parts[0]);
        Duration end = parseDuration(parts[1]);

        return new Pair<>(start, end);
    }

    private Duration parseDuration(String durationTxt) {
        Matcher matcher = DURATION_FORMAT.matcher(durationTxt);

        matcher.find();

        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        int seconds = Integer.parseInt(matcher.group(3));
        int millis = Integer.parseInt(matcher.group(4));

        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusMillis(millis);
    }

    private String parseText() {
        StringBuilder text = new StringBuilder();
        String line;

        do {
            line = scanner.nextLine();
            if (!line.isEmpty())
                text.append(line).append("\n");
        } while (!line.isEmpty());

        if (!text.isEmpty())
            text.deleteCharAt(text.length() - 1);

        return text.toString();
    }
}
