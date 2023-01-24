import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dispatcher {
    public static void main(String[] args) {

        AtomicInteger wordCount = new AtomicInteger(0);
        AtomicInteger totalWordLength = new AtomicInteger(0);
        Pattern p = Pattern.compile("\\b\\w+'?\\w*\\b");

        List<File> files = Arrays.asList(new File("f1"), new File("f2"), new File("f3"));

        files.stream()
                .parallel()
                .flatMap(f -> {
                    try {
                        return Files.lines(f.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .forEach(s -> {
                    Matcher m = p.matcher(s);
                    while (m.find()) {
                        wordCount.addAndGet(1);
                        totalWordLength.addAndGet(m.group().length());
                    }
                });

        double averagelWordLength = (double) totalWordLength.get() / wordCount.get();

        System.out.printf("totalWordLength = %s, wordCount = %s \naverageLenthWord = %f", totalWordLength, wordCount, averagelWordLength);
    }
}
