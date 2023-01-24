import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DispatcherV2 {
    public static void main(String[] args) {

        AtomicInteger totalWordLength = new AtomicInteger(0);
        AtomicInteger wordCount = new AtomicInteger(0);

        List<File> files = Arrays.asList(new File("f1"), new File("f2"), new File("f3"));

        files.stream()
//				.parallel()
                .map(file -> new Thread(() -> Controller.readFile(file, totalWordLength, wordCount)))
                .peek(Thread::start).forEach(t -> {
                    try {
                        t.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                });

        double averagelWordLength = (double) totalWordLength.get() / wordCount.get();

        System.out.printf("totalWordLength = %s, wordCount = %s \naverageLenthWord = %f", totalWordLength, wordCount, averagelWordLength);
    }
}

class Controller {

    public static void readFile(File file, AtomicInteger totalWordLength, AtomicInteger wordCount) {

        try (Stream<String> inputStream = Files.lines(file.toPath())) {
            countNumberAndWordsLength(inputStream, totalWordLength, wordCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void countNumberAndWordsLength(Stream<String> inputStream, AtomicInteger totalWordLength, AtomicInteger wordCount) {

        Pattern p = Pattern.compile("\\b\\w+'?\\w*\\b");

        inputStream
                .parallel()
                .forEach(s -> {
                    Matcher m = p.matcher(s);
                    while(m.find()) {
                        wordCount.addAndGet(1);
                        totalWordLength.addAndGet(m.group().length());
                    }});



    }
}

