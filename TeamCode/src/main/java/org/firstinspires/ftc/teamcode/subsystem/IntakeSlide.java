package org.firstinspires.ftc.teamcode.subsystem;


import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.Encoder;

public class IntakeSlide extends Encoder {
    private DcMotor intakeFlip;
    private CRServo intake;
    private CRServo intake2;
    private DcMotor intakeOut;

    private DistanceSensor inStopper;

    HardwareMap hwMap =  null;
    Telemetry telemetry;

    //arm expand and shrink
    public int ENDSTOP;
    public int c_position;
    public int c_target;
    public double c_speed;
    public String c_state;

    //arm start positions, where the countings start
    public int V_MIN = 0; //should be 0
    //arm is down, shrink, and at front when stop
    //private final int V_STOP = 5;
    //arm current up and down positions
    public boolean v_state_up;
    public int v_position;    //ticks
    public int v_target;
    public double v_speed;

    public IntakeSlide (HardwareMap hwMap, Telemetry telemetry) {
        /* Initialize standard Hardware interfaces */
        this.hwMap = hwMap;
        this.telemetry = telemetry;
    }

    public void init () {
        intake = hwMap.get(CRServo.class, "intake");
        intake2 = hwMap.get(CRServo.class, "intake2");

        intakeOut = hwMap.get(DcMotor.class, "intakeOut");
        intakeOut.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeOut.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeOut.setPower(0.0);
        c_position = intakeOut.getCurrentPosition();
        c_target = c_position;
        c_state = "";
        updateStops();

        inStopper = hwMap.get(DistanceSensor.class, "inStopper");

        intakeFlip = hwMap.get(DcMotor.class, "intakeFlip");
        intakeFlip.setDirection(DcMotor.Direction.REVERSE);
        //v_pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //this behave not as expected
        //intakeFlip.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeFlip.setPower(0.0);
        v_state_up = true;
        v_position = intakeFlip.getCurrentPosition();
        v_target = v_position;
        V_MIN = v_position;
    }

    private void updateStops() {
        ENDSTOP = intakeOut.getCurrentPosition()-5200;
    }

    public void shrink() {
        if (!(inStopper.getDistance(DistanceUnit.CM) < 6.) || inStopper.getDistance(DistanceUnit.CM) < 0) {
            if (c_state.equals("mOUT")) {
                c_target = intakeOut.getCurrentPosition();
                intakeOut.setTargetPosition(intakeOut.getCurrentPosition());
            }
            cmove(1, 300, 170);
            c_state = "mIN";
        } else {
            updateStops();
        }
    }

    public void expand() {
        if (c_state.equals("mIN")) {
            c_target = intakeOut.getCurrentPosition();
            intakeOut.setTargetPosition(intakeOut.getCurrentPosition());
        }
        cmove(1,-300, 170);
        c_state = "mOUT";
    }

    private boolean cmove(double speed, int change, int margin) {
        if (Math.abs(intakeOut.getCurrentPosition() - c_target) < margin) {
            c_target += change;
            c_speed = speed;
            return true;
        } else {
            return false;
        }
    }


    public void intake() {
        intake.setPower(-1);
    }
    public void intake2(){
        intake2.setPower(-1);
    }
    public void outtake() {
        intake.setPower(1);
    }
    public void outtake2(){
        intake2.setPower(1);
    }
    public void stopIntake() {
        intake.setPower(0);
    }
    public void stopIntake2() {
        intake2.setPower(0);
    }


    public void setIntakeFlip (double power) {
        intakeFlip.setPower(power);
    }

    //up and down ---------------------------------------------------------------------------------
    public void up() { //target is the ticks
        vmove(0.4,20);
    }

    public void down() {
        vmove(3,-20);
    }

    public void vhold() {
        v_speed = 0.8;
    }

    private boolean vmove(double speed, int change) {
        intakeFlip.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (Math.abs(intakeFlip.getCurrentPosition() - v_target) < 30) {
            v_target += change;
            v_speed = speed;
            return true;
        } else {
            return false;
        }
    }

    public void update() {
        //telemetry.addData("intakeFlip","speed: "+v_speed+" target: "+v_target);
        //runToPosition(intakeFlip, v_speed, v_target);
        telemetry.addData("stopperpos", inStopper.getDistance(DistanceUnit.CM));


        c_target = Math.max(c_target, ENDSTOP);
        telemetry.addData("intakeOut","speed: "+c_speed+" target: "+c_target + " position: " + intakeOut.getCurrentPosition());
        runToPosition(intakeOut, c_speed, c_target);
    }

    public void stopFlip() {
        //intakeFlip.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //intakeFlip.setTargetPosition(V_STOP);
        //intakeFlip.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //while (intakeFlip.isBusy()) {} //wait for moving down to finish
        intakeFlip.setPower(0.0); //?? should we rest v_pivot by
    }

    public void upFlip() {
        intakeFlip.setPower(0.75);
    }

    public void downFlip() {
        intakeFlip.setPower(-0.6);
    }
}
