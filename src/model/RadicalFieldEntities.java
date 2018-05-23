package model;

import java.text.MessageFormat;

public  class RadicalFieldEntities {

    private RadicalEntity maxEntity;
    private RadicalEntity minEntity;

    public RadicalEntity getMinEntity() {
        return minEntity;
    }

    public void setMinEntity(RadicalEntity minEntity) {
        this.minEntity = minEntity;
    }

    public RadicalEntity getMaxEntity() {
        return maxEntity;
    }

    public void setMaxEntity(RadicalEntity maxEntity) {
        this.maxEntity = maxEntity;
    }

    @Override
    public String toString(){
        return MessageFormat.format("Max -> recordNum: {0}  value: {1}" +
                "\nMin -> recordNum: {2}  value: {3}",this.getMaxEntity().getRecordNum(),
                this.getMaxEntity().getValue(),this.getMinEntity().getRecordNum(), this.getMinEntity().getValue());
    }
}


