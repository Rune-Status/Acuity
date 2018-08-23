package com.acuitybotting.db.arango.acuity.rabbit_db.domain.sub_documents;

import com.acuitybotting.db.arango.acuity.rabbit_db.domain.RabbitSubDocument;
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
public class LauncherConnection extends RabbitSubDocument {

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
