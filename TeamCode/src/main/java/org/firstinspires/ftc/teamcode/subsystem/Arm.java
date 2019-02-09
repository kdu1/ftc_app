package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Encoder;

//Hardward is in Hub 2 (the one close to the lift marked 5)
public class Arm extends Encoder {
    public DcMotor v_pivot = null;  //arm up and down
    public Servo cascader = null; //arm expand and shrink, HS-785HB servo, multi-turn, but always reurn to center
    public Servo intake  = null; //intaking vex motor 393

    //arm start positions, where the countings start
    public int V_MIN = 0; //should be 0
    //arm is down, shrink, and at front when stop
    private final int V_STOP = 5;
    //arm current up and down positions
    public boolean v_state_up;
    public int v_position;    //ticks
    public int v_target;

    //arm expand and shrink
    public double CMAX = 0.93;
    public double CMIN = 0.1;
    public double INCREMENT = 0.05;
    public double c_position;

    private ElapsedTime time = new ElapsedTime();

    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;
    Telemetry telemetry;

    // Constructor for TeleOp
    public Arm(HardwareMap hwMap, Telemetry telemetry) {
        /* Initialize standard Hardware interfaces */
        this.hwMap = hwMap;
        this.telemetry = telemetry;

        init_v_pivot();
        init_cascade();
        init_intake();
    }

    private void init_v_pivot() {
        v_pivot = hwMap.get(DcMotor.class, "v_pivot");
        v_pivot.setDirection(DcMotor.Direction.REVERSE);
        //v_pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //this behave not as expected
        v_pivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        v_pivot.setPower(0.0);
        v_state_up = true;
        v_position = v_pivot.getCurrentPosition();
        v_target = v_position;
        V_MIN = v_position;
    }

    private void init_cascade() {
        cascader = hwMap.get(Servo.class, "cascader");
        cascader.setPosition(0.0);
        c_position = 0.0;
    }

    private void init_intake() {
        intake = hwMap.get(Servo.class, "intake"); //version 2 changed to use vex 393
    }

    /* Constructor for Auto */
    public Arm(Telemetry telemetry, HardwareMap hwMap) { // }, liftType type) {
            /* Initialize standard Hardware interfaces */
        this.hwMap = hwMap;
        this.telemetry = telemetry;
        init_v_pivot();
        init_cascade();
    }

    //up and down ---------------------------------------------------------------------------------
    public void up() { //target is the ticks
        v_position = v_pivot.getCurrentPosition();
        v_target = v_position + 80;
        runToPosition(v_pivot, 0.3, v_target);
        //runToDistance(v_pivot, 0.3, v_target, telemetry);
        /*
        if (!v_state_up) { //in case margins differ between directions
            probeVPos();
            v_state_up = true;
        }
        vmove(80, 0.3, 60); */
    }

    public void down() {
        v_position = v_pivot.getCurrentPosition();
        v_target = v_position - 80;
        runToPosition(v_pivot, 0.1, v_target);

        /*
        if (v_state_up) { //in case margins differ between directions
            probeVPos();
            v_state_up = false;
        }
        vmove(-100, 0.1, 75); */
    }

    public void vhold() {
        runToPosition(v_pivot, 0.3, v_target);
        telemetry.addData("vhold: ", "%d %d", v_position, v_target);
        telemetry.update();
    }

    public void vmove (int distanceTicks, double speed, int margin) {
        if (Math.abs(v_target - v_pivot.getCurrentPosition()) <= margin) {
            probeVPos();
            v_target = /*Range.clip(*/v_position + distanceTicks/*, V_MIN, V_MAX)*/;

            /* no need as we changed to servo
            if (v_target >= 1250 && !c_lock_state) {
                c_lock_position = cascade.getCurrentPosition();
                c_lock_state = true;
            } else if (v_target < 1250 && c_lock_state) {
                c_lock_state = false;
            } */

            runToPosition(v_pivot, speed, v_target);
        } else {

        }
    }

    public void probeVPos() {
        v_position = v_pivot.getCurrentPosition();
        v_target = v_position;
    }

    // expand and shrink -------------------------------------------------------------------------
    public void shrink() {
        c_position = cascader.getPosition();
        if (c_position <= CMIN) return;
        c_position -= INCREMENT;
        cascader.setPosition(c_position);
    }

    public void expand() {
        c_position = cascader.getPosition();
        if (c_position >= CMAX) return;
        c_position += INCREMENT;
        cascader.setPosition(c_position);
    }

    public void silverExpand() {
        c_position = 0.7;
        cascader.setPosition(c_position);
    }

    public void goldExpand() {
        c_position = 0.85;
        cascader.setPosition(c_position);
    }

    // in and out ----------------------------------------------------------------------------
    // using vex motor 393
    public void intake() {
        //it needs to give some time after the call so the servo have time to response
        intake.setPosition(0.25);
    }
    public void outtake() {
        //it needs to give some time after the call so the servo have time to response
        intake.setPosition(0.75);
    }
    public void holdtake() {
        intake.setPosition(0.5);
    }


    //Combined arm move --------------------------------------------------------------------------
    public void stop() {
        v_pivot.setTargetPosition(V_STOP);
        v_pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (v_pivot.isBusy()) {} //wait for moving down to finish
        v_pivot.setPower(0.0); //?? should we rest v_pivot by
        intake.setPosition(0.5);
    }

    private void sleep(double ms) {
        time.reset();
        while (time.milliseconds() < ms) {}
    }
}

