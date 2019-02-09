package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AndyMarkMotor {
    private static DcMotor [] motors = null;
    private static double [] distanceInches;
    private static int [] distanceTicks;
    private static double [] speeds;
    //static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     COUNTS_PER_MOTOR_REV    = 1120 ;    // eg: AndyMark Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    //there are four run mode
    //DcMotor.RunMode.RUN_WITHOUT_ENCODER: set the power directly, still tracking encoder
    //DcMotor.RunMode.RUN_USING_ENCODER: set the speed
    //DcMotor.RunMode.RUN_TO_POSITION: run to the position and hold the position there
    //DcMotor.RunMode.STOP_AND_RESET_ENCODER: newer version to reset the encoder
    //isBusy() is useful method
    public static int[] runToPosition(DcMotor [] motorIn, double[] speed, int [] distanceTicks) {
        if (motors == null) motors = motorIn;
        int n = motors.length;
        positions = new int[n];
        for (int i = 0; i < motors.length; i++ ) {
            motors[i].setPower(speed[i]);
            positions[i] = motors[i].getCurrentPosition() + distanceTicks[i];
            motors[i].setTargetPosition(positions[i]);
            motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        return positions;
    }

    private static int [] positions;
    public static int []runToPosition(DcMotor[] motorIn, double[] speed, double []distanceInches) {
        if (motors == null) motors = motorIn;
        int n = motors.length;
        positions = new int[n];
        for (int i = 0; i < n; i++ ) {
            motors[i].setPower(speed[i]);
            positions[i] = motors[i].getCurrentPosition() + (int) (distanceInches[i]* COUNTS_PER_INCH);
            motors[i].setTargetPosition(positions[i]);
            motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        return positions;
    }

    public static void composeTelemetry(Telemetry telemetry) {
        int n = motors.length;
        for (int i = 0; i < n; i++) {
            telemetry.addLine().addData("Path1 ", "Running to %7d", positions[i])
                    .addData("Path2 ", "Running at %7d ",
                            motors[i].getCurrentPosition());;
        }
    }
}