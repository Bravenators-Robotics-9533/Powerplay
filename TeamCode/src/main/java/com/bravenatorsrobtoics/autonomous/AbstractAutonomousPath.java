package com.bravenatorsrobtoics.autonomous;

import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.vision.VisionPathway;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import roadrunner.drive.RoadrunnerMecanumDrive;

public abstract class AbstractAutonomousPath {

    protected void WaitMillis(int millis) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        while(opMode.opModeIsActive() && timer.milliseconds() <= millis);
    }

    protected final LinearOpMode opMode;
    protected final MecanumDriver legacyDriver;
    protected final RoadrunnerMecanumDrive drive;

    public AbstractAutonomousPath(LinearOpMode opMode, MecanumDriver legacyDriver, HardwareMap hardwareMap) {
        this.opMode = opMode;
        this.legacyDriver = legacyDriver;
        drive = new RoadrunnerMecanumDrive(hardwareMap);
    }

    public abstract void Run(VisionPathway.ParkingPosition parkingPosition);

}
