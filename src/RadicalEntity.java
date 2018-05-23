public class RadicalEntity {

    private int recordNum;
    private double value;

    public RadicalEntity(int recordNum, double value) {
        this.recordNum = recordNum;
        this.value = value;
    }


    public int getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(int recordNum) {
        this.recordNum = recordNum;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}