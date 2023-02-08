package com.bravenatorsrobtoics.autonomous;

import com.qualcomm.robotcore.hardware.HardwareMap;

import roadrunner.drive.SampleMecanumDrive;

public abstract class AbstractAutonomousPath {

    protected final SampleMecanumDrive drive;

    public AbstractAutonomousPath(HardwareMap hardwareMap) {
        drive = new SampleMecanumDrive(hardwareMap);
    }

    public abstract void Run();

}
