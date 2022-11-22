package com.bravenatorsrobtoics.subcomponent;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

public class LiftController {

    private static final double INTAKE_TARGET_OPEN_POSITION = 0.6;

    protected final LinearOpMode opMode;

    private final DcMotorEx liftMotor;

    private final Servo intakeServo;

    public enum LiftStage {
        GROUND(0),
        SLIGHTLY_RAISED(500),
        LOW(4265),
        MID(6895),
        HIGH(6895);

        final int encoderValue;

        LiftStage(int encoderValue) {
            this.encoderValue = encoderValue;
        }
    }

    public LiftController(LinearOpMode opMode) {
        this.opMode = opMode;

        liftMotor = opMode.hardwareMap.get(DcMotorEx.class, "lift");
        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeServo = opMode.hardwareMap.servo.get("intake");
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);
    }

    public void OpenIntake() {
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);
    }

    public void ToggleIntake() {
        if(intakeServo.getPosition() == 0)
            OpenIntake();
        else
            CloseIntake();
    }

    public void CloseIntake() {
        intakeServo.setPosition(0);
    }

    public void GoToLiftStage(LiftStage liftStage) {
        liftMotor.setTargetPosition(liftStage.encoderValue);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }

    public boolean IsLiftBusy() { return this.liftMotor.isBusy(); }

    public void Update() {}

}
