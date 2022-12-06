package com.bravenatorsrobtoics.subcomponent;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PoleSensorController {

    protected final HardwareMap hardwareMap;

    private RevColorSensorV3 poleSensor;

    public PoleSensorController(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        this.poleSensor = hardwareMap.get(RevColorSensorV3.class, "poleSensor");
    }

    private void DisplayTelemetry() {

    }

}
