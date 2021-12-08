package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.ChadBot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

@Autonomous(name = "blueDuck")
public class blueDuck extends OpMode {

    ElapsedTime timer;
    private ChadBot robot;
    private double speed;
    private int state;
    private int inttterState;

    private double ninety=1.0;//turn right 90degrees

    private static final String VUFORIA_KEY =
            "AYef6RP/////AAABmQhqgETT3Uq8mNFqAbjPOD990o1n/Osn3oBdTsKI0NXgPuXS612xYfN5Q65srnoMx2" +
                    "eKXe32WnMf6M2BSJSgoPfTZmkmujVujpE/hUrmy5p4L7CALtVoM+TDkfshpKd+LGJT834pEOYU" +
                    "qcUj+vySs3OZQNepaSflmiShfHRNVbrgjrEs1Erlg7zZzc6EQo+yvh0fFtUiQUPLCCcZEPyfnU" +
                    "4k0o8phhbR+Ca9B6dtoeNaYITGHvMmOkBLsyAnR/RQ4Xv8KpvSaSfk0PDyzCG7UsN49k055xOx" +
                    "kFI0iKYp7NMCDF+cezE80dkcnpZCzg1RpGuSpCKGuUbSkJp+q5qudl2qZfWnQntaNI0vlNKD2x1C";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker",
    };
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;
    public static final String TAG = "Vuforia VuMark Sample";

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }

    @Override
    public void init() {
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.25, 8.0 / 4.5);//change
            //can change mag later to find seomthing better- if want to test
            //dont change ratio
            robot.undumpy();
        }


        timer = new ElapsedTime();
        robot = new ChadBot();
        robot.init(hardwareMap);
        speed = .5;
        state = 0;
    }

    public void next() {
        state++;
        timer.reset();
        robot.stop();
    }

    @Override
    public void loop() {
        switch (state) {

            case 0:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.update();
                robot.stop();
                if (timer.seconds() > 0)
                    next();
                break;


            case 1:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.update();
                //if(tfod != null){
                int i=0;
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {

                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData("# Object Detected", updatedRecognitions.size());
                            // step through the list of recognitions and display boundary info.

                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            i++;
                            telemetry.update();
                            if (recognition.getLeft() <= 440) {//left postion
                                inttterState = 9;
                                next();
                            } else if (recognition.getRight() >= 470) {//right position
                                inttterState = 200;
                                next();
                            } else {//middle position
                                inttterState = 100;
                                next();
                            }
                        }
                    }
                    // }

                    break;

                    //starts of left movements
            case 2:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.update();
                robot.forward(speed);
                if (timer.seconds() > 1)
                    next();
                break;

            case 4:
                robot.spinright();
                if (timer.seconds() > ninety)
                    next();
                break;

            case 5:
                robot.forward(speed);
                if (timer.seconds() > 3)
                    next();
                break;

            case 6:
                robot.duckkyturningwheeelthing();
                if (timer.seconds() > 3) {
                    robot.STOPduckkyturningwheeelthing();
                    next();
                }
                break;

            case 7:
                robot.backward(speed);
                if (timer.seconds() > 5)
                    next();
                break;

            case 8:
                robot.spinright();
                if (timer.seconds() > ninety)
                    state=inttterState;
                break;
                //end of all positions

                case 9://start of left position
                    robot.backward(speed);
                    if (timer.seconds() > 1)
                        next();
                    break;

            case 3:
                robot.liftBot();
                if (timer.seconds() > .5)
                    next();
                break;

            case 10:
                robot.dumpy();
                if(timer.seconds() > 1)
                    next();
                break;

            case 11:
                robot.undumpy();
                if(timer.seconds() > 1)
                    next();
                break;

            case 12:
                robot.forward(speed);
                if(timer.seconds() > 1)
                    next();
                break;

            case 13:
                robot.spinright();
                if(timer.seconds() > ninety)
                    next();
                break;

            case 14:
                robot.forward(speed);
                if(timer.seconds() > 8.2)
                    next();
                break;

            // start of middle movemnets
            case 100:
                robot.backward(speed);
                if (timer.seconds() > 1)
                    next();
                break;

            case 101:
                robot.liftMid();// function name: uppiesMidddiess
                if (timer.seconds() > 1)
                    next();
                break;

            case 102:
                robot.dumpy();
                if(timer.seconds() > 1)
                    next();
                 break;

            case 103:
                 robot.undumpy();
                 if(timer.seconds() > 1)
                    next();
                 break;

            case 104:
                robot.forward(speed);
                if (timer.seconds() > 1)
                    next();
                break;


            case 105:
                robot.spinright();
                if (timer.seconds() > ninety)
                    next();
                break;

            case 106:
                robot.forward(speed);
                if(timer.seconds() > 8.2)
                    next();
                break;
                //end of position middle


            //start of right movements
            case 200:
                robot.backward(speed);
                if (timer.seconds() > 1)
                    next();
                break;

            case 201:
                robot.liftTop();
                if (timer.seconds() > 1)
                    next();
                break;

            case 202:
                //park on white tape
                robot.dumpy();
                if (timer.seconds() > 1)
                    next();
                break;


            case 203:
                //park on white tape
                robot.undumpy();
                if (timer.seconds() > 1)
                    next();
                break;


            case 204:
                //park on white tape
                robot.forward(speed);
                if (timer.seconds() > 1)
                    next();
                break;


            case 205:
                //park on white tape
                robot.spinright();
                if (timer.seconds() > ninety)
                    next();
                break;

            case 206:
                robot.forward(speed);
                if(timer.seconds() > 8.2)
                    next();
                break;
            //end of right position
                }
        }
    }
