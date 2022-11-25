package com.bravenatorsrobtoics.vision;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class AprilTagVisionPathway extends VisionPathway {

    protected OpenCvCamera camera;
    protected AprilTagDetectionPipeline aprilTagDetectionPipeline;

    public static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    protected double fx = 578.272;
    protected double fy = 578.272;
    protected double cx = 402.145;
    protected double cy = 221.506;

    // UNITS ARE METERS
    protected double tagsize = 0.166;

    protected int numFramesWithoutDetection = 0;

    public final float DECIMATION_HIGH = 3;
    public final float DECIMATION_LOW = 2;
    public final float THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f;
    public final int THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4;

    protected ArrayList<AprilTagDetection> detections;

    public AprilTagVisionPathway(LinearOpMode linearOpMode) {
        super(linearOpMode);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(1280,720, OpenCvCameraRotation.UPSIDE_DOWN);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("April Tag Vision Pathway Error Code", errorCode);
                telemetry.update();
            }
        });

        detections = aprilTagDetectionPipeline.getDetectionsUpdate();
    }

    @Override
    public void UpdateDetections() {
        detections = aprilTagDetectionPipeline.getDetectionsUpdate();

        // If there's been a new frame...
        if(detections != null) {

            // If we don't see any tags
            if(detections.size() == 0) {
                numFramesWithoutDetection++;

                // If we haven't seen a tag for a few frames, lower the decimation
                // so we can hopefully pick one up if we're e.g. far back
                if(numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_LOW);
                }
            }
            // We do see tags!
            else {
                numFramesWithoutDetection = 0;

                // If the target is within 1 meter, turn on high decimation to
                // increase the frame rate
                if(detections.get(0).pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_HIGH);
                }

                for(AprilTagDetection detection : detections) {
                    switch(detection.id) {
                        case 0: parkingPosition = ParkingPosition.ONE; break;
                        case 1: parkingPosition = ParkingPosition.TWO; break;
                        case 2: parkingPosition = ParkingPosition.THREE; break;
                    }
                }
            }
        }
    }

}
