package com.bravenatorsrobtoics.subcomponent;

import android.graphics.Color;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class PoleSensorController {

    protected final HardwareMap hardwareMap;
    protected final Telemetry telemetry;

    private RevColorSensorV3 poleSensor;

    public PoleSensorController(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.poleSensor = hardwareMap.get(RevColorSensorV3.class, "poleSensor");

        this.poleSensor.initialize();
    }

    private final float[] hsvValues = new float[3];

    private void DisplayTelemetry() {
        NormalizedRGBA colors = poleSensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(), hsvValues);

        telemetry.addData("Distance (mm)", poleSensor.getDistance(DistanceUnit.MM));
        telemetry.addLine()
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);
//        telemetry.addData("Red", poleSensor.red());
//        telemetry.addData("Green", poleSensor.green());
//        telemetry.addData("Blue", poleSensor.blue());
//        telemetry.addData("Light Detected", poleSensor.getLightDetected());
//        telemetry.addData("Raw Light Detected", poleSensor.getRawLightDetected());
//        telemetry.addData("Raw Light Detected Max", poleSensor.getRawLightDetectedMax());
//        telemetry.addData("Raw Optical", poleSensor.rawOptical());
        telemetry.update();
    }

}
