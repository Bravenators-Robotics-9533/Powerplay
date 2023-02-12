package com.bravenatorsrobtoics.autonomous;

import com.qualcomm.robotcore.hardware.HardwareMap;

import roadrunner.drive.RoadrunnerMecanumDrive;

public abstract class AbstractAutonomousPath {

    protected final RoadrunnerMecanumDrive drive;

    public AbstractAutonomousPath(HardwareMap hardwareMap) {
        drive = new RoadrunnerMecanumDrive(hardwareMap);
    }

    public abstract void Run();

}
