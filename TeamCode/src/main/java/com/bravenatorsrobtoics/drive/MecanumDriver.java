package com.bravenatorsrobtoics.drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.vuforia.SmartTerrain;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumDriver {

    private final LinearOpMode opMode;
    private final MecanumDriveHardware hardware;

    private final Telemetry telemetry;

    public enum TurnDirection {
        COUNTER_CLOCKWISE,
        CLOCKWISE
    }

    public MecanumDriver(LinearOpMode opMode, MecanumDriveHardware hardware) {
        this.opMode = opMode;
        this.telemetry = opMode.telemetry;
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
        double theta = Math.atan2(v, h);
        double power = Math.hypot(h, v);

        double sin = Math.sin(theta - Math.PI / 4);
        double cos = Math.cos(theta - Math.PI / 4);
        double max = Math.max(Math.abs(sin), Math.abs(cos));

        double leftFront  = power * cos/max + r;
        double rightFront = power * sin/max - r;
        double leftRear   = power * sin/max + r;
        double rightRear  = power * cos/max - r;

        if((power + Math.abs(r)) > 1) {
            leftFront /= power + r;
            rightFront /= power + r;
            leftRear /= power + r;
            rightRear /= power + r;
        }

        hardware.SetMotorPower(hardware.frontLeft, leftFront);
        hardware.SetMotorPower(hardware.frontRight, rightFront);
        hardware.SetMotorPower(hardware.backLeft, leftRear);
        hardware.SetMotorPower(hardware.backRight, rightRear);
    }

//    public void DriveByIntervals(double v, double h, double r) {
//        // Calculate Motors Speeds
//        double frontLeft    = v - h + r;
//        double frontRight   = v + h - r;
//        double backRight    = v - h - r;
//        double backLeft     = v + h + r;
//
//        // Limit the vectors to under 1
//        double max = Math.max(
//                Math.abs(backLeft),
//                Math.max(
//                        Math.abs(backRight),
//                        Math.max(
//                                Math.abs(frontLeft), Math.abs(frontRight)
//                        )
//                )
//        );
//
//        // Scale the power
//        if(max > 1) { // Only scale if max is greater than one
//            frontLeft   = ScalePower(frontLeft, max);
//            frontRight  = ScalePower(frontRight, max);
//            backLeft    = ScalePower(backLeft, max);
//            backRight   = ScalePower(backRight, max);
//        }
//
//        hardware.SetMotorPower(hardware.frontLeft, frontLeft);
//        hardware.SetMotorPower(hardware.frontRight, frontRight);
//        hardware.SetMotorPower(hardware.backLeft, backLeft);
//        hardware.SetMotorPower(hardware.backRight, backRight);
//    }

    private static final float LOW_CLIP = 0.2f;
    private static final float HIGH_CLIP = 0.5f;
    private static final float LOW_CLIP_MULTIPLIER = 1.0f / LOW_CLIP;

    private static final float SLOW_SPEED_MIN = 0.1f;

    public void DriveByInches(double inches, double power) {
        int ticksToMove = (int) (inches * MecanumDriveHardware.ENCODER_TICKS_PER_INCH);

        // Calculate Initial Positions
        int flInitialPosition = hardware.frontLeft.getCurrentPosition();
        int frInitialPosition = hardware.frontRight.getCurrentPosition();
        int blInitialPosition = hardware.backLeft.getCurrentPosition();
        int brInitialPosition = hardware.backRight.getCurrentPosition();

        // Set the target positions
        AddToTargetPosition(hardware.frontLeft, ticksToMove);
        AddToTargetPosition(hardware.frontRight, ticksToMove);
        AddToTargetPosition(hardware.backLeft, ticksToMove);
        AddToTargetPosition(hardware.backRight, ticksToMove);

        // Change the run mode
        SetRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        while(opMode.opModeIsActive() && IsBusy()) {

            // Calculate the progress from 0 to 1
            float flProgress = (hardware.frontLeft.getCurrentPosition() - flInitialPosition) / (float) ticksToMove;
            float frProgress = (hardware.frontRight.getCurrentPosition() - frInitialPosition) / (float) ticksToMove;
            float blProgress = (hardware.backLeft.getCurrentPosition() - blInitialPosition) / (float) ticksToMove;
            float brProgress = (hardware.backRight.getCurrentPosition() - brInitialPosition) / (float) ticksToMove;

            double flPower = power;
            double frPower = power;
            double blPower = power;
            double brPower = power;

          // Low Clip FL
            if(flProgress <= LOW_CLIP) {
                float normalizedProgress = 1 - (flProgress * LOW_CLIP_MULTIPLIER); // Normalize from 1 to 0
                flPower -= normalizedProgress * flPower;
            }

            // High Clip FL
            if(flProgress >= HIGH_CLIP) {
                float normalizedProgress = (flProgress - HIGH_CLIP) / (1 - HIGH_CLIP);
                flPower -= normalizedProgress * flPower;
            }

            // Low Clip FR
            if(frProgress <= LOW_CLIP) {
                float normalizedProgress = 1 - (frProgress * LOW_CLIP_MULTIPLIER); // Normalize from 1 to 0
                frPower -= normalizedProgress * frPower;
            }

            // High Clip FR
            if(frProgress >= HIGH_CLIP) {
                float normalizedProgress = (frProgress - HIGH_CLIP) / (1 - HIGH_CLIP);
                frPower -= normalizedProgress * frPower;
            }

            // Low Clip BL
            if(blProgress <= LOW_CLIP) {
                float normalizedProgress = 1 - (blProgress * LOW_CLIP_MULTIPLIER); // Normalize from 1 to 0
                blPower -= normalizedProgress * blPower;
            }

            // High Clip BL
            if(blProgress >= HIGH_CLIP) {
                float normalizedProgress = (blProgress - HIGH_CLIP) / (1 - HIGH_CLIP);
                blPower -= normalizedProgress * blPower;
            }

            // Low Clip BR
            if(brProgress <= LOW_CLIP) {
                float normalizedProgress = 1 - (brProgress * LOW_CLIP_MULTIPLIER); // Normalize from 1 to 0
                brPower -= normalizedProgress * brPower;
            }

            // High Clip BR
            if(brProgress >= HIGH_CLIP) {
                float normalizedProgress = (brProgress - HIGH_CLIP) / (1 - HIGH_CLIP);
                brPower -= normalizedProgress * brPower;
            }

            hardware.SetMotorPower(hardware.frontLeft, Math.max(SLOW_SPEED_MIN, flPower));
            hardware.SetMotorPower(hardware.frontRight, Math.max(SLOW_SPEED_MIN, frPower));
            hardware.SetMotorPower(hardware.backLeft, Math.max(SLOW_SPEED_MIN, blPower));
            hardware.SetMotorPower(hardware.backRight, Math.max(SLOW_SPEED_MIN, brPower));
        }

        hardware.StopAllMotors();

        SetRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void TurnDegrees(TurnDirection turnDirection, int degrees, double power) {
        double initialHeading = hardware.GetCurrentHeading();

        while(opMode.opModeIsActive()) {
            telemetry.addData("Delta Heading", hardware.GetCurrentHeading());
            telemetry.update();
        }
    }

}
