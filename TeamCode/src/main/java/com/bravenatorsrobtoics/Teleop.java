/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bravenatorsrobtoics;

import android.widget.MultiAutoCompleteTextView;

import com.bravenatorsrobtoics.common.FtcGamePad;
import com.bravenatorsrobtoics.config.Config;
import com.bravenatorsrobtoics.drive.MecanumDriveHardware;
import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Teleop", group="Linear Opmode")
public class Teleop extends LinearOpMode {

    private static final double MAX_ROBOT_SPEED = 0.5;

    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    // Declare OpMode members.
    private MecanumDriveHardware hardware;
    private MecanumDriver driver;
    private LiftController liftController;

    private float initialHeading = 0;

    private boolean shouldUseMasterController = false;

    private double offsetHeading = 0;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        Config config = new Config(hardwareMap.appContext);
        shouldUseMasterController = config.IsSingleControllerOverride();

        hardware = new MecanumDriveHardware(hardwareMap);
        hardware.SetBulkUpdateMode(LynxModule.BulkCachingMode.MANUAL);
        hardware.SetZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        driver = new MecanumDriver(this, hardware);
        liftController = new LiftController(this);

        driverGamePad = new FtcGamePad("Driver GamePad", gamepad1, this::OnDriverGamePadChange);
        operatorGamePad = new FtcGamePad("Operator GamePad", gamepad2, this::OnOperatorGamePadChange);

        initialHeading = (float) hardware.GetCurrentHeading();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            // Clear Bulk Cache
            hardware.ClearBulkCache();

            HandleDrive();

            driverGamePad.update();
            operatorGamePad.update();

            // Update Controllers
            liftController.Update();
        }

    }

    // Field Centric Driving
    private void HandleDrive() {
        double y = Range.clip(Math.pow(-gamepad1.left_stick_y, 3), -1.0, 1.0);
        double xt = (Math.pow(gamepad1.right_trigger, 3) - Math.pow(gamepad1.left_trigger, 3)) * (shouldUseMasterController ? 0 : 1);
        double x = Range.clip(Math.pow(gamepad1.left_stick_x, 3) + xt, -1.0, 1.0) * 1.1;
        double rx = Range.clip(Math.pow(gamepad1.right_stick_x, 3), -1.0, 1.0);

        // Read inverse IMU heading, as the UMG heading is CW positive
        double botHeading = -hardware.GetCurrentHeading() + offsetHeading;

        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        hardware.SetMotorPower(hardware.frontLeft, frontLeftPower / (1 / MAX_ROBOT_SPEED));
        hardware.SetMotorPower(hardware.frontRight, frontRightPower / (1 / MAX_ROBOT_SPEED));
        hardware.SetMotorPower(hardware.backLeft, backLeftPower / (1 / MAX_ROBOT_SPEED));
        hardware.SetMotorPower(hardware.backRight, backRightPower / (1 / MAX_ROBOT_SPEED));

        telemetry.addData("IMU", -hardware.GetCurrentHeading());
        telemetry.update();
    }

    private void OnDriverGamePadChange(FtcGamePad gamePad, int button, boolean pressed) {
        if(shouldUseMasterController)
            OnOperatorGamePadChange(gamePad, button, pressed);

        if(button == FtcGamePad.GAMEPAD_BACK && pressed) {
            offsetHeading = hardware.GetCurrentHeading();
        }
    }

    private double prevRightTrigger = 0;
    private double prevLeftTrigger = 0;

    private void OnOperatorGamePadChange(FtcGamePad gamePad, int button, boolean pressed) {
        switch (button) {

            case FtcGamePad.GAMEPAD_A:
                if(pressed) {
                    liftController.ToggleIntake();
                }

                break;

            // Lift Stage Down
            case FtcGamePad.GAMEPAD_DPAD_DOWN:
                if(pressed)
                    liftController.GoToLiftStage(LiftController.LiftStage.GROUND);
                break;

            // Lift Stage Low
            case FtcGamePad.GAMEPAD_DPAD_LEFT:
                if(pressed)
                    liftController.GoToLiftStage(LiftController.LiftStage.SLIGHTLY_RAISED);
                break;

            // Lift Stage Mid
            case FtcGamePad.GAMEPAD_DPAD_UP:
                if(pressed)
                    liftController.GoToLiftStage(LiftController.LiftStage.LOW);
                break;

            // Lift Stage High
            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                if(pressed)
                    liftController.GoToLiftStage(LiftController.LiftStage.MID);
                break;
        }

        if(gamePad.getRightTrigger() != prevRightTrigger || gamePad.getLeftTrigger() != prevLeftTrigger) {
            double powerApplied = gamePad.getRightTrigger() - gamePad.getLeftTrigger();

            if(powerApplied < 0 && liftController.GetLiftCurrentPosition() <= 0)
                liftController.SetRawLiftPower(0);
            else if(powerApplied > 0 && liftController.GetLiftCurrentPosition() >= LiftController.LiftStage.HIGH.encoderValue)
                liftController.SetRawLiftPower(0);
            else
                liftController.SetRawLiftPower(powerApplied);
        }

        prevLeftTrigger = gamePad.getLeftTrigger();
        prevRightTrigger = gamePad.getRightTrigger();
    }

}
