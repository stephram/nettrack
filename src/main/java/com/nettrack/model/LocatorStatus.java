package com.nettrack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;

/**
 * Created by sg on 26/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocatorStatus implements Serializable {
    private String baseAddress;
    private String packetType;
    private String heartBeatRate;
    private String baseStationCode;
    private String baseStationFirmware;
    private Instant timestamp;

    public LocatorStatus() {
        this.timestamp = Instant.now();
    }

    @JsonProperty("BA")
    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    @JsonProperty("PT")
    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    @JsonProperty("HB")
    public String getHeartBeatRate() {
        return heartBeatRate;
    }

    public void setHeartBeatRate(String heartBeatRate) {
        this.heartBeatRate = heartBeatRate;
    }

    @JsonProperty("BDT")
    public String getBaseStationCode() {
        return baseStationCode;
    }

    public void setBaseStationCode(String baseStationCode) {
        this.baseStationCode = baseStationCode;
    }

    @JsonProperty("BVER")
    public String getBaseStationFirmware() {
        return baseStationFirmware;
    }

    public void setBaseStationFirmware(String baseStationFirmware) {
        this.baseStationFirmware = baseStationFirmware;
    }

    @JsonProperty("timestamp")
    public Instant getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return "LocatorStatus{ baseAddress=" + baseAddress
            + ", packetType=" + packetType
            + ", heartBeatRate=" + heartBeatRate
            + ", baseStationCode=" + baseStationCode
            + ", baseStationFirmware=" + baseStationFirmware
            + ", timestamp=" + timestamp
            + " }";
    }
}
