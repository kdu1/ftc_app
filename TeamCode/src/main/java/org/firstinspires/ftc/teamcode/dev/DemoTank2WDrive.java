package org.firstinspires.ftc.teamcode.dev;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.DriveTank2WW;

@TeleOp(name="Demo Tank 2 Wheels", group="dev")
@Disabled

public class DemoTank2WDrive extends LinearOpMode {
    private DriveTank2WW tank;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        //initialize hardware
        tank = new DriveTank2WW(telemetry, hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Field", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //start button pressed
        while (opModeIsActive() ) {
            tank.driveRobotOriented(gamepad1);
        }
    }
}
