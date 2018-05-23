
import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
        ArffFileParser arffFileParser = new ArffFileParser("resources/glass.arff");
        arffFileParser.createRecordResolver();

    }
}

