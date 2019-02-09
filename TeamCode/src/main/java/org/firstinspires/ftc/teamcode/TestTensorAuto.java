package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//import org.firstinspires.ftc.teamcode.subsystem.SamplingTensorFlow;
import org.firstinspires.ftc.teamcode.TensorFlowTest;

import java.util.Stack;
@Autonomous(name="Test Tensor Auto", group="test")
public class TestTensorAuto extends LinearOpMode{
    private TensorFlowTest gold;
    private String goldLocation = "UNKNOWN";

    public void runOpMode() {
        telemetry.addData("Status ", "Starting up...");
        telemetry.update();
        // Initialize hardware ------------------------------------------------------------------
        gold = new TensorFlowTest(telemetry, hardwareMap, true);
        gold.initialize();
        telemetry.update();
        gold.activate();

        Stack<Byte>[] detectionBuckets = new Stack[3];
        detectionBuckets[0] = new Stack<Byte>();
        detectionBuckets[1] = new Stack<Byte>();
        detectionBuckets[2] = new Stack<Byte>();

        int currentStack = 3;

        gold.toggleFlash();
        //Effectively waitforstart???
        while (!opModeIsActive() && !isStopRequested()) {
            telemetry.addData("Status ", "READY");
            if (detectionBuckets[currentStack % 3].size() < 500) {
                //Byte legend: 0 = center, 1 = left, 2 = right, 3 = unknown, ((4 = logical error))
                detectionBuckets[currentStack % 3].push(detectionToByteDef(gold.getPosition()));
            } else {
                gold.toggleFlash();
                sleep(100);
                detectionBuckets[(currentStack - 1) % 3].clear();
                currentStack++;
                gold.toggleFlash();
            }
            telemetry.addData("currStackSize", detectionBuckets[currentStack % 3].size());
            telemetry.addData("currentStack ", currentStack % 3);
            //if (detectionBuckets[currentStack-1].size() != 0)
            //telemetry.addData("currentStack detection ", detectionBuckets[currentStack-1].peek());
            telemetry.update();
            sleep(2);
        }
        gold.shutdown();

        int[] counts = {0, 0, 0};
        if (detectionBuckets[currentStack % 3].size() > detectionBuckets[(currentStack - 1) % 3].size()) {
            for (Byte byteDefDetection : detectionBuckets[currentStack % 3]) {
                counts = countDetects(counts, byteDefDetection);
            }
        } else {
            for (Byte byteDefDetection : detectionBuckets[(currentStack - 1) % 3]) {
                counts = countDetects(counts, byteDefDetection);
            }
        }

       /* if(detectionBuckets[0].size()==1000) {
            goldLocation = "CENTER";
        }
        else if(detectionBuckets[1].size()==1000) {
            goldLocation = "LEFT";
        }
        else {
            goldLocation = "RIGHT";
        }
        telemetry.addData("gold location", " %s", goldLocation);
        telemetry.update();*/

     //   goldLocation = countsToDetection(counts);
    }
    public Byte detectionToByteDef(String detection) {
        switch (detection) {
            case "CENTER":
                return new Byte((byte) 0);
            case "LEFT":
                return new Byte((byte) 1);
            case "RIGHT":
                return new Byte((byte) 2);
            case "UNKNOWN":
                return new Byte((byte) 3);
        }
        return new Byte((byte) 4);
    }
    public int[] countDetects(int[] incounts, Byte bytedef) {
        int[] counts = incounts;
        switch (bytedef) {
            case 0:
                counts[1]++;
                break;
            case 1:
                counts[0]++;
                break;
            case 2:
                counts[2]++;
            default:
                break;
        }
        return counts;
    }

    public String countsToDetection(int[] counts) {
        if (counts[0] > counts[1] && counts[0] > counts[2]) {
            return "LEFT";
        } else if (counts[1] > counts[0] && counts[1] > counts[2]) {
            return "CENTER";
        } else if (counts[2] > counts[0] && counts[2] > counts[1]) {
            return "RIGHT";
        }
        return "UNKNOWN";
    }
}
