package com.bravenatorsrobtoics;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class LiftController {

    protected final LinearOpMode opMode;

    private DcMotorEx intakeMotor = null;

    public LiftController(LinearOpMode opMode) {
        this.opMode = opMode;

        intakeMotor = opMode.hardwareMap.get(DcMotorEx.class, "intake");
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void OpenIntake() {
        intakeMotor.setTargetPosition(42);
        intakeMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intakeMotor.setPower(1);
    }

    public void CloseIntake() {
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setPower(0);
    }

}
