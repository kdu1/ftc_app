package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This is NOT an opmode.
 *
 * This class is to define hardware and behaviou of tank two wheel drive train
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Motor channel 0:  Left  drive motor:        "leftDrive"
 * Motor channel 1:  Right drive motor:        "rightDrive"
 */

public class DriveTank2WW {
    /* Public OpMode members. */
    public DcMotor leftDrive   = null;
    public DcMotor rightDrive  = null;
    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;
    Telemetry telemetry;
    static final double     Kp  = 0.03;

    /* Constructor */
    public DriveTank2WW(Telemetry telemetry, HardwareMap hwMap){
        this.telemetry = telemetry;
        this.hwMap = hwMap;
        init();
    }

    /* Initialize standard Hardware interfaces */
    private void init( ) {
        // Define and Initialize Motors
        leftDrive  = hwMap.get(DcMotor.class, "leftDrive");
        rightDrive = hwMap.get(DcMotor.class, "rightDrive");

        leftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        drive(0.0, 0.0); // Set all motors to zero power
    }

    public void driveStraightLine(double forward, double theta ) {
        drive(forward, Kp * theta);
    }

    //alway use POV mode to control robot
    // left  y: forward / backward
    // right x: clockwise / counter-clockwise

    public void driveRobotOriented(Gamepad gamepad1) {
        double forward = -gamepad1.left_stick_y;
        double turn  =  gamepad1.right_stick_x;
        telemetry.addData("robot view drive ", "%2.5f %2.5f straight turn ", forward, turn);
        drive(forward, turn);
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
        theta = theta * Math.PI / 180; // change to radians measure of the angle

        if (Math.abs(theta) > 90.0) forward = -forward; //easy one
        drive(forward, turn);
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

    public void cruise() {
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void brake() {
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

}
