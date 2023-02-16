package com.bravenatorsrobtoics.subcomponent;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class LiftController {

    private static final double MAX_MOTOR_VELOCITY = 2800;
    private static final double INTAKE_TARGET_OPEN_POSITION = 1.0;
    private static final double INTAKE_TARGET_CLOSED_POSITION = 0.2;

    protected final LinearOpMode opMode;

    public final DcMotorEx liftMotor;

    private final Servo intakeServo;

    public void ResetLiftEncoder() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public enum LiftStage {
        GROUND(0),
        SLIGHTLY_RAISED(210),
        LOW (575),
        MID (955),
        HIGH(1500);

        public final int encoderValue;

        LiftStage(int encoderValue) {
            this.encoderValue = encoderValue;
        }
    }

    public LiftController(LinearOpMode opMode) {
        this.opMode = opMode;

        liftMotor = opMode.hardwareMap.get(DcMotorEx.class, "lift-right");

        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeServo = opMode.hardwareMap.servo.get("intake");
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);
    }

    public void OpenIntake() {
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);
    }

    public void ToggleIntake() {
        if(intakeServo.getPosition() == INTAKE_TARGET_CLOSED_POSITION)
            OpenIntake();
        else
            CloseIntake();
    }

    public void CloseIntake() {
        intakeServo.setPosition(INTAKE_TARGET_CLOSED_POSITION);
    }

    public void GoToLiftPosition(int position) {
        liftMotor.setTargetPosition(position);

        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        liftMotor.setTargetPositionTolerance(25);

        if(liftMotor.getCurrentPosition() > position) {
            liftMotor.setVelocity(MAX_MOTOR_VELOCITY / 4);
        } else {
            liftMotor.setVelocity(MAX_MOTOR_VELOCITY);
        }

    }

    public void GoToLiftStage(LiftStage liftStage) {
        liftMotor.setTargetPosition(liftStage.encoderValue);

        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        liftMotor.setTargetPositionTolerance(25);

        if(liftMotor.getCurrentPosition() > liftStage.encoderValue) {
            liftMotor.setVelocity(MAX_MOTOR_VELOCITY / 4);
        } else {
            liftMotor.setVelocity(MAX_MOTOR_VELOCITY);
        }

    }

    public void SetRawLiftPower(double power) {
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotor.setVelocity(power * MAX_MOTOR_VELOCITY);
    }

    public void RunSafetyChecks() {


    }

}
