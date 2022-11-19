package com.bravenatorsrobtoics.drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

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

    public void Drive(double v, double h, double r) {
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

    private static final float LOW_CLIP = 0.2f;
    private static final float HIGH_CLIP = 0.5f;

    private static final float SLOW_SPEED_MIN = 0.1f;

    /**
     * @author Nick Fanelli
     * @param absProgress total progress of the motor to target
     * @param originalPower the (top speed) or target speed
     * @return the eased value
     */
    protected double EaseInOut(float absProgress, double originalPower) {
        return (absProgress <= MecanumDriver.LOW_CLIP) ? (originalPower - ((1 - (absProgress * (1.0 / MecanumDriver.LOW_CLIP))) * originalPower))
                : ((absProgress >= MecanumDriver.HIGH_CLIP) ? (originalPower - (((absProgress - MecanumDriver.HIGH_CLIP) / (1 - MecanumDriver.HIGH_CLIP)) * originalPower)) : (originalPower));
    }

    public void DriveByInches(double inches, double power) {
        DriveByInches(inches, inches, power);
    }

    public void DriveByInches(double leftInches, double rightInches, double power) {
        int ticksToMoveLeft = (int) (leftInches * MecanumDriveHardware.ENCODER_TICKS_PER_INCH);
        int ticksToMoveRight = (int) (rightInches * MecanumDriveHardware.ENCODER_TICKS_PER_INCH);

        // Calculate Initial Positions
        int flInitialPosition = hardware.frontLeft.getCurrentPosition();
        int frInitialPosition = hardware.frontRight.getCurrentPosition();
        int blInitialPosition = hardware.backLeft.getCurrentPosition();
        int brInitialPosition = hardware.backRight.getCurrentPosition();

        int flTargetPosition = flInitialPosition + ticksToMoveLeft;
        int frTargetPosition = frInitialPosition + ticksToMoveRight;
        int blTargetPosition = blInitialPosition + ticksToMoveLeft;
        int brTargetPosition = brInitialPosition + ticksToMoveRight;

        // Set the target positions
        hardware.frontLeft.setTargetPosition(flTargetPosition);
        hardware.frontRight.setTargetPosition(frTargetPosition);
        hardware.backLeft.setTargetPosition(blTargetPosition);
        hardware.backRight.setTargetPosition(brTargetPosition);

        // Change the run mode
        SetRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        do {
            if(!opMode.opModeIsActive())
                break;

            // Calculate the progress from 0 to 1
            float flProgress = (hardware.frontLeft.getCurrentPosition() - flInitialPosition) / (float) ticksToMoveLeft;
            float frProgress = (hardware.frontRight.getCurrentPosition() - frInitialPosition) / (float) ticksToMoveRight;
            float blProgress = (hardware.backLeft.getCurrentPosition() - blInitialPosition) / (float) ticksToMoveLeft;
            float brProgress = (hardware.backRight.getCurrentPosition() - brInitialPosition) / (float) ticksToMoveRight;

            double flPower = EaseInOut(flProgress, power);
            double frPower = EaseInOut(frProgress, power);
            double blPower = EaseInOut(blProgress, power);
            double brPower = EaseInOut(brProgress, power);

            hardware.SetMotorPower(hardware.frontLeft, Math.max(SLOW_SPEED_MIN, flPower));
            hardware.SetMotorPower(hardware.frontRight, Math.max(SLOW_SPEED_MIN, frPower));
            hardware.SetMotorPower(hardware.backLeft, Math.max(SLOW_SPEED_MIN, blPower));
            hardware.SetMotorPower(hardware.backRight, Math.max(SLOW_SPEED_MIN, brPower));
        } while(opMode.opModeIsActive() && IsBusy());

        hardware.StopAllMotors();

        SetRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void TurnDegrees(TurnDirection turnDirection, int degrees, double power) {
        // TODO: IF WORKS REMOVE THE PCC / 360 and put in the track distance instead
        double distance = Math.abs(degrees * 2) * (MecanumDriveHardware.PIVOT_CIRCLE_CIRCUMFERENCE / 360.0);

        if(turnDirection == TurnDirection.CLOCKWISE)
            distance = -distance;

        DriveByInches(-distance, distance, power);
    }

}
