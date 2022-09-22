package com.bravenatorsrobtoics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MecanumDriver {

    private final LinearOpMode opMode;
    private final MecanumDriveHardware hardware;

    public MecanumDriver(LinearOpMode opMode, MecanumDriveHardware hardware) {
        this.opMode = opMode;
        this.hardware = hardware;
    }

    private void SetRunMode(DcMotor.RunMode runMode) {
        hardware.frontLeft.setMode(runMode);
        hardware.frontRight.setMode(runMode);
        hardware.backRight.setMode(runMode);
        hardware.backLeft.setMode(runMode);
    }

    private void AddToTargetPosition(DcMotorEx motor, int encoderTicks) {
        motor.setTargetPosition(motor.getCurrentPosition() + encoderTicks);
    }

    private boolean IsBusy() {
        return
                hardware.frontLeft.isBusy() || hardware.backLeft.isBusy() ||
                hardware.frontRight.isBusy() || hardware.backRight.isBusy();
    }

    public void DriveByInches(double inches, double power) {

        int ticksToMove = (int) (inches * MecanumDriveHardware.ENCODER_TICKS_PER_INCH);

        AddToTargetPosition(hardware.frontLeft, ticksToMove);
        AddToTargetPosition(hardware.frontRight, ticksToMove);
        AddToTargetPosition(hardware.backLeft, ticksToMove);
        AddToTargetPosition(hardware.backRight, ticksToMove);

        SetRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        while(opMode.opModeIsActive() && IsBusy()) {
            hardware.SetMotorPower(hardware.frontLeft, power);
            hardware.SetMotorPower(hardware.frontRight, power);
            hardware.SetMotorPower(hardware.backLeft, power);
            hardware.SetMotorPower(hardware.backRight, power);
        }

        hardware.StopAllMotors();

        SetRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

}
