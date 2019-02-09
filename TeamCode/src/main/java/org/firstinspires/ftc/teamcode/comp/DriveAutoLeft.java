package org.firstinspires.ftc.teamcode.comp;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystem.Arm;
import org.firstinspires.ftc.teamcode.subsystem.DriveMecanumWW;
import org.firstinspires.ftc.teamcode.subsystem.IMU;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSlide;
import org.firstinspires.ftc.teamcode.subsystem.LandingLiftLeadScrew;
import org.firstinspires.ftc.teamcode.subsystem.SamplingTensorFlow;
import org.firstinspires.ftc.teamcode.subsystem.Shuttle;

import java.util.Stack;

@Autonomous(name="Drive Auto Left", group="comp")
//@Disabled
public class DriveAutoLeft extends LinearOpMode {
    //in the order of activation
    private SamplingTensorFlow gold;    //camera on the recognize the gold
    //private Arm arm;                    //arm ready to sampling
    private LandingLiftLeadScrew lift;  //lift expand to land
    private IMU imu;                    //gyro ready to nevigate
    private DriveMecanumWW tank;        //encoder drive to position
    private IntakeSlide intakeSlide;
    //private Servo teamMarker = null;    //team marker only shows up here once
    private ElapsedTime runtime = new ElapsedTime();
    private Servo marker;
    //private Servo parker;
    private Shuttle shuttle;

    //private Servo sampleDown;
    //private Servo sampleTurn;
    //private ColorSensor right;
    //private ColorSensor center;

    private static final double LANDING_SPEED = 1.0;
    private static final double FORWARD_SPEED = 0.9;
    private static final double STRAFE_SPEED = 0.6;
    private static final double TURN_SPEED = 0.8;

    private String goldLocation = "UNKNOWN";

    @Override
    public void runOpMode() {
        telemetry.addData("Status ", "Starting up...");
        telemetry.update();
        // Initialize hardware ------------------------------------------------------------------
        gold = new SamplingTensorFlow(telemetry, hardwareMap, true);
        //arm = new Arm(telemetry, hardwareMap);
        lift = new LandingLiftLeadScrew(telemetry, hardwareMap);
        imu = new IMU(telemetry, hardwareMap);
        imu.start(0.0);
        tank = new DriveMecanumWW(telemetry, hardwareMap);
        intakeSlide = new IntakeSlide(hardwareMap, telemetry);
        shuttle = new Shuttle(hardwareMap, telemetry);
        marker = hardwareMap.get(Servo.class, "marker");
        //parker = hardwareMap.get(Servo.class, "parker");
        //right = hardwareMap.get(ColorSensor.class, "right");
        //center = hardwareMap.get(ColorSensor.class, "center");
        //sampleDown = hardwareMap.get(Servo.class, "sampleDown");
        //sampleTurn = hardwareMap.get(Servo.class, "sampleTurn");

        //teamMarker = hardwareMap.get(Servo.class, "teamMarker");
        //telemetry.addData("Status ", "Starting up...");
        //telemetry.addData("arm ", "v out h %7d %7d ", arm.v_position, arm.c_position);
        //telemetry.addData("lift ", "pos %5.2f ", lift.position);
        //telemetry.addData("imu ", "degree %5.2f ", imu.theta());
        //telemetry.update();

        tank.init();
        intakeSlide.init();
        shuttle.init();
        marker.setPosition(0);

        double timeout;

        gold.initialize();
        telemetry.update();
        gold.activate();

        Stack<Byte>[] detectionBuckets = new Stack[3];
        detectionBuckets[0] = new Stack<Byte>();
        detectionBuckets[1] = new Stack<Byte>();
        detectionBuckets[2] = new Stack<Byte>();

        int currentStack = 3;
        int stacksize = 1000;

        //gold.toggleFlash();
        //Effectively waitforstart???
        while (!opModeIsActive() && !isStopRequested()) {
            telemetry.addData("Status ", "READY");
            if (detectionBuckets[currentStack % 3].size() < stacksize) {
                //Byte legend: 0 = center, 1 = left, 2 = right, 3 = unknown, ((4 = logical error))
                detectionBuckets[currentStack % 3].push(detectionToByteDef(gold.getPosition()));
            } else {
                //gold.toggleFlash();
                gold.deactivate();
                sleep(20);
                gold.activate();
                detectionBuckets[(currentStack - 1) % 3].clear();
                currentStack++;
                //gold.toggleFlash();
            }
            telemetry.addData("currStackSize", detectionBuckets[currentStack % 3].size());
            telemetry.addData("currentStack ", currentStack % 3);
            //if (detectionBuckets[currentStack-1].size() != 0)
            //telemetry.addData("currentStack detection ", detectionBuckets[currentStack-1].peek());
            telemetry.update();
            sleep(2);
        }
        gold.shutdown();

        int[] counts = {0, 0, 0};
        if (detectionBuckets[currentStack % 3].size() > stacksize/2) {
            for (Byte byteDefDetection : detectionBuckets[currentStack % 3]) {
                counts = countDetects(counts, byteDefDetection);
            }
        } else {
            for (Byte byteDefDetection : detectionBuckets[(currentStack - 1) % 3]) {
                counts = countDetects(counts, byteDefDetection);
            }
        }

        goldLocation = countsToDetection(counts);

        //parker.setPosition(1);
        marker.setPosition(0);
        // Landinging -----------------------------------------------------------------------------
        runtime.reset();
        timeout = 1.0; // 8.5; not doing the landing for testing purposes
        lift.reset(LANDING_SPEED); //lift goes up
        while (opModeIsActive() && (runtime.seconds() < timeout)) {
            telemetry.addData("Landing Fast: ", "%2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        lift.stop();

        runtime.reset();
        timeout = 1.0; // 1.0;
        lift.reset(LANDING_SPEED / 2);
        while (opModeIsActive() && runtime.seconds() < timeout) {
            telemetry.addData("Landing Slow: ", "%2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        lift.stop();

        runtime.reset();
        tank.resetEncoder();
        tank.encoderMode();
        timeout = 1; //1.5;
        tank.encoderDrive(FORWARD_SPEED, 5.50, runtime, timeout, true);
        tank.stop();
        tank.resetEncoder();

        runtime.reset();
        tank.encoderMode(); //reset from runToPosition mode
        timeout = 1.5;
        tank.encoderStrafe(STRAFE_SPEED, -28.0, runtime, timeout, false);
        tank.stop();



        /*
        //After escaping from lander reset to 0
        runtime.reset();
        tank.resetEncoder();
        tank.encoderMode(); //reset from runToPosition mode
        timeout = 1;
        tank.imuTurn(imu, TURN_SPEED, 0, runtime, timeout);
        while (opModeIsActive() && (runtime.seconds() < timeout)) {
            telemetry.addData("Landing Adjustment", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        tank.stop();
        */

        double goldMove;
        switch (goldLocation) {
            case "RIGHT": goldMove = 27.0; break;
            case "CENTER": goldMove = 13.0; break;
            default: goldMove = -6.0; break;
        }

        // Gold ramming 1, position to gold ------------------------------------------
        runtime.reset();
        timeout = 2.0;
        tank.encoderDrive(FORWARD_SPEED, -goldMove, runtime, timeout, true);
        tank.stop();

        // Gold ramming 2, ram gold ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.7; //1.0;
        tank.encoderStrafe(STRAFE_SPEED, -12.0, runtime, timeout, true);
        tank.stop();

        // Gold ramming 3, retreat ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.7; //1.0;
        tank.encoderStrafe(STRAFE_SPEED, 13.0, runtime, timeout, true);
        tank.stop();

        // Gold ramming 4 reposition ---------------------------------------

        runtime.reset();
        timeout = 0.5; //3.0;
        tank.imu2StepTurn(imu, TURN_SPEED, TURN_SPEED / 3, 0, runtime, timeout);

        // Driving to depot: drive forward for 30 inches ------------------------------------------
        runtime.reset();
        timeout = 3.0; //5.0;
        tank.encoderDrive(FORWARD_SPEED, 30.00 + goldMove, runtime, timeout, true);
        tank.stop();


        // Driving to depot: rotate right for 135 degree seconds ----------------------------------
        runtime.reset();
        timeout = 6.0; //3.0;
        tank.imu2StepTurn(imu, TURN_SPEED, TURN_SPEED / 3, 45, runtime, timeout);

        // Driving to depot: sneaky up on wall ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.5; //1.0;
        tank.encoderStrafe(STRAFE_SPEED/1.3, -16.0, runtime, timeout, true);
        tank.stop();

        // Driving to depot: antisneak ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.5; //1.0;
        tank.encoderStrafe(STRAFE_SPEED, 3.0, runtime, timeout, true);
        tank.stop();

        // Reposition ----------------------------------
        runtime.reset();
        timeout = 1.0; //3.0;
        tank.imu2StepTurn(imu, TURN_SPEED,TURN_SPEED/3, 45, runtime, timeout);

        // Driving to depot:  Drive Backwards for 48 inches ---------------------------------------
        runtime.reset();
        timeout = 5.0; // 6.0;
        tank.encoderDrive(FORWARD_SPEED, 38.00, runtime, timeout, true);
        tank.stop();
        tank.resetEncoder();

        //Drop -------
        marker.setPosition(0.6);
        sleep(500);

        // Parking encoder drive 1. -----------------------------------------------------------------
        runtime.reset();
        timeout = 5.0; //5.0;
        tank.encoderDrive(FORWARD_SPEED, -6.00, runtime, timeout, true);

        tank.stop();
        tank.encoderMode();

        // Parking 2: sneaky up on wall ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.5; //1.0;
        tank.encoderStrafe(STRAFE_SPEED/1.3, -10, runtime, timeout, true);
        tank.stop();

        // Parking 3: antisneak ------------------------------------------
        runtime.reset();
        tank.encoderMode();
        timeout = 1.5; //1.0;
        tank.encoderStrafe(STRAFE_SPEED, 1.5, runtime, timeout, true);
        tank.stop();

        // Parking encoder drive 4. -----------------------------------------------------------------
        runtime.reset();
        timeout = 5.0; //5.0;
        tank.encoderDrive(FORWARD_SPEED, -50.00, runtime, timeout, true);

        tank.stop();
        tank.encoderMode();


        runtime.reset();
        timeout = 0.3;
        while (opModeIsActive() && runtime.seconds() < timeout)  {
            intakeSlide.downFlip();
        }

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    public Byte detectionToByteDef(String detection) {
        switch (detection) {
            case "CENTER":
                return new Byte((byte) 0);
            case "LEFT":
                return new Byte((byte) 1);
            case "RIGHT":
                return new Byte((byte) 2);
            case "UNKNOWN":
                return new Byte((byte) 3);
        }
        return new Byte((byte) 4);
    }

    public int[] countDetects(int[] incounts, Byte bytedef) {
        int[] counts = incounts;
        switch (bytedef) {
            case 0:
                counts[1]++;
                break;
            case 1:
                counts[0]++;
                break;
            case 2:
                counts[2]++;
            default:
                break;
        }
        return counts;
    }

    public String countsToDetection(int[] counts) {
        if (counts[0] > counts[1] && counts[0] > counts[2]) {
            return "LEFT";
        } else if (counts[1] > counts[0] && counts[1] > counts[2]) {
            return "CENTER";
        } else if (counts[2] > counts[0] && counts[2] > counts[1]) {
            return "RIGHT";
        }
        return "UNKNOWN";
    }
}
/*
    public void sampleColor() {
        String out = "";
        float CenterhsvValues[] = {0F, 0F, 0F};
        float RighthsvValues[] = {0F, 0F, 0F};
        double SCALE_FACTOR = 255;
        Color.RGBToHSV((int) (center.red() * SCALE_FACTOR),
                (int) (center.green() * SCALE_FACTOR),
                (int) (center.blue() * SCALE_FACTOR),
                CenterhsvValues);
        Color.RGBToHSV((int) (right.red() * SCALE_FACTOR),
                (int) (right.green() * SCALE_FACTOR),
                (int) (right.blue() * SCALE_FACTOR),
                RighthsvValues);

        if (Math.abs(CenterhsvValues[1]) < 0.3)
            telemetry.addData("Center Silver: Sat is ", CenterhsvValues[1]);
        else if (Math.abs(CenterhsvValues[1]) > 0.6)
            telemetry.addData("Center gold: Sat is ", CenterhsvValues[1]);

        if (Math.abs(RighthsvValues[1]) < 0.3)
            telemetry.addData("Center Silver: Sat is ", RighthsvValues[1]);
        else if (Math.abs(RighthsvValues[1]) > 0.6)
            telemetry.addData("Center gold: Sat is ", RighthsvValues[1]);

        /*telemetry.addData("Center Red ", center.red());
        telemetry.addData("Center Green ", center.green());
        telemetry.addData("Center Blue ", center.blue());
        telemetry.addData("Center Alpha ", center.alpha());
        telemetry.addData("Center Hue", CenterhsvValues[0]);
        telemetry.addData("Center Sat", CenterhsvValues[1]);
        telemetry.addData("Center Val", CenterhsvValues[2]);
        telemetry.addData("Right Red ", right.red());
        telemetry.addData("Right Green ", right.green());
        telemetry.addData("Right Blue ", right.blue());
        telemetry.addData("Right Alpha ", right.alpha());
        telemetry.addData("Right Hue", RighthsvValues[0]);
        telemetry.addData("Right Sat", RighthsvValues[1]);
        telemetry.addData("Right Val", RighthsvValues[2]);
        telemetry.update(); */

