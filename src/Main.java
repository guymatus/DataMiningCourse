
import weka.core.Instances;

import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
//        ArffFileParser arffFileParser = new ArffFileParser();
//        Instances dataSet = arffFileParser.ParseArffFile("resources/glass_missing.arff");
//        arffFileParser.RemoveRadicalEntities(dataSet);
//        arffFileParser.CreateNewArffFile("resources/new_glass.arff", dataSet);
        ArffUtils arffUtils = new ArffUtils();
      //  arffUtils.CreateFileWithAverageDataCompletion("glass_missing.arff");
//        arffUtils.CalculateEuclideanDistanceMatrix(dataSet);
        arffUtils.CreateNonRadicalDataSetFile("glass.arff");
        arffUtils.CreateFileWithKNearestAverageDataCompletion(7,"glass_missing.arff");

    }
}

