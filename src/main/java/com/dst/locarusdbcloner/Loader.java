package com.dst.locarusdbcloner;

import org.springframework.data.annotation.Id;

public class Loader {

    @Id
    private int serialNumber;
    private String locarusNumber;
    private String recordingSince;

    public Loader(int serialNumber, String locarusNumber, String recordingSince) {
        this.serialNumber = serialNumber;
        this.locarusNumber = locarusNumber;
        this.recordingSince = recordingSince;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getLocarusNumber() {
        return locarusNumber;
    }

    public String getRecordingSince() {
        return recordingSince;
    }
}
