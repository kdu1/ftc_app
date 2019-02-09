package org.firstinspires.ftc.teamcode.dev;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.AndyMarkMotor;
public abstract class ArmNonActiveOpMode extends LinearOpMode {

    public DcMotor cascade = null;
    public DcMotor v_pivot = null;
    //public Servo claw = null;
    public CRServo claw = null;
    public Servo h_pivot = null;

    public int v_position = 0;

    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;
    Telemetry telemetry;

    /* Constructor */
    public ArmNonActiveOpMode () { //(Telemetry telemetry, HardwareMap hwMap) { // }, liftType type) {
        /* Initialize standard Hardware interfaces */
        this.hwMap = hardwareMap;
        this.telemetry = telemetry;
        initArm();

    }

    public void initArm() {
        cascade = hwMap.get(DcMotor.class, "cascade");
        cascade.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        cascade.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        cascade.setPower(0.0);
        v_pivot = hwMap.get(DcMotor.class, "v_pivot");
        v_pivot.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        v_pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        v_pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        v_pivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        v_pivot.setPower(0.0);
        claw = hwMap.get(CRServo.class, "claw");
        //claw.setPosition(0.5);
        h_pivot = hwMap.get(Servo.class, "h_pivot");
        h_pivot.setPosition(0.0);

    }
    public void center(){
        h_pivot.setPosition(.0);
    }
    public void left(){
        double position = h_pivot.getPosition();
        if (position < 0.005) position = 0.0;
        else position -= .005;
        h_pivot.setPosition(position);
        sleep(50);
    }
    public void right(){
        double position = h_pivot.getPosition();
        if (position > 0.99) position = 1.0;
        else position += 0.005;
        h_pivot.setPosition(position);
        sleep(50);
    }

    public void move(Servo s, double speed, double target) {
        double current = s.getPosition();
        while (Math.abs(current-target) < 0.01) {
            if (current > target) current -= speed;
            else current += speed;
            s.setPosition(current);
            sleep(50); //wait(50L); not good, has to handle error and they did appear
        }
    }
    // Continuous Rotation setpower goes from -1 to 1. Zero is stopped,
    // negative is one direction, positive is the other. (Some servos don't stop at zero,
    // you have to put in a small positive or negative number to find where they actually stop.)
    public void closed() {
        //it needs to give some time after the call so the servo have time to response
        claw.setPower(-0.7);
    }

    public void open() {
        claw.setPower(1.0);
    }

    public void hold() {
        claw.setPower(0.0);
    }

    public void out() { //assuming on no load
        cascade.setPower(-0.7); //new full power
    }


    public void in() {
        cascade.setPower(0.7); //new full power
    }

    public void expand() { //assuming on no load
        cascade.setPower(-1.0); //new full power
    }
    public void shrink() { //assuming on no load
        cascade.setPower(1.0); //new full power
    }

    public void stopArm() {
        v_pivot.setTargetPosition(v_position);
        v_pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        cascade.setPower(0.0);
    }

    public void up() {
        DcMotor[] motors = {v_pivot};
        double [] speeds = {0.28};
        double [] distanceInches = {1.0};
        v_position = AndyMarkMotor.runToPosition(motors, speeds, distanceInches)[0];
            /*v_pivot.setPower(0.7);
            v_position = v_pivot.getCurrentPosition() + 20;
            v_pivot.setTargetPosition(v_position);
            v_pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION); */
    }

    public void down() {
        DcMotor[] motors = {v_pivot};
        double [] speeds = {-0.1};
        double [] distanceInches = {-0.2};
        v_position = AndyMarkMotor.runToPosition(motors, speeds, distanceInches)[0];
            /*
            v_pivot.setPower(-0.7);
            v_position = v_pivot.getCurrentPosition() - 20;
            v_pivot.setTargetPosition(v_position);
            v_pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION); */
    }

        /*private int runToPosition(DcMotor motor, double speed, int distance) {
            motor.setPower(speed);
            int position = motor.getCurrentPosition() + distance;
            motor.setTargetPosition(position);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            return position;
        } */
}
