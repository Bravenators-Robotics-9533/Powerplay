package com.bravenatorsrobtoics;

import com.bravenatorsrobtoics.config.Config;
import com.bravenatorsrobtoics.config.SimpleMenu;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Configuration", group="Config")
public class Configuration extends LinearOpMode {

    private static SimpleMenu menu = new SimpleMenu("Configuration Menu");

    private static final String SINGLE_CONTROLLER_OVERRIDE = "Single Controller Override";
    private static final String STARTING_POSITION = "Starting Position";

    @Override
    public void runOpMode() {
        Config config = new Config(hardwareMap.appContext);

        menu.clearOptions();

        menu.addOption(SINGLE_CONTROLLER_OVERRIDE, config.IsSingleControllerOverride());
        menu.addOption(STARTING_POSITION, Config.StartingPosition.class, config.GetStartingPosition());

        menu.setGamepad(gamepad1);
        menu.setTelemetry(telemetry);

        waitForStart();

        while(!isStopRequested()) {
            menu.displayMenu();

            config.SetIsControllerOverride(Boolean.parseBoolean(menu.getCurrentChoiceOf(SINGLE_CONTROLLER_OVERRIDE)));
            config.SetStartingPosition(Config.StartingPosition.toStartingPosition(menu.getCurrentChoiceOf(STARTING_POSITION)));

            sleep(50); // Keep the processor from dying
        }

        config.Save();
    }
}
