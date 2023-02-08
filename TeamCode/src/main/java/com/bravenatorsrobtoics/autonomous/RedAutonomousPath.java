package com.bravenatorsrobtoics.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RedAutonomousPath extends AbstractAutonomousPath {

    private Trajectory lineupInitialConeTrajectory;

    public RedAutonomousPath(HardwareMap hardwareMap) {
        super(hardwareMap);

//        lineupInitialConeTrajectory = drive.trajectoryBuilder(new Pose2d())
    }

    @Override
    public void Run() {

    }

}
