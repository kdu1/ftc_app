package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LandingLiftREV {
    public DcMotor lift1   = null;
    //public DcMotor lift2   = null;
    public DcMotor lift    = null;
    public enum liftType  {REV, LEADSCREW}; //lift two motors for REV, one motor for lead screw
    //public liftType type = liftType.REV;

    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;
    Telemetry telemetry;

    /* Constructor */
    public LandingLiftREV(Telemetry telemetry, HardwareMap hwMap) { // }, liftType type) {
    /* Initialize standard Hardware interfaces */
        this.hwMap = hwMap;
        this.telemetry = telemetry;
        /*this.type = type;

        switch (type) {
            case REV:
                initREV();
                break;
            case LEADSCREW:
                initLeadScrew();
                break;
        } */
        initREV();

    }

    private void initREV() {
        lift1 = hwMap.get(DcMotor.class, "lift1");
        lift1.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        lift1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift1.setPower(0.0);
        /*lift2 = hwMap.get(DcMotor.class, "lift2");
        lift2.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        lift2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift2.setPower(0.0); */
    }


    public void up() {
        lift1.setPower(-0.5);
        //lift2.setPower(0.4);

    }

    public void down() {
        /*switch (type) {
            case REV: */
                lift1.setPower(0.4);
                //lift2.setPower(-0.2);
           /*     break;
            case LEADSCREW:
                lift.setPower(-0.4);
                break;
        } */
    }

    public void stop() {
        /*switch (type) {
            case REV: */
                lift1.setPower(0.0);
                //lift2.setPower(0.0);
        /*        break;
            case LEADSCREW:
                lift.setPower(0.0);
                break;
        } */
    }
}
