import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hagainuriel on 04/06/2018.
 */
public class MatrixUtils {

    private static double[][] createVector(Instance instance) {
        double[][] vector = new double[instance.numAttributes()][1];
        for(int i = 0 ; i < vector.length ; i++){
            vector[i][0] = instance.value(i);
        }

        return vector;
    }

    private static double[][] createVectorTranspose(Instance instance) {
        double[][] vector = new double[1][instance.numAttributes()];
        for(int i = 0 ; i < vector[0].length ; i++){
            vector[0][i] = instance.value(i);
        }

        return vector;
    }

    public static double[][] vectorDiff(Instance instance1, Instance instance2) {
        double[][] vector1 = createVector(instance1);
        double[][] vector2 = createVector(instance2);
        double[][] result = new double[instance1.numAttributes()][1];

        for(int i = 0 ; i < result.length ; i++) {
            result[i][0] = vector1[i][0] - vector2[i][0];
        }

        return result;
    }

    public static double[][] vectorDiffTranspose(Instance instance1, Instance instance2) {
        double[][] vector1 = createVectorTranspose(instance1);
        double[][] vector2 = createVectorTranspose(instance2);
        double[][] result = new double[1][instance1.numAttributes()];

        for(int i = 0 ; i < result[0].length ; i++){
            result[0][i] = vector1[0][i] - vector2[0][i];
        }

        return result;
    }

    public static double[][] matrixMultiply(double[][] matrixA, double[][] matrixB) {
        double[][] resultMatrix = new double[matrixA.length][matrixB[0].length];
        double currentVal = 0;
        for(int i = 0 ; i < matrixA.length ; i++) {
            for(int j = 0 ; j < matrixB[0].length ; j++) {
                for(int k = 0 ; k < matrixA[0].length ; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return resultMatrix;
    }

    public static double[][] readRevertedCovarianceMatrix(String filePath) throws IOException {
        double[][] result = null;
        int currRow = 0;
        int rowSize, columnSize;
        List<Double> currList;
        String line;

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            rowSize = Integer.parseInt(reader.readLine());
            columnSize = Integer.parseInt(reader.readLine());
            result = new double[rowSize][columnSize];

            do{
                line = reader.readLine();
                currList = Arrays.asList(line.split(",")).stream().map
                        (elem -> Double.parseDouble(elem)).collect(Collectors.toList());

                for(int i = 0 ; i < currList.size() ; i++){
                    result[currRow][i] = currList.get(i);
                }

                currRow++;
            }while(line != null);

        }catch(Exception e){
            System.out.println("failed to parse matrix file: " + e.getMessage());
        }

        return result;
    }
}
