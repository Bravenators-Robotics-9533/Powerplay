package com.bravenatorsrobtoics.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.bravenatorsrobtoics.drive.MecanumDriver;
import com.bravenatorsrobtoics.subcomponent.LiftController;
import com.bravenatorsrobtoics.vision.VisionPathway;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BlueAutonomousPath extends AbstractAutonomousPath {

    private final Pose2d startPosition = new Pose2d(0, 0, Math.toRadians(0));

    private final LiftController liftController;

    private final Trajectory forward;
    private final Trajectory positionOne;
    private final Trajectory positionThree;

    public BlueAutonomousPath(LinearOpMode opMode, MecanumDriver legacyDriver, HardwareMap hardwareMap) {
        super(opMode, legacyDriver, hardwareMap);

        this.liftController = new LiftController(opMode);

        forward = drive.trajectoryBuilder(startPosition)
                .lineToConstantHeading(new Vector2d(26, 0))
                .build();

        positionOne = drive.trajectoryBuilder(forward.end())
                .lineToConstantHeading(new Vector2d(26, 26))
                .build();

        positionThree = drive.trajectoryBuilder(forward.end())
                .lineToConstantHeading(new Vector2d(26, -26))
                .build();
    }

    @Override
    public void Run(VisionPathway.ParkingPosition parkingPosition) {

        liftController.CloseIntake();

        WaitMillis(500);

        liftController.AsyncGoToLiftStage(LiftController.LiftStage.SLIGHTLY_RAISED);

        WaitMillis(1000);

        drive.followTrajectory(forward); // Go Forward

        switch (parkingPosition) {
            case ONE:
                drive.followTrajectory(positionOne);
                break;
            case THREE:
                drive.followTrajectory(positionThree);
                break;
            case TWO:
            case UNDEFINED:
                break;
        }

    }

}
