package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name = "chadDriver")
public class chadDriver extends OpMode{
    private ChadBot robot;
    private double speed;

    public void init() {
        robot = new ChadBot();
        robot.init(hardwareMap);
        speed = 1;
    }

    public void loop() {
        // Routines separated into separate functions
        updateDriver();
        updateCodriver();
        //feedback(telemetry);
    }


    /**
     * Process gamepad1 (driver) controls
     */
    private void updateDriver() {

        // Set drive speed
        speed = .75;

        if (gamepad1.left_bumper)
            speed -= .25;
        if (gamepad1.right_bumper)
            speed += .25;

        // direction controls
        if (gamepad1.dpad_up) robot.forward(speed*.5);
       // else if (gamepad1.dpad_right) robot.right(speed*.5);
        else if (gamepad1.dpad_down) robot.backward(speed*.5);
        //else if (gamepad1.dpad_left) robot.left(speed*.5);
        else if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0) {
            if (Math.abs(gamepad1.left_stick_y) > Math.abs(gamepad1.left_stick_x)) {
                if (gamepad1.left_stick_y > 0.1) {
                    //forward
                    robot.backward(speed);
                } else {
                    //backwards
                    robot.forward(speed);
                }
            } else if (gamepad1.left_stick_x > -0.1) {
                //right
             //   robot.right(speed);
            } else {
                //left
              //  robot.left(speed);
            }
        }
        else {
            robot.stop();
        }

        //spin
        if(gamepad1.right_stick_x>0.1){
            robot.spinright();
        }
        else if(gamepad1.right_stick_x<-0.1){
            robot.spinleft();
        }

        if (gamepad1.a){
            robot.dumpy();
        }
        if(gamepad1.b){
            robot.undumpy();
        }
        if (gamepad1.y){
            robot.duckkyturningwheeelthing();
        }
        if(gamepad1.x){
            robot.STOPduckkyturningwheeelthing();
        }

    }
    /**
     * Process gamepad2 (codriver) controls
     *
     */

    private void updateCodriver() {

        if(gamepad2.dpad_left){

        }
        else if (gamepad2.dpad_right){

        }

        if(gamepad2.a){
            robot.liftBot();
        }
        if (gamepad2.b){
            robot.liftMid();
        }
        if(gamepad2.y){
            robot.liftTop();
        }

        if (gamepad2.dpad_up){

        }
        else if(gamepad2.dpad_down){

        }
    }



}