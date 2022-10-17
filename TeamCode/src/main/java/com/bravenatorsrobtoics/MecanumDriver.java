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

    private static double ScalePower(double value, double max) {
        return max != 0 ? value / max : 0;
    }

    public void DriveByIntervals(double v, double h, double r) {
        // Calculate Motors Speeds
        double frontLeft    = v - h + r;
        double frontRight   = v + h - r;
        double backRight    = v - h - r;
        double backLeft     = v + h + r;

        // Limit the vectors to under 1
        double max = Math.max(
                Math.abs(backLeft),
                Math.max(
                        Math.abs(backRight),
                        Math.max(
                                Math.abs(frontLeft), Math.abs(frontRight)
                        )
                )
        );

        // Scale the power
        if(max > 1) { // Only scale if max is greater than one
            frontLeft   = ScalePower(frontLeft, max);
            frontRight  = ScalePower(frontRight, max);
            backLeft    = ScalePower(backLeft, max);
            backRight   = ScalePower(backRight, max);
        }

        hardware.SetMotorPower(hardware.frontLeft, frontLeft);
        hardware.SetMotorPower(hardware.frontRight, frontRight);
        hardware.SetMotorPower(hardware.backLeft, backLeft);
        hardware.SetMotorPower(hardware.backRight, backRight);
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
