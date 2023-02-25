package com.bravenatorsrobtoics.subcomponent;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class LiftController {

    private static final double MAX_MOTOR_VELOCITY = 2800;
    private static final double INTAKE_TARGET_OPEN_POSITION = 1.0;
    private static final double INTAKE_TARGET_CLOSED_POSITION = 0.2;

    protected final LinearOpMode opMode;

    public final DcMotorEx liftMotor;

    private final Servo intakeServo;

    private final TouchSensor magneticBottomSensor;

    public void ResetLiftEncoder() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public enum LiftStage {
        GROUND(0),
        SLIGHTLY_RAISED(210),
        LOW (575),
        MID (955),
        HIGH(1350);

        public final int encoderValue;

        LiftStage(int encoderValue) {
            this.encoderValue = encoderValue;
        }
    }

    public LiftController(LinearOpMode opMode) {
        this.opMode = opMode;

        liftMotor = opMode.hardwareMap.get(DcMotorEx.class, "lift");

        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeServo = opMode.hardwareMap.servo.get("intake");
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);
        intakeServo.setDirection(Servo.Direction.REVERSE);

        magneticBottomSensor = opMode.hardwareMap.get(TouchSensor.class, "lift-sensor");
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

    public void AsyncGoToLiftPosition(int position) {
        liftMotor.setTargetPosition(position);

        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        liftMotor.setVelocity(MAX_MOTOR_VELOCITY);
    }

    private static final double MOTOR_ZEROING_SPEED = 0.25;

    private boolean isZeroingAsync = false;

    public void Update() {
        if(isZeroingAsync && magneticBottomSensor.isPressed()) {

            liftMotor.setPower(0);

            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            isZeroingAsync = false;
        }
    }

    public void AsyncGoToLiftStage(LiftStage liftStage) {
        liftMotor.setTargetPosition(liftStage.encoderValue);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setVelocity(MAX_MOTOR_VELOCITY);
    }

    public void SetRawLiftPower(double power) {
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setVelocity(power * MAX_MOTOR_VELOCITY);
    }

}
