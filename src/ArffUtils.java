import com.sun.tools.javac.util.Pair;
import model.FarthestPair;
import model.FieldAverageAccumilator;
import weka.core.Instance;
import weka.core.Instances;
import org.apache.commons.lang3.ArrayUtils;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ArffUtils {

    private static final String arffFolderPath = "resources/";
    private ArffFileParser arffFileParser;

    public ArffUtils() {
        arffFileParser = new ArffFileParser();
    }

    public void CreateNonRadicalDataSetFile(String fileName) throws IOException {
        String filePath = arffFolderPath + fileName;
        Instances dataSet = arffFileParser.ParseArffFile(filePath);
        arffFileParser.RemoveRadicalEntities(dataSet);
        arffFileParser.CreateNewArffFile(arffFolderPath + "q5.arff", dataSet);
    }

    public Instances readInstances(String fileName) throws IOException {
        String filePath = arffFolderPath + fileName;
        Instances dataSet = arffFileParser.ParseArffFile(filePath);

        return dataSet;
    }

    public void CreateFileWithAverageDataCompletion(String fileName) throws IOException {
        String filePath = arffFolderPath + fileName;
        Instances dataSet = arffFileParser.ParseArffFile(filePath);
        double currentAverage;
//        List<FieldAverageAccumilator> averageAccumilators = new ArrayList<>
//                (Collections.nCopies(dataSet.numAttributes(), new FieldAverageAccumilator()));

        List<FieldAverageAccumilator> averageAccumilators = new ArrayList<>();
        for(int i = 0 ; i < dataSet.numAttributes() - 1; i++){
            averageAccumilators.add(new FieldAverageAccumilator());
        }

        System.out.println("data set before completion: ");
        System.out.println(dataSet.toString());
        for (int i = 0; i < dataSet.numInstances(); i++) {

            for (int j = 0; j < dataSet.numAttributes() - 1; j++) {
                if (!Double.isNaN(dataSet.instance(i).value(j))) {
                    averageAccumilators.get(j).accumilate(dataSet.instance(i).value(j));
                }
            }
        }

        for (int i = 0; i < dataSet.numInstances(); i++) {

            for (int j = 0; j < dataSet.numAttributes() - 1; j++) {
                if (Double.isNaN(dataSet.instance(i).value(j))) {
                    currentAverage = averageAccumilators.get(j).getSum() / averageAccumilators.get(j).getNumElements();
                    dataSet.instance(i).setValue(j, currentAverage);
                }
            }
        }

        for (FieldAverageAccumilator accumilator: averageAccumilators) {
            System.out.println(accumilator.getSum() / accumilator.getNumElements());
        }

        arffFileParser.CreateNewArffFile(arffFolderPath + "q6a.arff",dataSet);
        System.out.println("data set after completion: ");
        System.out.println(dataSet.toString());
    }

    public void CreateFileWithKNearestAverageDataCompletion(int k, String fileName) throws IOException {
        String filePath = arffFolderPath + fileName;
        int currentUnknownEntry, currentUnknownAttrIndex;
        Instances dataSet = arffFileParser.ParseArffFile(filePath);
        double[][] euclideanDistances = CalculateEuclideanDistanceMatrix(dataSet);
        HashMap<Integer, List<Integer>> unknownInstances = extractUnknownInstances(dataSet);

        for (HashMap.Entry<Integer, List<Integer>> entry: unknownInstances.entrySet()) {
            currentUnknownEntry = entry.getKey();
            for(int i = 0 ; i < entry.getValue().size() ; i++){
                currentUnknownAttrIndex = entry.getValue().get(i);
                dataSet.instance(currentUnknownEntry).setValue(currentUnknownAttrIndex,
                        calculateAverageByKNearestNeighbours(dataSet, k, currentUnknownAttrIndex, euclideanDistances[currentUnknownEntry]));
            }
        }

        arffFileParser.CreateNewArffFile(arffFolderPath + "q6c.arff", dataSet);
    }

    private double calculateAverageByKNearestNeighbours(Instances dataSet, int numOfNeighbours, int attributeIndex, double[] euclideanDistance) {
        List<Pair<Integer, Double>> indexToDistanceMap = createIndexToDistanceList(euclideanDistance);
        List<Pair<Integer, Double>> sortedIndexToDistance = indexToDistanceMap.stream().sorted(new Comparator<Pair<Integer, Double>>() {
            @Override
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if(o1.snd == o2.snd)
                    return 0;

                return o1.snd < o2.snd ? -1 : 1;
            }
        }).collect(Collectors.toList());


        int neighboursCounter = 0;
        int index = 1;
        double sum = 0;
        //for every row in the distance matrix, once sorted, the first entry will always be zero
        while(neighboursCounter < numOfNeighbours){
            if(!Double.isNaN(dataSet.instance(sortedIndexToDistance.get(index).fst).value(attributeIndex))) {
                sum += dataSet.instance(sortedIndexToDistance.get(index).fst).value(attributeIndex);
                neighboursCounter++;
                index++;
            }
        }

        return sum / numOfNeighbours;
    }

    private List<Pair<Integer, Double>> createIndexToDistanceList(double[] euclideanDistance) {
        List<Pair<Integer, Double>> indexDistancePairs = new ArrayList<>();
        Pair<Integer, Double> currPair;
        for(int i = 0 ; i < euclideanDistance.length ; i++) {
            currPair = new Pair<>(i, euclideanDistance[i]);
            indexDistancePairs.add(currPair);
        }

        return indexDistancePairs;
    }


    //filters out only the indexes of instances that contain a valid value in the given attribute index
    private List<Integer> filterInstancesWithAttr(Instances dataSet, int attributeIndex) {
        List<Integer> filteredInstances = new ArrayList<>();
        for(int i = 0 ; i < dataSet.numInstances() ; i++){
            if(!Double.isNaN(dataSet.instance(i).value(attributeIndex))){
                filteredInstances.add(i);
            }
        }

        return filteredInstances;
    }

    private HashMap<Integer, List<Integer>>  extractUnknownInstances(Instances dataSet) {
        HashMap<Integer, List<Integer>> unknownInstances = new HashMap<>();
        Instance currInstance;
        List<Integer> currUnknowns;

        for(int i = 0 ; i < dataSet.numInstances() ; i++) {
            currInstance = dataSet.instance(i);
            currUnknowns = getUnknownsInInstance(currInstance);
            if(currUnknowns.size() > 0)
                unknownInstances.put(i, currUnknowns);
            }

        return unknownInstances;
    }

    private List<Integer> getUnknownsInInstance(Instance instance) {
        List<Integer> unknowns = new ArrayList<>();

        for(int i = 0 ; i < instance.numAttributes() ; i++){
            if(Double.isNaN(instance.value(i))){
                unknowns.add(i);
            }
        }

        return unknowns;
    }

    private boolean isUnknownInstance(Instance instance) {
        for(int i = 0 ; i < instance.numAttributes() ; i++){
            if(Double.isNaN(instance.value(i)))
                return true;
        }

        return false;
    }

    public double[][] CalculateEuclideanDistanceMatrix(Instances dataSet)
    {
        double[][] euclideanDistances = new double[dataSet.numInstances()][dataSet.numInstances()];
        double currentDistance;

        for(int i = 0 ; i < dataSet.numInstances() ; i++){
            for(int j = i +1 ; j < dataSet.numInstances() ; j++){
                currentDistance = calculateEuclideanDistance(dataSet.instance(i), dataSet.instance(j));
                euclideanDistances[i][j] = currentDistance;
                euclideanDistances[j][i] = currentDistance;
            }
        }

        return euclideanDistances;
    }

    private double calculateEuclideanDistance(Instance instance1, Instance instance2) {
        double distance = 0;
        for(int i = 0 ; i < instance1.numAttributes() - 1 ; i++){
            if(!Double.isNaN(instance1.value(i)) && !Double.isNaN(instance2.value(i)))
                distance += Math.pow(instance1.value(i) - instance2.value(i), 2);
        }

        return Math.sqrt(distance);
    }

    public double[][] CalculateMahalanobisMatrix(String fileName) throws IOException {
        Instances dataSet = readInstances(fileName);
        double[][] resultMatrix = new double[dataSet.numInstances()][dataSet.numInstances()];
        double[][] covarianceMatrix = MatrixUtils.readRevertedCovarianceMatrix
                (arffFolderPath + "covariance_matrix.txt");
        double currentDistance;
        String newLine = System.getProperty("line.separator");

        for(int i = 0 ; i < dataSet.numInstances() ; i++){
            for(int j = i +1 ; j < dataSet.numInstances() ; j++){
                currentDistance = calculateMahalanobisDistance(dataSet.instance(i), dataSet.instance(j), covarianceMatrix);
                resultMatrix[i][j] = currentDistance;
                resultMatrix[j][i] = currentDistance;
            }
        }

        for(int i = 0 ; i < resultMatrix.length ; i++) {
            for(int j = 0 ; j < resultMatrix[0].length ; j++) {
                System.out.print(resultMatrix[i][j] + ", ");
            }

            System.out.print("\b" + newLine);
        }

        return resultMatrix;
    }

    private double calculateMahalanobisDistance(Instance instanceA, Instance instanceB, double[][] covarianceMatrix) throws IOException {
        double[][] diffTranspose = MatrixUtils.vectorDiffTranspose(instanceA, instanceB);
        double[][] diff = MatrixUtils.vectorDiff(instanceA, instanceB);

        double distance = MatrixUtils.matrixMultiply(MatrixUtils.matrixMultiply
                (diffTranspose,covarianceMatrix), diff)[0][0];
        distance = Math.sqrt(distance);

        return distance;
    }

    public FarthestPair findFarthestTwoInstancesInMatrix(double[][] matrix) {
        FarthestPair farthestInstances = new FarthestPair();
        double maxDistance = 0;
        double currMax = 0;
        List<Double> currList;

        for(int i = 0 ; i< matrix.length ; i++) {
            currList = Arrays.asList(ArrayUtils.toObject(matrix[i]));
            currMax = Collections.max(currList);
            if(currMax > maxDistance) {
                maxDistance = currMax;
                farthestInstances.setInstanceAIndex(i);
                farthestInstances.setInstanceBIndex(currList.indexOf(currMax));
                farthestInstances.setDistance(currMax);
            }
        }

        return farthestInstances;
    }
}
