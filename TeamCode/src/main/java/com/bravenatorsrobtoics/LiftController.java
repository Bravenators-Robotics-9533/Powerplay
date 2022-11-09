package com.bravenatorsrobtoics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class LiftController {

    private static final double INTAKE_TARGET_OPEN_POSITION = 0.6;

    protected final LinearOpMode opMode;

    private final DcMotorEx liftMotor;

    private final Servo intakeServo;

    public enum LiftStage {
        GROUND(0),
        LOW(200),
        HIGH(400);

        final int encoderValue;

        LiftStage(int encoderValue) {
            this.encoderValue = encoderValue;
        }
    }

    public LiftController(LinearOpMode opMode) {
        this.opMode = opMode;

        liftMotor = opMode.hardwareMap.get(DcMotorEx.class, "lift");
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        intakeServo = opMode.hardwareMap.servo.get("intake");
    }

    public void OpenIntake() {
        intakeServo.setPosition(INTAKE_TARGET_OPEN_POSITION);
    }

    public void CloseIntake() {
        intakeServo.setPosition(0);
    }

    public void GoToLiftStage(LiftStage liftStage) {
        liftMotor.setTargetPosition(liftStage.encoderValue);
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(1);
    }

    public void Update() {}

}
