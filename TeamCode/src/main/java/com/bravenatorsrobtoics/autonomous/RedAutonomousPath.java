package com.bravenatorsrobtoics.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import roadrunner.drive.DriveConstants;

public class RedAutonomousPath extends AbstractAutonomousPath {

    private final LiftController liftController;

    private final Trajectory lineupInitialConeTrajectory;

    private final Trajectory comeOffPoleTrajectory;
    private final Trajectory pushConeForwardTrajectoryP1;
    private final Trajectory pushConeForwardTrajectoryP2;
    private final Trajectory driveToConeStackTrajectory;

    private final Trajectory relineUpForConeDrop;

    public RedAutonomousPath(LinearOpMode opMode, HardwareMap hardwareMap) {
        super(opMode, hardwareMap);

        // Trajectory Definitions

        liftController = new LiftController(opMode);

        lineupInitialConeTrajectory = drive.trajectoryBuilder(new Pose2d(-36, -62, Math.toRadians(90)))
                .lineToConstantHeading(new Vector2d(-36, -28))
                .splineToConstantHeading(new Vector2d(-22.5, -10.5), Math.toRadians(0))
                .build();

        comeOffPoleTrajectory = drive.trajectoryBuilder(lineupInitialConeTrajectory.end())
                .lineToConstantHeading(new Vector2d(-26, -15))
                .build();

        pushConeForwardTrajectoryP1 = drive.trajectoryBuilder(comeOffPoleTrajectory.end())
                .lineToConstantHeading(new Vector2d(-26, -9))
                .build();

        pushConeForwardTrajectoryP2 = drive.trajectoryBuilder(pushConeForwardTrajectoryP1.end())
                .lineToConstantHeading(new Vector2d(-26, -15))
                .build();

        driveToConeStackTrajectory = drive.trajectoryBuilder(pushConeForwardTrajectoryP2.end())
                .lineToSplineHeading(new Pose2d(-36 - 25, -15, Math.toRadians(180)))
                .build();

        relineUpForConeDrop = drive.trajectoryBuilder(driveToConeStackTrajectory.end())
                .lineToSplineHeading(new Pose2d(-22.5, -10.5, Math.toRadians(90)))
                .build();
    }

    @Override
    public void Run() {

        drive.setPoseEstimate(new Pose2d(-36, -62, Math.toRadians(90)));

        // Engage Cone Cam
        liftController.CloseIntake();

        WaitMillis(500);

        // Synchronized Lift to Cruise Position
        liftController.GoToLiftStage(LiftController.LiftStage.HIGH);

        WaitMillis(250);

        // Follow Trajectory to Initial Cone Drop-off
        drive.followTrajectory(lineupInitialConeTrajectory);

        WaitMillis(1000);

        // Disengage Cone Cam
        liftController.OpenIntake();

        // Cone Off Pole
        drive.followTrajectory(comeOffPoleTrajectory);

        // Drive forward to get the cone unstuck
        drive.followTrajectory(pushConeForwardTrajectoryP1);
        drive.followTrajectory(pushConeForwardTrajectoryP2);

        liftController.GoToLiftPosition(440);

        drive.followTrajectory(driveToConeStackTrajectory);

        // Hit the cone stack
        liftController.GoToLiftPosition(150);

        WaitMillis(1000);

        // Grab the Cone
        liftController.CloseIntake();

        WaitMillis(250);

        liftController.GoToLiftStage(LiftController.LiftStage.HIGH);

        WaitMillis(1000);

        drive.followTrajectory(relineUpForConeDrop);

//        WaitMillis(500);
//
//        liftController.GoToLiftPosition(440);

//        while(opMode.opModeIsActive()) {}


    }

}
