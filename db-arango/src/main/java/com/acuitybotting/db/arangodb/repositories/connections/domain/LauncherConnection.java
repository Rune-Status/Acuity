package com.acuitybotting.db.arangodb.repositories.connections.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.text.DecimalFormat;

/**
 * Created by Zachary Herridge on 8/14/2018.
 */
@Setter
@Getter
@ToString
public class LauncherConnection {

    private String _key;
    private String principalId;

    private MachineState state;

    @Getter
    public static class MachineState {

        private transient final DecimalFormat decimalFormat = new DecimalFormat("#.##");

        private double cpuLoad;
        private double cpuUpTime;
        private double halMemoryAvailable;
        private double memoryTotal;
        private String userName;

        public String getFormattedCpuLoad(){
            return getCpuLoad() == 0 ? "NA" : decimalFormat.format(getCpuLoad() * 100) + "%";
        }
    }
}
