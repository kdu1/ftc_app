package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.ArrayList;
import java.util.List;

public class SamplingTensorFlow {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold";
    private static final String LABEL_SILVER_MINERAL = "Silv";

    //the developer key has been updated as of 11/25/2018 to recognize the external webcam
    private static final String VUFORIA_KEY = "AQKeBKb/////AAABmfNaqlcy3UaOsu0dRA43ih5dzMpN/iR5wvX9C0iL6/Sn+zDFTh9DKy+UxH7huvKqjJQIFfac+f3wqKTHh6P/W1LR2K2h4A32TUQWyXLI9+Zr5bYa7CSv5a82CZVrDhAOkxe4vl2+zhexLNaDe5e/ua0yJp8M3TCQL0QzUEOjbnmPIpRW6+M1c6Eaz+9diFZbEZjpOAWalWgOEJi+PGBifwmaPDbw5Hn17uucqszWRkr/wHeVu80VhtdjOSOaBQSdCc53DElvbsoi5vgQWQRyiGG/Koi42TmgKLpMgcFyCB6RFgpgnZnWcC3IkVfEQUGtOtLU6Uyhi56OE8+pfqrXVsq9GX+SSwnp06FLv3GYmV22";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    private List<Recognition> updatedRecognitions;

    private Telemetry telemetry = null;
    private HardwareMap hardwareMap = null;

    private boolean guess = false;
    private int failed3detects = 0;

    private boolean flashState = false;

    public SamplingTensorFlow(Telemetry telemetry, HardwareMap hwMap, boolean guess) {
        /* Initialize standard Hardware interfaces */
        hardwareMap = hwMap;
        this.telemetry = telemetry;
        this.guess = guess;
    }

    public void initialize() {
        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            telemetry.addData("TFOD", "Compatibility check succeeded.");
            initTfod();
        } else {
            telemetry.addData("TFOD", "Compatibility check did not succeed.");
        }
        //telemetry.update();
    }

    public void activate() {
        if (tfod != null) {
            tfod.activate();
        }
    }

    public void deactivate() {
        tfod.deactivate();
    }

    public void shutdown() {
        if (tfod != null) {
            tfod.shutdown();
        }
    }

    public String getPosition() {
        String out = "UNKNOWN";
        telemetry.addData("Status ", "Detecting Gold... Standby.");
        if (tfod == null) return "TFODNULL";

        // getUpdatedRecognitions() will return null if no new information is available since
        // the last time that call was made.
        List<Recognition> updatedRecognitions = tfod.getRecognitions();

        if (updatedRecognitions == null) return "UPDATENULL";

        List<Recognition> filteredRecognitions = new ArrayList<>();
        for (Recognition recognition : updatedRecognitions) {
            float height = recognition.getHeight();
            float width = recognition.getWidth();
            float ratio = height / width;
            boolean used = false;
            if (
                  //Check if its within normal detection size
                  height > 40 && height < 160 && width > 50 && width < 160 &&
                  //Check if the ratio is "square" enough to be a detection
                  (
                  (ratio < 1.4 && ratio > 0.5 && recognition.getLabel() == LABEL_SILVER_MINERAL) ||
                  (ratio < 1.4 && ratio > 0.75 && recognition.getLabel() == LABEL_GOLD_MINERAL)
                  )
                  //Futher remove pit detections by ruling out detections higher up
                  //recognition.getTop() >= 100 && recognition.getTop() <= Integer.MAX_VALUE &&
                  //And finally, check whether Tensorflow is happy with itself
                  //recognition.getConfidence() > 0.6
            )
            {
                filteredRecognitions.add(recognition);
                used = true;
            }
            telemetry.addData("h, w, r, c " + recognition.getLabel() + " " + used,
                    "%5.2f %5.2f %5.2f %5.2f", height, width, ratio, recognition.getConfidence());
        }

        //print recognitions for telemetry
        /*telemetry.addData("Label", "Val:");
        for (Recognition recognition : filteredRecognitions) {
            telemetry.addData(recognition.getLabel() + " left", recognition.getLeft());
            telemetry.addData("^width", recognition.getWidth());
                    //telemetry.addData("^height", recognition.getWidth());
                    telemetry.addData("^good?", recognition.getConfidence());
                } */
        telemetry.addData("allsize:", updatedRecognitions.size());
        telemetry.addData("filteredsize:", filteredRecognitions.size());
        if (filteredRecognitions.size() == 3) {
            int goldMineralX = -1;
            int silverMineral1X = -1;
            int silverMineral2X = -1;
            for (Recognition recognition : updatedRecognitions) {
                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                    goldMineralX = (int) recognition.getLeft();
                } else if (silverMineral1X == -1) {
                    silverMineral1X = (int) recognition.getLeft();
                } else {
                    silverMineral2X = (int) recognition.getLeft();
                }
            }
            out = columnCheck(goldMineralX);
        } else {
            failed3detects++; //Detecting 3 objects has failed, add to count
        }
        //If enabled, after 2000 failures, the routine can guess with just two objects.
        try {
            if (filteredRecognitions.size() == 2 && guess && failed3detects > 200) {

                telemetry.addData("WARN", "Using guess mode for 2!");
                int goldIndex = -1;
                boolean silverState = true;
                String[] silverColumns = new String[2];
                for (Recognition recognition : filteredRecognitions) {
                    if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                        goldIndex = filteredRecognitions.indexOf(recognition);
                        break;
                    } else {
                        if (silverState) {
                            silverColumns[0] = columnCheck(recognition.getLeft());
                            silverState = false;
                        } else {
                            silverColumns[1] = columnCheck(recognition.getLeft());
                        }
                    }
                }
                if (goldIndex != -1) {
                    out = columnCheck(filteredRecognitions.get(goldIndex).getLeft());
                } else {
                    for (String column : new String[]{"LEFT", "CENTER", "RIGHT"}) {
                        if (!(silverColumns[0].equals(column) || silverColumns[1].equals(column))) {
                            out = column;
                            break;
                        }
                    }
                }

            } else if (filteredRecognitions.size() == 1 && guess && failed3detects > 200) {
                telemetry.addData("WARN", "Using guess mode for 1!");
                if (filteredRecognitions.get(0).getLabel().equals(LABEL_GOLD_MINERAL)) {
                    out = columnCheck(filteredRecognitions.get(0).getLeft());
                } else {
                    telemetry.addData("WARN", "Using guess mode for 0.5!");
                    int guessnum = (int) Math.round(Math.random());
                    String[] possibleColumns = new String[2];
                    int index = 0;
                    for (String column : new String[]{"LEFT", "CENTER", "RIGHT"}) {
                        if (!column.equals(filteredRecognitions.get(0))) {
                            possibleColumns[index] = column;
                            index++;
                        }
                    }
                    out = possibleColumns[guessnum];
                }
            }
        } catch (Exception e) {
            telemetry.addData("WARN", "Guessing Failed! Did not match criteria!");
        }

        if (filteredRecognitions.size() <= 0 || filteredRecognitions.size() >= 4) {
            hardwareMap.get(WebcamName.class, "webcam").resetDeviceConfigurationForOpMode();
        }

        telemetry.addData("RETURN ", out);
        return out;
    }

    public void toggleFlash() {
        CameraDevice.getInstance().setFlashTorchMode(!flashState);
        flashState = !flashState;
    }

    public String columnCheck(float leftPixel) {
        if (leftPixel < 150) {
            return "LEFT";
        } else if (leftPixel > 300 && leftPixel < 480) {
            return "CENTER";
        } else if (leftPixel > 600 && leftPixel < 780) {
            return "RIGHT";
        } else {
            return "UNKNOWN";
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "webcam");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        //tfodParameters.minimumConfidence = 0.75; //xt added, default is 0.40
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}
