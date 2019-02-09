package org.firstinspires.ftc.teamcode.comp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.Arm;
import org.firstinspires.ftc.teamcode.subsystem.DriveMecanumWW;
import org.firstinspires.ftc.teamcode.subsystem.IMU;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSlide;
import org.firstinspires.ftc.teamcode.subsystem.LandingLiftLeadScrew;
import org.firstinspires.ftc.teamcode.subsystem.Shuttle;

@TeleOp(name="Drive Field", group="comp")
//@Disabled

public class DriveFieldView extends LinearOpMode {
    private DriveMecanumWW tank;

    private IntakeSlide intakeSlide;
    private Shuttle shuttle;

    private ElapsedTime runtime = new ElapsedTime();
    //private IMU imu;
    private LandingLiftLeadScrew lift;
    //private Arm arm;
    //private Servo parker = null;

    @Override
    public void runOpMode() {
        tank = new DriveMecanumWW(telemetry, hardwareMap);
        intakeSlide = new IntakeSlide(hardwareMap, telemetry);
        shuttle = new Shuttle(hardwareMap, telemetry);

        //initialization
        tank.init();
        intakeSlide.init();
        shuttle.init();

        //imu = new IMU(telemetry, hardwareMap);
        lift = new LandingLiftLeadScrew(telemetry, hardwareMap);
        //arm = new Arm(hardwareMap, telemetry);
        //parker = hardwareMap.get(Servo.class, "parker");

        //arm.v_position = 0;
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Field", "Ready to run");
        telemetry.update();

        // Set up our telemetry dashboard
        //imu.composeTelemetry();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //parker.setPosition(1);
        //boolean field = false; //regular run by default
        intakeSlide.stopIntake();
        intakeSlide.stopIntake2();
        while (opModeIsActive()) {
            //gamepad1's joysticks control drive train
            tank.driveRobotOriented(gamepad1, 0.82);

            //gamepad1's dpad controls intakeFlip
            if(gamepad1.dpad_up || gamepad2.left_stick_y < -0.2){
                intakeSlide.upFlip();
            }
            else if(gamepad1.dpad_down || gamepad2.left_stick_y > 0.2){
                intakeSlide.downFlip();
            }
            else{
                intakeSlide.stopFlip();
            }

            //left trigger expands intakeOut, right trigger shrinks it
            if(gamepad1.left_trigger > 0.2 || gamepad2.right_stick_y > 0.2){
                intakeSlide.shrink();
            }
            else if(gamepad1.right_trigger > 0.2 || gamepad2.right_stick_y < -0.2){
                intakeSlide.expand();
            }

            //gamepad1 and gamepad2's buttons control intake
            if (gamepad1.a) {
                intakeSlide.intake();
                intakeSlide.outtake2();
            } else if (gamepad1.b) {
                intakeSlide.outtake();
                intakeSlide.stopIntake2();
            } else if (gamepad1.x) {
                intakeSlide.stopIntake();
                intakeSlide.stopIntake2();
            } else if(gamepad1.y) {
                intakeSlide.outtake();
                intakeSlide.intake2();
            }

            if (gamepad2.y) {
                shuttle.setToLand();
            }
            if (gamepad2.a) {
                shuttle.setToBot();
            }
            if (gamepad2.b) {
                shuttle.boxGold();
            }
            if (gamepad2.x) {
                shuttle.boxSilver();
            }

            //gamepad1's bumpers control lift
            if(gamepad1.left_bumper){
                lift.reset(-1);
            }
            else if(gamepad1.right_bumper){
                lift.reset(1);
            }
            else{
                lift.reset(0);
            }

            //gamepad2's bumpers control shuttleOut
            if (gamepad2.left_trigger > 0.1) {
                shuttle.decrShuttleOut();
            } else if (gamepad2.right_trigger > 0.1) {
                shuttle.incrShuttleOut();
            }

            //gamepad2's dpad controls the shuttleBox
            if (gamepad2.dpad_up) {
                shuttle.incrShuttleBox();
            } else if (gamepad2.dpad_down) {
                shuttle.decrShuttleBox();
            }

            shuttle.update();

            intakeSlide.update();

            telemetry.update();
        }
    }
}

