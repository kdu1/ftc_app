package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystem.IMU;

/**
 * This is NOT an opmode.
 *
 * This class is to define hardware and behaviou of tank two wheel drive train
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Motor channel 0:  Left  drive motor:        "leftDrive"
 * Motor channel 1:  Right drive motor:        "rightDrive"
 * Motor channel 2:  Left back drive motor:    "leftBackDrive"
 * Motor channel 3:  Right back drive motor:   "rightBackDrive"
 *
 * The angle is multiplied by a proportional scaling constant (Kp) to scale it
 * for the speed of the robot drive. This factor is called the proportional constant
 * or loop gain. Increasing Kp will cause the robot to correct more quickly
 * (but too high and it will oscillate). Decreasing the value will cause the robot correct
 * more slowly (possibly never reaching the desired heading). This is known as proportional
 * control, and is discussed further in the PID control section of the advanced programming
 * section.
 */

public class DriveTrain {
    /* Public OpMode members. */
    public DcMotor leftDrive   = null;
    public DcMotor rightDrive  = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightBackDrive = null;
    public enum tankType  {TANK2M, MECANUM}; //tank two motors, mecanum four motors
    public tankType tank;

    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;

    Telemetry telemetry;
    static final double     Kp  = 0.03;

    /* Constructor */
    public DriveTrain(tankType t, Telemetry telemetry){
        tank = t;
        this.telemetry = telemetry;
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        leftDrive  = hwMap.get(DcMotor.class, "leftDrive");
        rightDrive = hwMap.get(DcMotor.class, "rightDrive");

        leftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        switch (tank) {
            case TANK2M:
                drive(0.0, 0.0); // Set all motors to zero power
                break;
            case MECANUM:
                // Define and Initialize Motors
                leftBackDrive  = hwMap.get(DcMotor.class, "leftBackDrive");
                rightBackDrive = hwMap.get(DcMotor.class, "rightBackDrive");

                leftBackDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
                rightBackDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors

                // Set all motors to run without encoders.
                // May want to use RUN_USING_ENCODERS if encoders are installed.
                leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                drive(0.0, 0.0, 0.0); // Set all motors to zero power
                break;
        }
    }

    public void driveStraightLine(double forward, double theta ) {
        drive(forward, Kp * theta);
    }

    //alway use POV mode to control robot
    // left  y: forward / backward
    // left  x: left / right
    // right x: clockwise / counter-clockwise

    public void driveRobotOriented(IMU imuWrapper, Gamepad gamepad1) {
        double forward = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;
        double theta = imuWrapper.theta();

        telemetry.addData("robot oriented ", "%2.5f theta", theta);
        switch (tank) {
            case TANK2M:

                drive(forward, turn);
                break;
            case MECANUM:
                // remapping gamepad
                double strafe = gamepad1.left_stick_x;
                drive(forward, turn, strafe);
                break;
        }
        telemetry.update();
    }

    public void driveFieldOriented(IMU imuWrapper, Gamepad gamepad1) {
        double forward = gamepad1.left_stick_y;
        double turn  =  -gamepad1.right_stick_x;
        double theta = imuWrapper.theta();

        telemetry.addData("field oriented ", "%2.5f theta", theta);
        //field oriented drive
        // "theta" is measured COUNTER-CLOCKWISE from the zero reference:
        //start zero heading, counter-closeweise positive until 180 degree,
        //then change to -180 and goes to -1
        if (Math.abs(theta) > 90.0) forward = -forward;
        //theta = theta * Math.PI / 180; // change to redian
        //double temp = forward * Math.cos(theta) + turn * Math.sin(theta);
        //turn = -forward * Math.sin(theta) + turn * Math.cos(theta);
        //forward = temp;

        switch (tank) {
            case TANK2M:
                drive(forward, turn);
                break;
            case MECANUM:
                // remapping gamepad
                double strafe = gamepad1.left_stick_x;
                drive(forward, turn, strafe);
                break;
        }

        telemetry.update();
    }

    public void drive(double forward, double turn) {
        double leftPower    = Range.clip(forward + turn, -1.0, 1.0) ;
        double rightPower   = Range.clip(forward - turn, -1.0, 1.0) ;
        //safe drive
        if (Math.abs(leftPower) < 0.05 && Math.abs(rightPower) < 0.05) {
            leftPower = 0.0;
            rightPower = 0.0;
        }
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
    }

    public void drive(double forward, double turn, double strafe) {
        double leftPower      = Range.clip(forward + turn + strafe, -1.0, 1.0) ;
        double rightPower     = Range.clip(forward - turn - strafe, -1.0, 1.0) ;
        double leftBackPower  = Range.clip(forward + turn - strafe, -1.0, 1.0) ;
        double rightBackPower = Range.clip(forward - turn + strafe, -1.0, 1.0) ;

        //safe drive
        if (Math.abs(leftPower) < 0.05 ) leftPower = 0.0;
        if (Math.abs(rightPower) < 0.05) rightPower = 0.0;
        if (Math.abs(leftBackPower) < 0.05 ) leftBackPower = 0.0;
        if (Math.abs(rightBackPower) < 0.05) rightBackPower = 0.0;
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
    }

    public void cruise() {
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        if (tank == tankType.MECANUM) {
            leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }

    public void brake() {
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if (tank == tankType.MECANUM) {
            leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

}