package com.bravenatorsrobotics.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    
    private static final double DROP_SECONDS = 0.75;

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity botEntity = new DefaultBotBuilder(meepMeep)
                .setConstraints(52.56702632542596, 52.56702632542596, 1.6772617340087892, Math.toRadians(199.46150662251654), 14.75)
                .setDimensions(15, 16)
                .followTrajectorySequence(drive ->
                    drive.trajectorySequenceBuilder(new Pose2d(-36, -62, Math.toRadians(90)))
                            .lineToConstantHeading(new Vector2d(-36, -20))
                            .splineToConstantHeading(new Vector2d(-24, -12), Math.toRadians(0))

                            // Cycle
                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-50, -12, Math.toRadians(180)))
                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-24, -12, Math.toRadians(90)))

                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-50, -12, Math.toRadians(180)))
                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-24, -12, Math.toRadians(90)))

                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-50, -12, Math.toRadians(180)))
                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-24, -12, Math.toRadians(90)))

                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-50, -12, Math.toRadians(180)))
                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-24, -12, Math.toRadians(90)))

                            .waitSeconds(DROP_SECONDS)
                            .lineToLinearHeading(new Pose2d(-50, -12, Math.toRadians(180)))
                            .waitSeconds(DROP_SECONDS)

                            .lineToLinearHeading(new Pose2d(-36, -12, Math.toRadians(90)))

                            .lineToConstantHeading(new Vector2d(-36, -34))

                            .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(botEntity)
                .start();
    }

}