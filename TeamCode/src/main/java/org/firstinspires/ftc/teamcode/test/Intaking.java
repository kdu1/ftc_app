package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.Arm;
import org.firstinspires.ftc.teamcode.subsystem.LandingLiftLeadScrew;

@TeleOp(name="Intake", group="test")
@Disabled

public class Intaking extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    Servo intake;

    @Override
    public void runOpMode() {
        //initialization
        intake = hardwareMap.get(Servo.class, "intake");

        //arm.v_position = 0;
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Intake ", "Vex motor 393");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //boolean field = false; //regular run by default
        while (opModeIsActive() ) {
            //----------------------------------------------------------------------------
            //
            if (gamepad2.right_stick_y > 0.1) {
                intake.setPosition(0.75);
            }
            else if (gamepad2.right_stick_y < -0.1) {
                intake.setPosition(0.25);
            }
            else {
                intake.setPosition(0.5);
            }
            telemetry.addData("intake position: ", intake.getPosition());
            telemetry.update();

        }

    }

}



