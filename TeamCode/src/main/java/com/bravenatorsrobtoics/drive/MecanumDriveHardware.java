package com.bravenatorsrobtoics.drive;

import android.graphics.drawable.GradientDrawable;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.List;

public class MecanumDriveHardware {

    public static final double ENCODER_TICKS_PER_MOTOR_REV = 28;
    public static final double DRIVE_GEAR_REDUCTION = 20.0; // 20:1
    public static final double WHEEL_DIAMETER_INCHES = 3.7795;

    public static final double ENCODER_TICKS_PER_INCH = (ENCODER_TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                        (WHEEL_DIAMETER_INCHES * Math.PI);

    public static final double TRACK_DISTANCE = 12.5;
    public static final double PIVOT_CIRCLE_CIRCUMFERENCE = TRACK_DISTANCE * Math.PI;

    public static final double MAX_MOTOR_VELOCITY = 2800;

    private static final int TARGET_POSITION_TOLERANCE = (int) (ENCODER_TICKS_PER_INCH * 0.75);

    protected final HardwareMap hardwareMap;
    protected final List<LynxModule> hubsList;

    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    public BNO055IMU imu;

    public MecanumDriveHardware(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        this.hubsList = hardwareMap.getAll(LynxModule.class);

        this.frontLeft = hardwareMap.get(DcMotorEx.class, "fl");
        this.frontRight = hardwareMap.get(DcMotorEx.class, "fr");
        this.backLeft = hardwareMap.get(DcMotorEx.class, "bl");
        this.backRight = hardwareMap.get(DcMotorEx.class, "br");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        frontLeft.setTargetPositionTolerance(TARGET_POSITION_TOLERANCE);
        frontRight.setTargetPositionTolerance(TARGET_POSITION_TOLERANCE);
        backLeft.setTargetPositionTolerance(TARGET_POSITION_TOLERANCE);
        backRight.setTargetPositionTolerance(TARGET_POSITION_TOLERANCE);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode                     = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit                = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit                = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled           = false;

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void SetMotorPower(DcMotorEx motor, double power) {
        motor.setVelocity(power * MAX_MOTOR_VELOCITY);
    }

    public void SetMotorPower(DcMotorEx[] motors, double power) {
        for(DcMotorEx motor : motors)
            motor.setVelocity(power * MAX_MOTOR_VELOCITY);
    }

    public void StopAllMotors() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        frontRight.setPower(0);
    }

    public void SetZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        frontLeft.setZeroPowerBehavior(zeroPowerBehavior);
        frontRight.setZeroPowerBehavior(zeroPowerBehavior);
        backLeft.setZeroPowerBehavior(zeroPowerBehavior);
        backRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public double GetCurrentHeading() {
        return imu.getAngularOrientation().firstAngle;
    }

    public void SetBulkUpdateMode(LynxModule.BulkCachingMode cachingMode) {
        for(LynxModule hub : hubsList) {
            hub.setBulkCachingMode(cachingMode);
        }
    }

    public void ClearBulkCache() {
        for(LynxModule hub : hubsList)
            hub.clearBulkCache();
    }

}
