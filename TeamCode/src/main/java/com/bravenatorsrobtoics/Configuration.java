package com.bravenatorsrobtoics;

import com.bravenatorsrobtoics.config.Config;
import com.bravenatorsrobtoics.config.SimpleMenu;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Configuration", group="Config")
public class Configuration extends LinearOpMode {

    private static SimpleMenu menu = new SimpleMenu("Configuration Menu");

    private static final String SINGLE_CONTROLLER_OVERRIDE = "Single Controller Override";
    private static final String STARTING_POSITION = "Starting Position";
    private static final String RED_DISTANCE_OFF_WALL = "Red Distance Off Wall";
    private static final String RED_STRAFE_DISTANCE_TO_POLE = "Red Strafe Distance To Pole";
    private static final String BLUE_DISTANCE_OFF_WALL = "Blue Distance Off Wall";
    private static final String BLUE_STRAFE_DISTANCE_TO_POLE = "Blue Strafe Distance To Pole";

    @Override
    public void runOpMode() {
        Config config = new Config(hardwareMap.appContext);

        menu.clearOptions();

        menu.addOption(STARTING_POSITION, Config.StartingPosition.class, config.GetStartingPosition());
        menu.addOption(SINGLE_CONTROLLER_OVERRIDE, config.IsSingleControllerOverride());

        menu.addOption(RED_DISTANCE_OFF_WALL, 100, 0, 0.25, (double) config.GetRedDistanceOffWall());
        menu.addOption(RED_STRAFE_DISTANCE_TO_POLE, 100, 0, 0.25, (double) config.GetRedStrafeDistanceToPole());

        menu.addOption(BLUE_DISTANCE_OFF_WALL, 100, 0, 0.25, (double) config.GetBlueDistanceOffWall());
        menu.addOption(BLUE_STRAFE_DISTANCE_TO_POLE, 100, 0, 0.25, (double) config.GetBlueStrafeDistanceToPole());

        menu.setGamepad(gamepad1);
        menu.setTelemetry(telemetry);

        waitForStart();

        while(!isStopRequested()) {
            menu.displayMenu();

            config.SetIsControllerOverride(Boolean.parseBoolean(menu.getCurrentChoiceOf(SINGLE_CONTROLLER_OVERRIDE)));
            config.SetStartingPosition(Config.StartingPosition.toStartingPosition(menu.getCurrentChoiceOf(STARTING_POSITION)));

            config.SetRedDistanceOffWall((float) Double.parseDouble(menu.getCurrentChoiceOf(RED_DISTANCE_OFF_WALL)));
            config.SetRedStrafeDistanceToPole((float) Double.parseDouble(menu.getCurrentChoiceOf(RED_STRAFE_DISTANCE_TO_POLE)));

            config.SetBlueDistanceOffWall((float) Double.parseDouble(menu.getCurrentChoiceOf(BLUE_DISTANCE_OFF_WALL)));
            config.SetBlueStrafeDistanceToPole((float) Double.parseDouble(menu.getCurrentChoiceOf(BLUE_STRAFE_DISTANCE_TO_POLE)));


            sleep(50); // Keep the processor from dying
        }

        config.Save();
    }
}
