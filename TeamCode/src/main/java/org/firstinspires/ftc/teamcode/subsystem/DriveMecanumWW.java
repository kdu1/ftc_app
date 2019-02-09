package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Encoder;

/**
 * This is NOT an opmode.
 *
 * This class is to define hardware and behaviou of tank two wheel drive train
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Motor channel 0:  front left drive motor:    "driveFrontLeft"
 * Motor channel 1:  front Right drive motor:   "driveFrontRight"
 * Motor channel 2:  back Left drive motor:     "driveBackLeft"
 * Motor channel 3:  bacl Right drive motor:    "driveBackRight"
 *
 * The angle is multiplied by a proportional scaling constant (Kp) to scale it
 * for the speed of the robot drive. This factor is called the proportional constant
 * or loop gain. Increasing Kp will cause the robot to correct more quickly
 * (but too high and it will oscillate). Decreasing the value will cause the robot correct
 * more slowly (possibly never reaching the desired heading). This is known as proportional
 * control, and is discussed further in the PID control section of the advanced programming
 * section.
 */

public class DriveMecanumWW extends Encoder {
    /* Public OpMode members. */
    public DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    public DcMotor backRight = null;



    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;
    Telemetry telemetry;
    static final double     Kp  = 0.03;
    private double forward;
    private double strafe;
    private double rotate;

    /* Constructor */
    public DriveMecanumWW(Telemetry telemetry, HardwareMap hwMap){
        this.telemetry = telemetry;
        this.hwMap = hwMap;
    }

    /* Initialize standard Hardware interfaces */
    public void init( ) {
        // Define and Initialize Motors
        frontLeft = hwMap.get(DcMotor.class, "frontLeft");
        frontRight = hwMap.get(DcMotor.class, "frontRight");
        backLeft = hwMap.get(DcMotor.class, "backLeft");
        backRight = hwMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        //resetEncoder(); auto mode
        //encoderMode();


        stop();
    }

    public void driveRobotOriented(Gamepad gamepad1, double speed) {
        forward =  gamepad1.left_stick_y * speed;
        strafe  =  -gamepad1.left_stick_x * speed;
        rotate  = gamepad1.right_stick_x * speed;
        //telemetry.addData("Robot view tank speed ", "%2.5f %2.5f %2.5f", forward,strafe,rotate);
        drive(forward, strafe, rotate);
    }

    public void driveFieldOriented(IMU imu, Gamepad gamepad1) {
        double theta = imu.theta();
        telemetry.addData("field oriented ", "%2.5f theta", theta);
        //field oriented drive
        // "theta" is measured COUNTER-CLOCKWISE from the zero reference:
        //start zero heading, counter-closeweise positive until 180 degree,
        //then change to -180 and goes to -1
        //theta = theta * Math.PI / 180; // change to radians measure of the angle

        // remapping gamepad
        double y = gamepad1.left_stick_y;
        double x  =  gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;
        double forward = y;
        double strafe  = x;
        if (Math.abs(theta) < 85)  {
            //head outward: doing nothing
            strafe = -x;
        }
        else if (Math.abs(theta) > 95) {
            //head inward: remapping
            forward = -y;
            strafe = x;
        }
        else if (theta > -95 && theta < -85){
            //head right
            forward = x;
            strafe = 0.0;
        }
        else {
            //head left
            forward = -x;
            strafe = 0.0;
        }
        /*double temp = forward * Math.cos(theta) + strafe * Math.sin(theta);
        strafe = -forward * Math.sin(theta) + strafe * Math.cos(theta);
        forward = temp; */

        drive(forward, strafe, rotate);
    }

    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    public void drive(double forward, double strafe, double rotate) {
        double leftPower      = Range.clip(forward + strafe - rotate, -1.0, 1.0) ;
        double rightPower     = Range.clip(forward - strafe + rotate, -1.0, 1.0) ;
        double leftBackPower  = Range.clip(forward - strafe - rotate, -1.0, 1.0) ;
        double rightBackPower = Range.clip(forward + strafe + rotate, -1.0, 1.0) ;

        //safe drive
        if (Math.abs(leftPower) < 0.05 ) leftPower = 0.0;
        if (Math.abs(rightPower) < 0.05) rightPower = 0.0;
        if (Math.abs(leftBackPower) < 0.05 ) leftBackPower = 0.0;
        if (Math.abs(rightBackPower) < 0.05) rightBackPower = 0.0;

        if (Math.abs(forward) > 0.1 || Math.abs(strafe) > 0.1 || Math.abs(rotate) > 0.1) {
            telemetry.addData("input ", "%2.5f %2.5f %2.5f", forward,strafe, rotate);
            telemetry.addData("powers ", "%2.5f %2.5f %2.5f %2.5f", leftPower,rightPower,leftBackPower,rightBackPower);
        }

        frontLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
        //fine turn for each motor due to robot balance or motor power inbalance. need more work???
        /*double offset1 = 1.0, offset2 = 1.0, offset3 = 1.0, offset4 = 1.0;
        frontLeft.setPower(powerOffset(offset1,leftPower));
        frontRight.setPower(powerOffset(offset2,rightPower));
        backLeft.setPower(powerOffset(offset3,leftBackPower));
        backRight.setPower(powerOffset(offset4,rightBackPower)); */
    }

    private double powerOffset(double offset, double power) {
        if (offset > 0.9 || Math.abs(power) > 0.5 ) return power;
        return offset*power;
    }

    public void cruise() {
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void brake() {
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetEncoder() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void encoderMode() {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void positionMode() {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /*public void encoderDrive(double speed, int distance) {
        DcMotor[] motors = {frontLeft,frontRight,backLeft,backRight};
        int[] distances = {distance, distance, distance, distance};
        double [] speeds = {speed, speed, speed, speed};
        int [] positions = AndyMarkMotor.runToPosition(motors, speeds, distances);
        AndyMarkMotor.composeTelemetry(telemetry);
    } */

    public void imuTurn(IMU imu, double speed, double degree, ElapsedTime runtime, double timeout, double margin) {
        encoderMode(); //make sure it runs as last usage is stop and reset
        double currDeg = imu.theta();
        while (Math.abs(degree - currDeg) > margin && runtime.seconds() < timeout) {
            telemetry.addData("imudiff ", "%2.5f", degree - imu.theta());
            if ((degree - currDeg) > 5.0) {
                drive(0.0, 0.0, -speed);
            }
            if ((degree - currDeg) < -5.0) {
                drive(0.0, 0.0, speed);
            }
            telemetry.update();
            currDeg = imu.theta();
        }
        telemetry.update();
        stop();
    }

    public void imu2StepTurn(IMU imu, double speed1, double speed2, double degree, ElapsedTime runtime, double timeout) {
        imuTurn(imu, speed1, degree, runtime, timeout, 5);
        imuTurn(imu, speed2, degree, runtime, timeout, 1);
    }

    public void encoderDrive(DcMotor motor, double speed, double distanceInches) {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        runToDistance(motor, speed, distanceInches, telemetry);
        while (motor.isBusy() ) {

            telemetry.addData("motor ", "current %7d     target %7d", motor.getCurrentPosition(), motor.getTargetPosition());
            telemetry.update();
        }
    }

    public void encoderDrive(double speed, double distanceInches, ElapsedTime runtime, double timeout, boolean fastMode) {
        DcMotor[] motors = {frontLeft,frontRight,backLeft,backRight};
        double[] distances = {distanceInches, distanceInches, distanceInches, distanceInches};
        for (int i = 0; i < motors.length; i++ ) {
            runToDistance(motors[i], speed, distances[i]);
        }
        //wait
        //Assumes runtime has already been reset
        int motorsDone;
        while (isBusy() && runtime.seconds() < timeout) {
            motorsDone = 0;
            for (int i = 0; i < motors.length; i++ ) {
                if (Math.abs(motors[i].getCurrentPosition() - inchToTicks(distanceInches, "AndyMark")) < 100) {
                    motorsDone++;
                }
                if (motorsDone == 4 || (motorsDone >= 1 && fastMode)) break;
                if (motors[i].isBusy()) {
                    telemetry.addData("motor ", "%7d current %7d     target %7d", i + 1, motors[i].getCurrentPosition(), motors[i].getTargetPosition());
                }
            }
            telemetry.update();
        }
        stop();
        resetEncoder();
    }

    //TODO place a proper modifying constant to actual inches for strafe
    //takes priority to the right as positive
    public void encoderStrafe(double speed, double distanceInches, ElapsedTime runtime, double timeout, boolean fastMode) {
        DcMotor[] motors = {frontLeft,frontRight,backLeft,backRight};
        double[] distances = {-distanceInches, distanceInches, distanceInches, -distanceInches};
        for (int i = 0; i < motors.length; i++ ) {
            runToDistance(motors[i], speed, distances[i]);
        }
        //wait
        //Assumes runtime has already been reset
        int motorsDone;
        while (isBusy() && runtime.seconds() < timeout) {
            motorsDone = 0;
            for (int i = 0; i < motors.length; i++ ) {
                if (Math.abs(motors[i].getCurrentPosition() - motors[i].getTargetPosition()) < 100) {
                    motorsDone++;
                }
                if (motorsDone == 4 || (motorsDone >= 1 && fastMode)) break;
                if (motors[i].isBusy()) {
                    telemetry.addData("motor ", "%7d current %7d     target %7d", i, motors[i].getCurrentPosition(), motors[i].getTargetPosition());
                }
            }
            telemetry.update();
        }
        stop();
        resetEncoder();
    }

    public boolean isBusy() {
        return frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy();
    }


}