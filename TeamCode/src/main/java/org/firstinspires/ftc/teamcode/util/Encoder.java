package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Encoder {
    static final double     COUNTS_PER_MOTOR_REV_T    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     COUNTS_PER_MOTOR_REV_A    = 1120 ;    // eg: AndyMark Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH_T         = (COUNTS_PER_MOTOR_REV_T * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     COUNTS_PER_INCH_A         = (COUNTS_PER_MOTOR_REV_A * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    //there are four run mode
    //DcMotor.RunMode.RUN_WITHOUT_ENCODER: set the power directly, still tracking encoder
    //DcMotor.RunMode.RUN_USING_ENCODER: set the speed
    //DcMotor.RunMode.RUN_TO_POSITION: run to the position and hold the position there
    //DcMotor.RunMode.STOP_AND_RESET_ENCODER: newer version to reset the encoder
    //isBusy() is useful method

    //run to new position ------------------------------------------------------------------------
    public void runToPosition(DcMotor motor, double speed, int positionTicks) {
        motor.setPower(speed);
        motor.setTargetPosition(positionTicks);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void runToPosition(DcMotor motor, double speed, double positionInches, String motorType) {
        int positionTicks = inchToTicks(positionInches, motorType);
        runToPosition(motor, speed, positionTicks);
    }

    public void runToPosition(DcMotor motor, double speed, double positionInches) {
        int positionTicks = inchToTicks(positionInches, motorType(motor));
        runToPosition(motor, speed, positionTicks);
    }

    //run from current position to the + or - distance away,  ------------------------------------
    public int runToDistance(DcMotor motor, double speed, int distanceTicks) {
        int newPosition = motor.getCurrentPosition() + distanceTicks;
        runToPosition(motor, speed, newPosition);

        return newPosition;
    }

    public int runToDistance(DcMotor motor, double speed, int distanceTicks, Telemetry telemetry) {
        int newPosition = motor.getCurrentPosition() + distanceTicks;
        motor.setPower(speed);
        motor.setTargetPosition(newPosition);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (motor.isBusy()) {
            telemetry.addData("run motor ", "current %7d     target %7d", motor.getCurrentPosition(), motor.getTargetPosition());
            telemetry.update();
        }
        return newPosition;
    }

    public int runToDistance(DcMotor motor, double speed, double distanceInches, Telemetry telemetry) {
        int newPosition = motor.getCurrentPosition() + inchToTicks(distanceInches, "AndyMark");
        telemetry.addData("motor ", "current %7d     target %7d", motor.getCurrentPosition(), motor.getTargetPosition());
        //telemetry.update();
        runToPosition(motor, speed, newPosition);
        while (motor.isBusy()) {
            telemetry.addData("run motor ", "current %7d     target %7d", motor.getCurrentPosition(), motor.getTargetPosition());
            telemetry.update();
        }
        return newPosition;
    }

    public int runToDistance(DcMotor motor, double speed, double distanceInches) {
        int newPosition = motor.getCurrentPosition() + inchToTicks(distanceInches, "AndyMark");
        runToPosition(motor, speed, newPosition);
        return newPosition;
    }
    // inches to ticks conversion based on the motor type -----------------------------------------
    public int inchToTicks(double inches, String motorType) {
        int ticks = 0;
        if (motorType.equals("AndyMark"))
            ticks = (int) (inches * COUNTS_PER_INCH_A);
        else if (motorType.equals("Tetrix"))
            ticks = (int) (inches * COUNTS_PER_INCH_T);
        return ticks;
    }

    public String motorType(DcMotor motor) { //Lynx is Andy Mark motor, see DriveAutoLeft testing
        if (motor.getManufacturer() == HardwareDevice.Manufacturer.Lynx )  {
            return "AndyMark";
        }
        return motor.getManufacturer().name();
    }
}
