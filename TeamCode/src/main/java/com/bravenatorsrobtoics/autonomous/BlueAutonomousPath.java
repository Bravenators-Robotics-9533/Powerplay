package com.bravenatorsrobtoics.autonomous;

import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.vision.VisionPathway;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BlueAutonomousPath extends AbstractAutonomousPath {

    public BlueAutonomousPath(LinearOpMode opMode, MecanumDriver legacyDriver, HardwareMap hardwareMap) {
        super(opMode, legacyDriver, hardwareMap);
    }

    @Override
    public void Run(VisionPathway.ParkingPosition parkingPosition) {

    }

}
