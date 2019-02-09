/*package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.DriveMecanumWW;
import org.firstinspires.ftc.teamcode.subsystem.IMU;
import org.firstinspires.ftc.teamcode.util.AndyMarkMotor;

@Autonomous(name="Drive Only", group="test")
@Disabled
public class DriveOnly extends LinearOpMode {

    private DriveMecanumWW tank;
    private ElapsedTime runtime = new ElapsedTime();
    private IMU imu;

    static final double     LANDING_SPEED = 0.3;
    static final double     FORWARD_SPEED = 0.4;
    static final double     STRAFE_SPEED  = 0.3;
    static final double     TURN_SPEED    = 0.7;

    @Override
    public void runOpMode() throws InterruptedException { //need throw for landing() method
        tank = new DriveMecanumWW(telemetry, hardwareMap);
        imu = new IMU(telemetry, hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Start the logging of measured acceleration
        imu.start(0.0);

        // Step through each leg of the path, ensuring that the Auto mode has not been stopped along the way
        // step 1: recognize the gold

        // step 2: get rid of the gold

        // step 3: landing total 10 seconds

/*
        tank.encoderDrive(LANDING_SPEED, -6.00);
        telemetry.update();
//what if cable is broken??? should we add imu?
        while (opModeIsActive() && tank.isBusy()) {
            telemetry.addData("Landing 3: ", "%2.5f S Elapsed", runtime.seconds());
            //AndyMarkMotor.composeTelemetry(telemetry);
            telemetry.update();
        }
        tank.stop();
        tank.resetEncoder();
        sleep(1000);


        // Step 4:  strafe right for 1 seconds
        tank.drive(0.0, -STRAFE_SPEED, 0.0);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 1.5)) {
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        sleep(1000);
*/
        // Step 4:  Drive Backwards for 48 inches
      /*  tank.encoderDrive(FORWARD_SPEED, -41.00);
        runtime.reset();
        while (opModeIsActive() && tank.isBusy()) {
            telemetry.addData("Path to depot 3: ", "%2.5f S Elapsed", runtime.seconds());
            AndyMarkMotor.composeTelemetry(telemetry);
            telemetry.update();
        }
        tank.stop();
        //tank.resetEncoder();
        tank.encoderMode();
        sleep(100);

        // Step 4:  rotate for 1 seconds
        double angle1 = imu.theta();
        double angle2 = angle1;
        tank.drive( 0.0, 0., TURN_SPEED);
        //runtime.reset();
        while (opModeIsActive() && Math.abs(angle2 - angle1) < 130) {
            angle2 = imu.theta();
            telemetry.addData("Path to depot 2", "Leg 2: %2.5f %2.5f", angle1, angle2);
            telemetry.update();
        }
        tank.stop();
        telemetry.addData("Path to depot 2", "Leg 2: %2.5f %2.5f", angle1, angle2);
        telemetry.update();
        sleep(6000);

        // Step 4:  Drive Backwards for 48 inches
        tank.encoderDrive(FORWARD_SPEED, 48.00);
        runtime.reset();
        while (opModeIsActive() && tank.isBusy()) {
            telemetry.addData("Path to depot 3: ", "%2.5f S Elapsed", runtime.seconds());
            AndyMarkMotor.composeTelemetry(telemetry);
            telemetry.update();
        }
        tank.stop();
        //tank.resetEncoder();
        tank.encoderMode();
        sleep(100);

        // Step 5:  place team marker
        //

        // Step 6: park encoder drive.
        runtime.reset();
        tank.encoderDrive(FORWARD_SPEED, -72.00);
        while (opModeIsActive() && (tank.isBusy())) {
            telemetry.addData("Parking ", "Leg 3: %2.5f S Elapsed", runtime.seconds());
            AndyMarkMotor.composeTelemetry(telemetry);
            telemetry.update();
        }
        tank.stop();
        tank.encoderMode();

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);

    }
}
*/
