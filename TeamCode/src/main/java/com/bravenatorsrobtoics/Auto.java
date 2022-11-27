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

import com.bravenatorsrobtoics.config.Config;
import com.bravenatorsrobtoics.drive.MecanumDriveHardware;
import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.bravenatorsrobtoics.vision.AprilTagVisionPathway;
import com.bravenatorsrobtoics.vision.VisionPathway;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


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

@Autonomous(name="Autonomous", group="Linear Opmode")
public class Auto extends LinearOpMode {

    private Config config;

    private MecanumDriveHardware hardware;
    private MecanumDriver driver;
    private LiftController liftController;

    private VisionPathway visionPathway;
    private VisionPathway.ParkingPosition parkingPosition;

    private void WaitMillis(int millis) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        while(opModeIsActive() && timer.milliseconds() <= millis);
    }

    private static final double MOVE_SPEED = 0.5;
    private static final int MOVE_WAIT_MILLIS = 500;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        config = new Config(hardwareMap.appContext);

        hardware = new MecanumDriveHardware(hardwareMap);
        hardware.SetBulkUpdateMode(LynxModule.BulkCachingMode.AUTO);
        hardware.SetZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        driver = new MecanumDriver(this, hardware);
        liftController = new LiftController(this);

        visionPathway = new AprilTagVisionPathway(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        while(!isStarted()) {
            visionPathway.UpdateDetections();
            parkingPosition = visionPathway.GetDetectedParkingPosition();
            telemetry.addData("Parking Position", visionPathway.GetDetectedParkingPosition().name());
            telemetry.update();
        }

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run the autonomous based on the config
        if(config.GetStartingPosition() == Config.StartingPosition.RED)
            RedAutonomous();
        else
            BlueAutonomous();
    }

    private void RedAutonomous() {
        liftController.CloseIntake();
        WaitMillis(750);

        liftController.GoToLiftStage(LiftController.LiftStage.SLIGHTLY_RAISED);
        WaitMillis(1000);

        driver.DriveByInches(3, MOVE_SPEED);
        WaitMillis(MOVE_WAIT_MILLIS);

        driver.StrafeByInches(-5, MOVE_SPEED / 2);
        WaitMillis(MOVE_WAIT_MILLIS);

        driver.TurnDegrees(MecanumDriver.TurnDirection.COUNTER_CLOCKWISE, 90, MOVE_SPEED);
        WaitMillis(MOVE_WAIT_MILLIS);

        driver.DriveByInches(23.25, MOVE_SPEED);
        WaitMillis(MOVE_WAIT_MILLIS);

        liftController.GoToLiftStage(LiftController.LiftStage.HIGH);
        WaitMillis(4000);

        driver.StrafeByInches(15.25, MOVE_SPEED / 2.0);
        WaitMillis(MOVE_WAIT_MILLIS);

        liftController.OpenIntake();
        WaitMillis(500);

        driver.StrafeByInches(-12.25, MOVE_SPEED / 2.0); // less
        WaitMillis(MOVE_WAIT_MILLIS);

        liftController.GoToLiftStage(LiftController.LiftStage.GROUND);
        WaitMillis(1000);

        if(parkingPosition == VisionPathway.ParkingPosition.ONE)
            driver.StrafeByInches(-30, MOVE_SPEED);
        else if(parkingPosition == VisionPathway.ParkingPosition.THREE)
            driver.StrafeByInches(27, MOVE_SPEED);
        else
            driver.DriveByInches(-3, MOVE_SPEED);

        while(opModeIsActive() && liftController.GetLiftCurrentPosition() >= LiftController.LiftStage.GROUND.encoderValue); // Wait for lift to finish
    }

    private void BlueAutonomous() {
        liftController.CloseIntake();
        WaitMillis(750);

        liftController.GoToLiftStage(LiftController.LiftStage.SLIGHTLY_RAISED);
        WaitMillis(1000);

        driver.DriveByInches(3, MOVE_SPEED);
        WaitMillis(MOVE_WAIT_MILLIS);

        driver.StrafeByInches(-5, MOVE_SPEED / 2);
        WaitMillis(MOVE_WAIT_MILLIS);

        driver.TurnDegrees(MecanumDriver.TurnDirection.COUNTER_CLOCKWISE, 90, MOVE_SPEED);
        WaitMillis(MOVE_WAIT_MILLIS);

        driver.DriveByInches(23.25, MOVE_SPEED);
        WaitMillis(MOVE_WAIT_MILLIS);

        liftController.GoToLiftStage(LiftController.LiftStage.HIGH);
        WaitMillis(4000);

        driver.StrafeByInches(-15.25, MOVE_SPEED / 2.0);
        WaitMillis(MOVE_WAIT_MILLIS);

        liftController.OpenIntake();
        WaitMillis(500);

        driver.StrafeByInches(12.25, MOVE_SPEED / 2.0);
        WaitMillis(MOVE_WAIT_MILLIS);

        liftController.GoToLiftStage(LiftController.LiftStage.GROUND);
        WaitMillis(1000);

        if(parkingPosition == VisionPathway.ParkingPosition.ONE)
            driver.StrafeByInches(-30, MOVE_SPEED);
        else if(parkingPosition == VisionPathway.ParkingPosition.THREE)
            driver.StrafeByInches(27, MOVE_SPEED);
        else
            driver.DriveByInches(-3, MOVE_SPEED);

        while(opModeIsActive() && liftController.GetLiftCurrentPosition() >= LiftController.LiftStage.GROUND.encoderValue); // Wait for lift to finish
    }

}
