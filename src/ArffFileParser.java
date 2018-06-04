import model.RadicalEntity;
import model.RadicalFieldEntities;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class ArffFileParser {



    public Instances ParseArffFile(String filePath) throws IOException {
        return extractDataSet(filePath);
    }

    private Instances extractDataSet(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath)) ;
        Instances data = new Instances(reader);
        reader.close();
        data.setClassIndex(data.numAttributes() - 1);

        return data;
    }

    public void RemoveRadicalEntities(Instances dataSet) throws IOException {
        ArrayList<RadicalFieldEntities> radicalFieldEntities = getRadicalEntities(dataSet);
        Set<Integer> radicalSet = mapToSet(radicalFieldEntities);

        for (Integer integer : radicalSet) {
            int num = integer;
            System.out.println("removing record: " + dataSet.remove(num));
        }
    }

    public void CreateNewArffFile(String fileName, Instances dataSet) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(dataSet.toString());
        writer.flush();
        writer.close();
    }

    private Set<Integer> mapToSet(ArrayList<RadicalFieldEntities> radicalFieldEntities){
        Set<Integer> maxSet = (radicalFieldEntities.stream().map(rad -> rad.getMaxEntity().getRecordNum()).collect(Collectors.toSet()));
        Set<Integer> minSet = (radicalFieldEntities.stream().map(rad -> rad.getMinEntity().getRecordNum()).collect(Collectors.toSet()));

        maxSet.addAll(minSet);
        return maxSet;
    }

    private ArrayList<RadicalFieldEntities> getRadicalEntities(Instances dataSet){
        ArrayList<RadicalFieldEntities> radicalFieldEntities = initRadicalFieldEntities(dataSet);
        for (int i = 0; i < dataSet.numInstances(); i++)
        {

            for (int j=0; j<dataSet.numAttributes()-1;j++)
            {
                if( dataSet.instance(i).value(j) > radicalFieldEntities.get(j).getMaxEntity().getValue())
                {
                    radicalFieldEntities.get(j).getMaxEntity().setRecordNum(i);
                    radicalFieldEntities.get(j).getMaxEntity().setValue(dataSet.instance(i).value(j));
                }
                if( dataSet.instance(i).value(j) < radicalFieldEntities.get(j).getMinEntity().getValue())
                {
                    radicalFieldEntities.get(j).getMinEntity().setRecordNum(i);
                    radicalFieldEntities.get(j).getMinEntity().setValue(dataSet.instance(i).value(j));
                }
            }
        }

        return radicalFieldEntities;
    }

    private ArrayList<RadicalFieldEntities> initRadicalFieldEntities(Instances dataSet) {
        RadicalFieldEntities currRadicalFieldEntities;
        RadicalEntity currMinEntity, currMaxEntity;
        Instance firstInstance = dataSet.instance(0);

        ArrayList<RadicalFieldEntities> radicalFieldEntities = new ArrayList<>();

        for(int i = 0 ; i < dataSet.numAttributes() -1 ; i++){
            currMaxEntity = new RadicalEntity(0, firstInstance.value(i));
            currMinEntity = new RadicalEntity(0, firstInstance.value(i));
            currRadicalFieldEntities = new RadicalFieldEntities();
            currRadicalFieldEntities.setMaxEntity(currMaxEntity);
            currRadicalFieldEntities.setMinEntity(currMinEntity);

            radicalFieldEntities.add(currRadicalFieldEntities);
        }

        return  radicalFieldEntities;
    }

    public void printFirstEntity(Instances dataSet){
        System.out.println(dataSet.instance(0));
        Instance firstInstance = dataSet.instance(0);
        System.out.println(firstInstance.value(0));
    }


}
