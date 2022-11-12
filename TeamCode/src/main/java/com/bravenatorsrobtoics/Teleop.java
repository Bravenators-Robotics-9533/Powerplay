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

import com.bravenatorsrobtoics.common.FtcGamePad;
import com.bravenatorsrobtoics.config.Config;
import com.bravenatorsrobtoics.drive.MecanumDriveHardware;
import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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
public class Teleop extends LinearOpMode implements FtcGamePad.ButtonHandler {

    private static final double MAX_ROBOT_SPEED = 0.75;

    private FtcGamePad driverGamePad;
    private FtcGamePad operatorGamePad;

    // Declare OpMode members.
    private MecanumDriveHardware hardware;
    private MecanumDriver driver;
    private LiftController liftController;

    private boolean shouldUseMasterController = false;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        Config config = new Config(hardwareMap.appContext);
        shouldUseMasterController = config.IsSingleControllerOverride();

        hardware = new MecanumDriveHardware(hardwareMap);
        driver = new MecanumDriver(this, hardware);
        liftController = new LiftController(this);

        driverGamePad = new FtcGamePad("Driver GamePad", gamepad1, this::OnDriverGamePadChange);
        operatorGamePad = new FtcGamePad("Operator GamePad", gamepad2, this::OnOperatorGamePadChange);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            HandleDrive();

            driverGamePad.update();

            if(!shouldUseMasterController)
                operatorGamePad.update();

            // Update Controllers
            liftController.Update();
        }

    }

    private void HandleDrive() {
        double v = -Math.pow(gamepad1.left_stick_y, 3);
        double h = Math.pow(gamepad1.left_stick_x, 3) - Math.pow(driverGamePad.getLeftTrigger(), 3)
                    + Math.pow(driverGamePad.getRightTrigger(), 3);
        double r = Math.pow(gamepad1.right_stick_x, 3);

        v = Range.clip(v, -MAX_ROBOT_SPEED, MAX_ROBOT_SPEED);
        h = Range.clip(h, -MAX_ROBOT_SPEED, MAX_ROBOT_SPEED);
        r = Range.clip(r, -MAX_ROBOT_SPEED, MAX_ROBOT_SPEED);

        driver.DriveByIntervals(v, h, r);
    }

    @Override
    public void gamepadButtonEvent(FtcGamePad gamepad, int button, boolean pressed) {

    }

    private void OnDriverGamePadChange(FtcGamePad gamePad, int button, boolean pressed) {
        if(shouldUseMasterController)
            OnOperatorGamePadChange(gamePad, button, pressed);
    }

    private void OnOperatorGamePadChange(FtcGamePad gamePad, int button, boolean pressed) {
        switch (button) {

            case FtcGamePad.GAMEPAD_A:
                if(pressed) {
                    liftController.OpenIntake();
                } else {
                    liftController.CloseIntake();
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
                    liftController.GoToLiftStage(LiftController.LiftStage.LOW);
                break;

            // Lift Stage Mid
            case FtcGamePad.GAMEPAD_DPAD_UP:
                if(pressed)
                    liftController.GoToLiftStage(LiftController.LiftStage.MID);
                break;

            // Lift Stage High
            case FtcGamePad.GAMEPAD_DPAD_RIGHT:
                if(pressed)
                    liftController.GoToLiftStage(LiftController.LiftStage.HIGH);
                break;

        }
    }

}
