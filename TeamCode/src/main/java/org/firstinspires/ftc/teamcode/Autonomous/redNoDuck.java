package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.ChadBot;

import java.util.List;

@Autonomous(name = "redNoDuck")
public class redNoDuck extends OpMode {

    ElapsedTime timer;
    private ChadBot robot;
    private double speed;
    private int state;
    private int inttterState;

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
        switch (state) {/*

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
                int i = 0;
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
                            inttterState = 10;
                            next();
                        } else if (recognition.getLeft() <= 490) {//middle position
                            inttterState = 20;
                            next();
                        } else {//right position
                            inttterState = 30;
                            next();
                        }
                    }
                }
                // }

                break;

            case 2:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                robot.encoder(1100, 1100, speed);
                robot.getDumper().setPosition(.499999);
                robot.TheEncoder(5000, .7);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;

            case 3:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                robot.encoder(-600, 600, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;
            case 4:
                //robot.forward(1);
                robot.encoder(-300, -300, .8);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    state=inttterState;
                }
                break;
            case 10:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                telemetry.addData("elevator", robot.getElevator().getCurrentPosition());
                robot.liftDown();
                if (!robot.getElevator().isBusy()) {
                    state=31;
                }
                break;
            case 20:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                telemetry.addData("elevator", robot.getElevator().getCurrentPosition());
                robot.liftMid();
                if (!robot.getElevator().isBusy()) {
                    state=31;
                }
                break;
            case 30:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                telemetry.addData("elevator", robot.getElevator().getCurrentPosition());
                robot.liftUp();
                if (!robot.getElevator().isBusy()) {
                    state=31;
                }
                break;

            case 31:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                robot.undumpy();
                if (robot.getDumper().getPosition() >= .89)
                    next();

            case 32:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                telemetry.addData("elevator", robot.getElevator().getCurrentPosition());
                robot.TheEncoder(100, 8);
                if (!robot.getElevator().isBusy()) {
                    next();
                }

                break;
            case 33:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                //robot.encoder(300, 300, speed);
                robot.encoder(700, 700, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;
            case 34:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                robot.encoder(-630, 630, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;

            case 35:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                robot.encoder(800, 800, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;

            case 36:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                robot.encoder(500, -500, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;

            case 37:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                robot.forward(speed);
                if (timer.seconds() > 1.69)
                    next();
                break;

            case 38:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("Seconds: ", timer.seconds());
                robot.encoder(0, 0, .25);
                robot.counterClockwiseDuckyTurn();
                robot.forward(1);
                if (timer.seconds() > 4)
                    next();
                break;

            case 39:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                //robot.encoder(-120000, -120000, 1);
                robot.encoder(-500, 500, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;
            case 40:
                telemetry.addData(String.format("State (%d)", state), state);
                telemetry.addData("backRight", robot.getBackRight().getCurrentPosition());
                telemetry.addData("backLeft", robot.getBackLeft().getCurrentPosition());
                //robot.forward(1);
                //robot.encoder(-120000, -120000, 1);
                robot.encoder(-300, -300, speed);
                if (!robot.getBackLeft().isBusy() && !robot.getBackRight().isBusy()) {
                    next();
                }
                break;*/
        }
    }
}
