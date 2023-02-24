package com.bravenatorsrobtoics.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import roadrunner.trajectorysequence.TrajectorySequence;

public class RedAutonomousPath extends AbstractAutonomousPath {

    private final LiftController liftController;

    private final Pose2d startPosition = new Pose2d(-36, -62, Math.toRadians(90));
    private final TrajectorySequence trajectorySequence;

    public RedAutonomousPath(LinearOpMode opMode, HardwareMap hardwareMap) {
        super(opMode, hardwareMap);

        this.liftController = new LiftController(opMode);

        this.trajectorySequence = drive.trajectorySequenceBuilder(startPosition)
                .addDisplacementMarker(liftController::CloseIntake) // Close Intake
                .waitSeconds(0.5) // Wait for the cone to be grabbed
                .addDisplacementMarker(() -> liftController.AsyncGoToLiftStage(LiftController.LiftStage.HIGH)) // Lift to high
                .waitSeconds(0.25) // Wait for the cone to be lifted off the ground

                // Line up to the first cone drop
                .lineToConstantHeading(new Vector2d(-36, -28))
                .splineToConstantHeading(new Vector2d(-22.5, -10.0f), Math.toRadians(0))

                // Wait for lift to stop shaking
                .waitSeconds(0.5)

                // Drop the cone
                .addDisplacementMarker(liftController::OpenIntake)

                // Back away from pole
                .lineToConstantHeading(new Vector2d(-26, -15))

                // Push initial cone away from the bot
                .lineToConstantHeading(new Vector2d(-26, -9))
                .lineToConstantHeading(new Vector2d(-26, -15))

                // Lower the lift
                .addDisplacementMarker(() -> liftController.AsyncGoToLiftPosition(440))
    
                // Drive to cone stack
                .lineToSplineHeading(new Pose2d(-61, -15, Math.toRadians(180)))

                // Lower the lift into the cone stack
                .addDisplacementMarker(() -> liftController.AsyncGoToLiftPosition(150))
                .waitSeconds(1) // Wait for the lift to go down

                // Grab cone
                .addDisplacementMarker(liftController::CloseIntake)
                .waitSeconds(0.25) // Wait for cone to be grabbed

                // Start lifting the lift to high async
                .addDisplacementMarker(() -> liftController.AsyncGoToLiftStage(LiftController.LiftStage.HIGH))
                .waitSeconds(1000) // Wait for cone to be clear of cone stack (so as to not knock over the cones)

                // Reline up for the cone grab
                .lineToSplineHeading(new Pose2d(-22.5, -10.5, Math.toRadians(90)))
                .build();
    }

    @Override
    public void Run() {
        drive.setPoseEstimate(startPosition);
        drive.followTrajectorySequence(trajectorySequence);
    }

}
