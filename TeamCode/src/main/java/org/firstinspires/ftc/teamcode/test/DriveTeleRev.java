package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.DriveMecanumWW;
import org.firstinspires.ftc.teamcode.subsystem.IMU;
import org.firstinspires.ftc.teamcode.subsystem.LandingLiftREV;

@TeleOp(name="Drive Rev", group="test")
@Disabled

public class DriveTeleRev extends LinearOpMode {
    private DriveMecanumWW tank;
    private ElapsedTime runtime = new ElapsedTime();
    private IMU imu;
    private LandingLiftREV lift;

    @Override
    public void runOpMode() {

        tank = new DriveMecanumWW(telemetry, hardwareMap);
        imu = new IMU(telemetry, hardwareMap);
        lift = new LandingLiftREV(telemetry, hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Field", "Ready to run");    //
        telemetry.update();

        // Set up our telemetry dashboard
        //imu.composeTelemetry();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        boolean field = false;
        while (opModeIsActive() ) {
            //gamepad1 drive and lift:
            if (gamepad1.x) {
                field = true;
                // Start the logging of measured acceleration
                imu.start(0.0);

            }
            if (gamepad1.b) {
                field = false;
                // Start the logging of measured acceleration
                imu.stop();

            }
            if (!field) tank.driveRobotOriented(gamepad1, 0.5);
            else tank.driveFieldOriented(imu, gamepad1);

            if (gamepad1.y) lift.up();
            else if (gamepad1.a) lift.down();
            else lift.stop();

            telemetry.update();

        }

    }

}

