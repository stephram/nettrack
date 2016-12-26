package com.nettrack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Created by sg on 26/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackerStatus implements Serializable {
    private String cardAddress;
    private String baseAddress;
    private String sosActive;
    private String batteryPercentage;
    private String signalStrength;
    private String broadcastInterval;
    private String packetNumber;

    @JsonProperty("CA")
    public String getCardAddress() {
        return cardAddress;
    }

    public void setCardAddress(String cardAddress) {
        this.cardAddress = cardAddress;
    }

    @JsonProperty("BA")
    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    @JsonProperty("SOS")
    public String getSosActive() {
        return sosActive;
    }

    public void setSosActive(String sosActive) {
        this.sosActive = sosActive;
    }

    @JsonProperty("BATT")
    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    @JsonProperty("RSSI")
    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    @JsonProperty("INTV")
    public String getBroadcastInterval() {
        return broadcastInterval;
    }

    public void setBroadcastInterval(String broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
    }

    @JsonProperty("SN")
    public String getPacketNumber() {
        return packetNumber;
    }

    public void setPacketNumber(String packetNumber) {
        this.packetNumber = packetNumber;
    }


    public String toString() {
        return "TrackerStatus{ cardAddress=" + cardAddress
            + ", baseAddress=" + baseAddress
            + ", sosActive=" + sosActive
            + ", batteryPercentage=" + batteryPercentage
            + ", signalStrength=" + signalStrength
            + ", broadcastInterval=" + broadcastInterval
            + ", packetNumber=" + packetNumber
            + " }";
    }
}
