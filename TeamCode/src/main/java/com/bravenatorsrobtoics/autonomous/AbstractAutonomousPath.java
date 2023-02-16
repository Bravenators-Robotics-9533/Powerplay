package com.bravenatorsrobtoics.autonomous;

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
    protected final RoadrunnerMecanumDrive drive;

    public AbstractAutonomousPath(LinearOpMode opMode, HardwareMap hardwareMap) {
        this.opMode = opMode;
        drive = new RoadrunnerMecanumDrive(hardwareMap);
    }

    public abstract void Run();

}
