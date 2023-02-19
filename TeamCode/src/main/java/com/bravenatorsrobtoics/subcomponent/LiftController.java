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

    public final DcMotorEx liftMotorRight;
    public final DcMotorEx liftMotorLeft;

    private final Servo intakeServo;

    private final TouchSensor magneticBottomSensor;

    public void ResetLiftEncoder() {
        liftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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

        liftMotorRight = opMode.hardwareMap.get(DcMotorEx.class, "lift-right");
        liftMotorLeft = opMode.hardwareMap.get(DcMotorEx.class, "lift-left");

        liftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        liftMotorRight.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotorLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeServo = opMode.hardwareMap.servo.get("intake");
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);

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
        liftMotorLeft.setTargetPosition(position);
        liftMotorRight.setTargetPosition(position);

        liftMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        liftMotorRight.setVelocity(MAX_MOTOR_VELOCITY);
        liftMotorLeft.setVelocity(MAX_MOTOR_VELOCITY);
    }

    private static final double MOTOR_ZEROING_SPEED = 0.25;

    public void SyncZeroOutLift() {
        liftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotorLeft.setVelocity(-MOTOR_ZEROING_SPEED * MAX_MOTOR_VELOCITY);
        liftMotorRight.setVelocity(-MOTOR_ZEROING_SPEED * MAX_MOTOR_VELOCITY);

        while(!magneticBottomSensor.isPressed() && opMode.opModeIsActive()) {}

        liftMotorLeft.setPower(0);
        liftMotorRight.setPower(0);

        liftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private boolean isZeroingAsync = false;

    public void AsyncZeroOutLift() {
        liftMotorLeft.setVelocity(-MOTOR_ZEROING_SPEED * MAX_MOTOR_VELOCITY);
        liftMotorRight.setVelocity(-MOTOR_ZEROING_SPEED * MAX_MOTOR_VELOCITY);

        isZeroingAsync = true;
    }

    public void Update() {
        if(isZeroingAsync && magneticBottomSensor.isPressed()) {

            liftMotorLeft.setPower(0);
            liftMotorRight.setPower(0);

            liftMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            liftMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            isZeroingAsync = false;
        }
    }

    public void AsyncGoToLiftStage(LiftStage liftStage) {
        liftMotorRight.setTargetPosition(liftStage.encoderValue);
        liftMotorLeft.setTargetPosition(liftStage.encoderValue);

        liftMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        liftMotorRight.setVelocity(MAX_MOTOR_VELOCITY);
        liftMotorLeft.setVelocity(MAX_MOTOR_VELOCITY);
    }

    public void SetRawLiftPower(double power) {
        liftMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftMotorLeft.setVelocity(power * MAX_MOTOR_VELOCITY);
        liftMotorRight.setVelocity(power * MAX_MOTOR_VELOCITY);
    }

    public void RunSafetyChecks() {


    }

}
