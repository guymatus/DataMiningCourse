package model;

public class FieldAverageAccumilator {

    private double sum;
    private int numElements;

    public FieldAverageAccumilator() {
        this.sum = 0;
        this.numElements = 0;
    }
    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getNumElements() {
        return numElements;
    }

    public void setNumElements(int numElements) {
        this.numElements = numElements;
    }

    public void accumilate(double value) {
        this.sum += value;
        this.numElements++;
    }
}
