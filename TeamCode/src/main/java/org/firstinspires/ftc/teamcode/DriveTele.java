package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystem.IMU;

@TeleOp(name="Drive Tele a", group="Dev")
//@Disabled

public class DriveTele extends LinearOpMode {
    private DriveTrain tank = new DriveTrain(DriveTrain.tankType.TANK2M, telemetry);
    private ElapsedTime runtime = new ElapsedTime();
    private IMU imu = new IMU(telemetry, hardwareMap);

    @Override
    public void runOpMode() {

        tank.init(hardwareMap);
        imu.init();

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Android", "Ready to run");    //
        telemetry.update();

        // Set up our telemetry dashboard
        imu.composeTelemetry();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Start the logging of measured acceleration
        imu.start(90);

        while (opModeIsActive() ) {
            tank.driveRobotOriented(imu, gamepad1);

            //tank.driveFieldOriented(imu, gamepad1);

        }

    }

}