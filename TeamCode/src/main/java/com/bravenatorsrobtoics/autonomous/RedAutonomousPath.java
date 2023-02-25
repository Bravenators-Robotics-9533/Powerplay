package com.bravenatorsrobtoics.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.bravenatorsrobtoics.vision.VisionPathway;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import roadrunner.trajectorysequence.TrajectorySequence;

public class RedAutonomousPath extends AbstractAutonomousPath {

    private final LiftController liftController;

    private final Pose2d startPosition = new Pose2d(-36, -62, Math.toRadians(90));
    private final TrajectorySequence trajectorySequence;

    public RedAutonomousPath(LinearOpMode opMode, MecanumDriver legacyDriver, HardwareMap hardwareMap) {
        super(opMode, legacyDriver, hardwareMap);

        this.liftController = new LiftController(opMode);

        this.trajectorySequence = drive.trajectorySequenceBuilder(startPosition)
                .addTemporalMarker(liftController::CloseIntake) // Close Intake
                .waitSeconds(0.75) // Wait for the cone to be grabbed
                .addTemporalMarker(() -> liftController.AsyncGoToLiftStage(LiftController.LiftStage.HIGH)) // Lift to high
                .waitSeconds(0.25) // Wait for the cone to be lifted off the ground

                // Line up to the first cone drop
                .splineToConstantHeading(new Vector2d(-12, -60), Math.toRadians(0))
                .lineToConstantHeading(new Vector2d(-12, -28))
                .splineToConstantHeading(new Vector2d(-23, -9f), Math.toRadians(180))
                .waitSeconds(0.25)
                .lineToConstantHeading(new Vector2d(-23, -7.5f))

                // Wait for lift to stop shaking
                .waitSeconds(1)

                // Drop the cone
                .addTemporalMarker(liftController::OpenIntake)

                .waitSeconds(0.5) // Wait for cone to stop

                // Back away from pole
                .lineToConstantHeading(new Vector2d(-26, -17))

                // Lower the lift
                .addTemporalMarker(() -> liftController.AsyncGoToLiftPosition(440))

                // Drive to cone stack
                .lineToSplineHeading(new Pose2d(-63, -15, Math.toRadians(180)))

                // Lower the lift into the cone stack
                .addTemporalMarker(() -> liftController.AsyncGoToLiftPosition(150))
                .waitSeconds(1) // Wait for the lift to go down

                // Grab cone
                .addTemporalMarker(liftController::CloseIntake)
                .waitSeconds(0.25) // Wait for cone to be grabbed

                // Start lifting the lift to high async
                .addTemporalMarker(() -> liftController.AsyncGoToLiftStage(LiftController.LiftStage.HIGH))
                .waitSeconds(1) // Wait for cone to be clear of cone stack (so as to not knock over the cones)

                // Reline up for the cone grab
                .lineToSplineHeading(new Pose2d(-24, -7.5, Math.toRadians(90)))
                .forward(3.5)

                .waitSeconds(0.5)
                .addTemporalMarker(liftController::OpenIntake)
                .back(3.5)

                // Back away from pole
                .lineToConstantHeading(new Vector2d(-26, -10))

                // Lower the lift
                .addTemporalMarker(() -> liftController.AsyncGoToLiftStage(LiftController.LiftStage.SLIGHTLY_RAISED))

                .strafeRight(12)

                .back(22)

                .build();
    }

    @Override
    public void Run(VisionPathway.ParkingPosition parkingPosition) {
        drive.setPoseEstimate(startPosition);
        drive.followTrajectorySequenceAsync(trajectorySequence);

        while(opMode.opModeIsActive() && drive.isBusy()) {
            drive.update();
        }

        Trajectory trajectory = drive.trajectoryBuilder(trajectorySequence.end())
                .strafeLeft(50)
                .build();

        Trajectory trajectoryHalf = drive.trajectoryBuilder(trajectorySequence.end())
                .strafeLeft(26)
                .build();

        switch (parkingPosition) {
            case ONE:
                drive.followTrajectory(trajectory);
                break;
            case TWO:
                drive.followTrajectory(trajectoryHalf);
                break;
            case THREE:
            case UNDEFINED:
                break;
        }
    }

}
