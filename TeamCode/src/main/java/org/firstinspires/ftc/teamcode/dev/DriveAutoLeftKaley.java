/*package org.firstinspires.ftc.teamcode.comp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.Arm;
import org.firstinspires.ftc.teamcode.subsystem.DriveMecanumWW;
import org.firstinspires.ftc.teamcode.subsystem.IMU;
import org.firstinspires.ftc.teamcode.subsystem.LandingLiftLeadScrew;
import org.firstinspires.ftc.teamcode.subsystem.SamplingTensorFlow;

@Autonomous(name="Drive Auto Left", group="comp")
@Disabled
public class DriveAutoLeftKaley extends LinearOpMode {
    //in the order of activation
    private SamplingTensorFlow gold;    //camera on the recognize the gold
    private Arm arm;                    //arm ready to sampling
    private LandingLiftLeadScrew lift;  //lift expand to land
    private IMU imu;                    //gyro ready to nevigate
    private DriveMecanumWW tank;        //encoder drive to position
    private Servo teamMarker = null;    //team marker only shows up here once
    private ElapsedTime runtime = new ElapsedTime();

    private static final double     LANDING_SPEED = 0.4;
    private static final double     FORWARD_SPEED = 1.0;
    private static final double     STRAFE_SPEED  = 0.4;
    private static final double     TURN_SPEED    = 0.4;

    private String goldLocation = "UNKNOWN";

    @Override
    public void runOpMode()  {
        // Initialize hardware ------------------------------------------------------------------
        gold = new SamplingTensorFlow(telemetry, hardwareMap);
        arm = new Arm(telemetry, hardwareMap);
        lift = new LandingLiftLeadScrew(telemetry, hardwareMap);
        imu = new IMU(telemetry, hardwareMap);
        imu.start(0.0);
        tank = new DriveMecanumWW(telemetry, hardwareMap);
        teamMarker = hardwareMap.get(Servo.class, "teamMarker");
        goldLocation = gold.getPosition();
        telemetry.addData("Init ", "Gold location ", goldLocation);
        telemetry.addData("arm ", "v out h %7d %7d ", arm.v_position, arm.c_position);
        telemetry.addData("lift ", "position %5.2f ", lift.position);
        telemetry.addData("imu ", "angle %5.2f ", imu.theta());
        //String motorType = lift.motorType(lift.lift); //Lynx is Andy Mark motor???
        //telemetry.addData("lift motor type test ", motorType);
        //motorType = arm.motorType(arm.v_pivot);
        //telemetry.addData("lift motor type test ", motorType);
        telemetry.update();

        double timeout;

        gold.initialize();
        sleep(2000); //make sure it initialized
        telemetry.addData("Status ", "READY");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY) -------------------------------------
        waitForStart();

        // Landinging -----------------------------------------------------------------------------
        runtime.reset();
        timeout = 4;
        lift.landing(timeout); //lift goes up
        while (opModeIsActive() && (runtime.seconds() < timeout)) {
            telemetry.addData("Landing: ", "$5.2f position %2.5f S Elapsed",
                    lift.position, runtime.seconds());
            telemetry.update();
        }
        lift.stop();
        sleep(100);

        runtime.reset();
        tank.encoderDrive(LANDING_SPEED, -6.00, runtime, 1.5);
        while (opModeIsActive() && tank.isBusy() && runtime.seconds() < 1) {
            telemetry.addData("Landing 2: ", "%2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        tank.resetEncoder();
        sleep(100);

        // Sampling -----------------------------------------------------------------------------
        gold.activate(); //why activate again???
        //sleep(4000);
        /*goldLocation = gold.getPosition();
        gold.status();
        telemetry.addData("Sampling Gold: ", goldLocation);
        telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
        telemetry.update();
        //sleep(10000); */
       /* runtime.reset();
        timeout = 2.0;
        while (goldLocation.equals("UNKNOWN") && opModeIsActive() && runtime.seconds() < timeout ) {
            goldLocation = gold.getPosition();
            telemetry.addData("Sampling Gold: ", goldLocation);
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            gold.status();
            telemetry.update();
        }
        gold.shutdown();

        telemetry.addData("Gold: ", goldLocation);
        telemetry.update();

        runtime.reset();
        tank.encoderMode(); //reset from runToPosition mode
        timeout = 1.5;
        tank.drive(0.0, -STRAFE_SPEED, 0.0);
        while (opModeIsActive() && (runtime.seconds() < timeout)) {
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        sleep(100);

        double goldMove = 0.0;
        if (goldLocation.equals("RIGHT") ) goldMove = 24.0;
        else if (goldLocation.equals("CENTER")) goldMove = 8.0;
        else goldMove = -8.0;

        // Gold ramming 1, position to gold ------------------------------------------
        runtime.reset();
        timeout = 3.0;
        tank.encoderDrive(FORWARD_SPEED, goldMove, runtime, timeout);
        while (opModeIsActive() && tank.isBusy() && (runtime.seconds() < timeout)) {
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        sleep(100);

        // Gold ramming 2, ram gold ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.5; //1.0;
        tank.drive(0.0, -STRAFE_SPEED, 0.0);
        while (opModeIsActive() && (runtime.seconds() < timeout)) {
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        sleep(100);

        // Gold ramming 3, retreat ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.5; //1.0;
        tank.drive(0.0, STRAFE_SPEED, 0.0);
        while (opModeIsActive() && (runtime.seconds() < timeout)) {
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        sleep(100);

        // Driving to depot: drive forward for 30 inches ------------------------------------------
        runtime.reset();
        timeout = 3.0; //5.0;
        tank.encoderDrive(FORWARD_SPEED, -38.00 - goldMove, runtime, timeout);
        while (opModeIsActive() && tank.isBusy() && (runtime.seconds() < timeout)) {
            telemetry.addData("Path to depot 1", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        sleep(100);

        // Driving to depot: rotate right for 135 degree seconds ----------------------------------
        runtime.reset();
        timeout = 5.0; //3.0;
        tank.imuTurn(imu, TURN_SPEED,-133.0, runtime, timeout);
        sleep(100);

        // Driving to depot:  Drive Backwards for 48 inches ---------------------------------------
        runtime.reset();
        timeout = 2.0; // 6.0;
        //tank.encoderDrive(FORWARD_SPEED, 40.00, runtime, timeout);
        //distance is in ticks
        //using runToPosition
        tank.driveToDepo(10);
        while (opModeIsActive() && tank.isBusy() && runtime.seconds() < timeout ) {
            telemetry.addData("Path to depot 3: ", "%2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        tank.resetEncoder();
        sleep(100);

        // Claiming:  place team marker -----------------------------------------------------------
        for (int i = 0; i < 2; i++) {
            teamMarker.setPosition(.8);
            sleep(500);
            teamMarker.setPosition(.2);
            sleep(500);
        }
        /*
        // Parking encoder drive. -----------------------------------------------------------------
        runtime.reset();
        timeout = 5.0; //5.0;
        tank.encoderDrive(FORWARD_SPEED, -72.00, runtime, timeout);
        while (opModeIsActive() && tank.isBusy() && runtime.seconds() < timeout) {
            telemetry.addData("Parking ", "Leg 3: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        tank.encoderMode();
        */
      /*  telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(100);
    }
}
*/