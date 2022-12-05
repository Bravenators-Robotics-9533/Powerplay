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

    public void ResetLiftEncoder() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public enum LiftStage {
        GROUND(0),
        SLIGHTLY_RAISED((int) (500 * .33)),
        LOW ((int) (4300 * .33)),
        MID ((int) (7150 * .33)),
        HIGH((int) (7400 * .33));

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

    public void SetRawLiftPower(double power) {
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(power);
    }

    public int GetLiftCurrentPosition() { return this.liftMotor.getCurrentPosition(); }

    public boolean IsLiftBusy() { return this.liftMotor.isBusy(); }

    public void Update() {}

}
