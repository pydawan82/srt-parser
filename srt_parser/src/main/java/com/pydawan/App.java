package com.pydawan;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.regex.Pattern;

import com.pydawan.srt.SrtParser;
import com.pydawan.srt.SrtToken;
import com.pydawan.util.DurationUtil;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Hello world!
 */
public final class App {

    private static final Pattern DURATION_FORMAT = Pattern.compile("(\\d+):(\\d+):(\\d+)");

    private static Duration parseDuration(String durationTxt) {
        var matcher = DURATION_FORMAT.matcher(durationTxt);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: " + durationTxt);
        }

        var hours = Integer.parseInt(matcher.group(1));
        var minutes = Integer.parseInt(matcher.group(2));
        var seconds = Integer.parseInt(matcher.group(3));

        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Exception {

        ArgumentParser argParser = ArgumentParsers
                .newFor("SRT Duration Scaling")
                .addHelp(true)
                .build();

        argParser.description("""
                Rescales the time of a SRT file.
                Duration format: HH:MM:SS
                """);

        argParser.addArgument("--input")
                .required(true);
        argParser.addArgument("--output")
                .required(false);
        argParser.addArgument("--input-duration")
                .required(true)
                .help("Duration of the input file, format: HH:MM:SS");
        argParser.addArgument("--output-duration")
                .required(true)
                .help("Duration of the output file, format: HH:MM:SS");

        Namespace namespace;

        try {
            namespace = argParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argParser.handleError(e);
            System.exit(1);
            return;
        }

        String inputFile = namespace.getString("input");
        String outputFile = namespace.getString("output");
        if (outputFile == null) {
            outputFile = inputFile + ".scaled";
        }
        Duration inputDuration = parseDuration(namespace.getString("input_duration"));
        Duration outputDuration = parseDuration(namespace.getString("output_duration"));
        double ratio = DurationUtil.ratio(outputDuration, inputDuration);

        InputStream source = Files.newInputStream(Path.of(inputFile), StandardOpenOption.READ);
        OutputStream output = Files.newOutputStream(Path.of(outputFile), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);

        try (PrintWriter writer = new PrintWriter(output)) {
            SrtParser parser = new SrtParser(source);

            while (parser.hasNext()) {
                SrtToken token = parser.next();

                Duration start = token.getStart();
                start = DurationUtil.scale(start, ratio);
                token.setStart(start);

                Duration end = token.getEnd();
                end = DurationUtil.scale(end, ratio);
                token.setEnd(end);

                writer.println(token);
            }
        }

        source.close();
    }
}
