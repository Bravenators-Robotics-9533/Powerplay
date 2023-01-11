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

    private final RevColorSensorV3 poleSensor;

    private NormalizedRGBA rgbaColors = null;
    private final float[] hsvColors = new float[3];

    public PoleSensorController(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.poleSensor = hardwareMap.get(RevColorSensorV3.class, "poleSensor");

        this.poleSensor.initialize();
    }

    public void Update() {
        rgbaColors = poleSensor.getNormalizedColors();
        Color.colorToHSV(rgbaColors.toColor(), hsvColors);
    }

    public boolean IsPoleSenseSuccess() {

        return true;

    }

    public void DisplayTelemetry() {
        telemetry.addData("Distance (mm)", poleSensor.getDistance(DistanceUnit.MM));
//        telemetry.addLine()
//                .addData("Red", "%.3f", rgbaColors.red)
//                .addData("Green", "%.3f", rgbaColors.green)
//                .addData("Blue", "%.3f", rgbaColors.blue);
//        telemetry.addLine()
//                .addData("Hue", "%.3f", hsvColors[0])
//                .addData("Saturation", "%.3f", hsvColors[1])
//                .addData("Value", "%.3f", hsvColors[2]);
//        telemetry.addData("Alpha", "%.3f", rgbaColors.alpha);
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
