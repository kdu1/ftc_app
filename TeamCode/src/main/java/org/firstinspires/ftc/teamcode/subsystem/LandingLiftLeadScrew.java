package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.util.Encoder;

public class  LandingLiftLeadScrew extends Encoder {
    public DcMotor lift    = null;
    /* need to pass object of HardareMap as local OpMode members. */
    HardwareMap hwMap           =  null;
    Telemetry telemetry;
    ElapsedTime runtime;

    public static double position;
    private static boolean isUsed = false;
    private final int MAXP = 25500;
    private final int MINP = 0;
    private final double MAXINCH = 8.0; //assuming starting position is shrinking all the way down
    private final double MININCH = 0.3;


    /* Constructor */
    public LandingLiftLeadScrew(Telemetry telemetry, HardwareMap hwMap) { // }, liftType type) {
        /* Initialize standard Hardware interfaces */
        this.hwMap = hwMap;
        this.telemetry = telemetry;
        init();
    }

    private void init() {
        runtime = new ElapsedTime();
        lift = hwMap.get(DcMotor.class, "lift");
        //lift.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        lift.setDirection(DcMotor.Direction.REVERSE); //gobilda
        //lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lift.setPower(0.0);
        position = lift.getTargetPosition(); //make sure we start from all the way down position
    }

    //little up and down
    public void up()   { //default up 0.5 inches
        up(1.5, 1.0, inchToTicks(1, "AndyMark"));
    }

    public void up(double timeout, double speed, int distanceTicks)   {
        distanceTicks  = Math.min(distanceTicks, MAXP);
        runtime.reset();
        runToDistance(lift, speed, distanceTicks);
        while (runtime.seconds() < timeout && lift.isBusy()) {
            position = lift.getTargetPosition();
            telemetry.addData("up position ", "%5.2f ", position);
        }
        stop();
    }

    public void up(double timeout, double speed, double targetPosition)   {
        targetPosition = Math.min(targetPosition, MAXINCH);
        runtime.reset();
        runToPosition(lift, speed, targetPosition);
        while (runtime.seconds() < timeout && lift.isBusy()) {
            position = lift.getTargetPosition();
            telemetry.addData("up position ", "%5.2f ", position);
        }
        stop();
    }

    public void down() {//default down 0.5 inches
        down(2.0, 1.0, inchToTicks(-1, "AndyMark"));
    }

    public void down(double timeout, double speed, int distanceTicks)   {
        distanceTicks  = Math.max(distanceTicks, MINP);
        runtime.reset();
        runToDistance(lift, speed, distanceTicks);
        while (runtime.seconds() < timeout && lift.isBusy()) {
            position = lift.getTargetPosition();
            telemetry.addData("down position ", "%5.2f ", position);
        }
        stop();
    }

    public void down(double timeout, double speed, double position)   {
        position = Math.max(position, MININCH);
        runtime.reset();
        runToPosition(lift, speed, position);
        while (runtime.seconds() < timeout && lift.isBusy()) {
            position = lift.getTargetPosition();
            telemetry.addData("down position ", "%5.2f ", position);
        }
        stop();
    }

    public void downToMin(double timeout) {
        runtime.reset();
        runToPosition(lift, 1.0, MINP);
        stop();
    }

    public void stop() {
        lift.setPower(0.0);
        //lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //not using encoder
    }

    public void latching() {
        downToMin(8.0);
    }

    public void landing(double timeout) {
        runtime.reset();
        int target = MAXP;
        runToPosition(lift, 1.0, MAXP - 2000);
        while (runtime.seconds() < timeout && lift.isBusy()) {
            position = lift.getCurrentPosition();
            telemetry.addData("landing target vs current: ", "%7d %7d", target, (int)position);
            telemetry.addData("at time: ", "%7.2f", runtime.seconds());
            telemetry.update();
        }
        //runToPosition(lift, 0.3, MAXP); //slow down when close to the limit
        //sleep(2000);
        stop();
    }

    public void reset(double power) {
        lift.setPower(power);
        position = lift.getTargetPosition();
    }

    private void sleep(double s) {
        runtime.reset();
        while (runtime.seconds() < s) {}
    }
}
