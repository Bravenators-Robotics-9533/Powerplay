package com.bravenatorsrobtoics.vision;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

public abstract class VisionPathway {

    public enum ParkingPosition {
        UNDEFINED, ONE, TWO, THREE
    }

    protected ParkingPosition parkingPosition = ParkingPosition.UNDEFINED;

    public ParkingPosition GetDetectedParkingPosition() { return this.parkingPosition; }

    public abstract void UpdateDetections();

}
