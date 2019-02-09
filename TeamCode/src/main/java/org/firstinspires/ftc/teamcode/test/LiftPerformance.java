package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.Arm;
import org.firstinspires.ftc.teamcode.subsystem.DriveMecanumWW;
import org.firstinspires.ftc.teamcode.subsystem.LandingLiftLeadScrew;

@TeleOp(name="Lift Performance", group="test")
@Disabled

public class LiftPerformance extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    //private IMU imu;
    private LandingLiftLeadScrew lift;
    //private Arm arm;

    //private Servo intake = hardwareMap.get(Servo.class, "intake");

    @Override
    public void runOpMode() {
        //initialization
        //imu = new IMU(telemetry, hardwareMap);
        lift = new LandingLiftLeadScrew(telemetry, hardwareMap);
        //arm = new Arm(hardwareMap, telemetry);

        //arm.v_position = 0;
        // Send telemetry message to signify robot waiting;
        telemetry.addData("lift ", "position %5.2f ", lift.position);
        telemetry.update();

        // Set up our telemetry dashboard
        //imu.composeTelemetry();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //boolean field = false; //regular run by default
        while (opModeIsActive() ) {
            //----------------------------------------------------------------------------
            // y: up
            // a: down
            // controlled by min and max allowed position:
            //              dpad_up    up all the way
            //              dpad_downa down all the way
            //              x          up a little bit
            //              b          down a little bit
            //dpad free play up and down
            if (gamepad1.y) {
                lift.reset(1.0);
            }
            else if (gamepad1.a) {
                lift.reset(-1.0);
            }
            /*else if (gamepad1.x) { //a little up
                lift.up();
            }
            else if (gamepad1.b) { //a little down
                lift.down();
            }
            else if (gamepad1.dpad_up) { //a little up
                if (!GAMEPAD1_Y) { //first time press y button
                    lift.up(12.0);
                    GAMEPAD1_Y = true;
                }
                // else holding y, do nothing
            }
            else if (gamepad1.dpad_down) { //a little down
                if (!GAMEPAD1_A) { //first time press y button
                    lift.down(12.0);
                    GAMEPAD1_A = true;
                }
            }*/
            else {
                lift.stop();
            }
            telemetry.addData("lift position: ", lift.position);
/*
            //----------------------------------------------------------------------------
            //gamepad2 lift arm up and down, expand (out) and shrink (in)
            if (gamepad2.left_stick_y < -0.1) arm.move(20, gamepad2.left_stick_y); //up
            else if (gamepad2.left_stick_y > 0.1) arm.move(-20, gamepad2.left_stick_y); //down
            else if (gamepad2.left_trigger > 0.2) arm.shrink();
            else if (gamepad2.right_trigger > 0.2) arm.expand();
            else if (gamepad2.x) { //some quick action
                //shink all the way
                //arm.shrink();
                arm.up(2.0, 1270); // all the up
                arm.expand(2.0,-5722);
                arm.h_pivot.setPosition(0.0);
                //sleep(1500);
            }
            else if (gamepad2.y) {
                // "I dont actually know what this is supposed to do?" - Brian
                /*
                arm.shrink();
                sleep(500);
                arm.down(2.0, 33600);
                sleep(500);
                arm.h_pivot.setPosition(0.0);
                sleep(500);
                // expand all the way
                //arm.expand();
                //sleep(1500);

            }
            else {
                //arm.v_hold();
                //arm.v_pivot.setTargetPosition(arm.v_pivot.getCurrentPosition());
                arm.cascade.setTargetPosition(arm.cascade.getCurrentPosition());
            }

            //claw
            if (gamepad2.a){
                arm.open();
                sleep(400);
                telemetry.addData("claw open: ", arm.claw.getPower());
                arm.hold();
            }
            else if (gamepad2.b){
                arm.closed();
                sleep(400);
                telemetry.addData("claw close: ", arm.claw.getPower());
                arm.hold();
            }
            else{
                arm.hold();
                telemetry.addData("claw hold: ", arm.claw.getPower());
            }

            if (gamepad2.right_stick_x < -.2 ) {
                arm.right();
                sleep(100);
            }
            else if(gamepad2.right_stick_x >.2) {
                arm.left();
                sleep(100);
            }

            //combine action
            //if (gamepad2.dpad_up) arm.gold("CENTER", 20.0); // arm.up(3.0, 350);
            //else if (gamepad2.dpad_down) arm.down(3.0);


            telemetry.addData("arm v position: ", arm.v_position);
            telemetry.addData("arm h position: ", arm.h_position);
            telemetry.addData("arm out position: ", arm.out_position);
*/
            telemetry.update();

        }

    }

}


