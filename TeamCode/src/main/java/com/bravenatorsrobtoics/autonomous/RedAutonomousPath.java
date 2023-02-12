package com.bravenatorsrobtoics.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RedAutonomousPath extends AbstractAutonomousPath {

    private final Trajectory lineupInitialConeTrajectory;

    public RedAutonomousPath(HardwareMap hardwareMap) {
        super(hardwareMap);

        // Trajectory Definitions

        lineupInitialConeTrajectory = drive.trajectoryBuilder(new Pose2d())
                .forward(48)
                .strafeRight(12)
                .build();
    }

    @Override
    public void Run() {

        // Engage Cone Cam

        // Synchronized Lift to Cruise Position

        // Async Lift to Stage 3

        // Follow Trajectory to Initial Cone Drop-off
        drive.followTrajectory(lineupInitialConeTrajectory);

        // Disengage Cone Cam



    }

}
