package com.dst.locarusdbcloner.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "alt", "dir", "acceleration", "valid"})
public class Coordinates {
    @JsonProperty("lon")
    private String lon;
    @JsonProperty("lat")
    private String lat;
    @JsonProperty("speed")
    private Double speed;
}