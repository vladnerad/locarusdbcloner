package com.dst.locarusdbcloner.response;

import java.util.List;

public class LocarusResultField {

    public LocarusResultField() {
    }

    public LocarusResultField(String error) {
        System.out.print("ERROR, reason is: ");
    }

    private List<LocarusDataField> data;

    public List<LocarusDataField> getData() {
        return data;
    }

    public void setData(List<LocarusDataField> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                '}';
    }
}
