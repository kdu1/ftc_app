/* Copyright (c) 2018 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

/**
 * This 2018-2019 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the gold and silver minerals.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@TeleOp(name = "Concept: TensorFlow Object Detection", group = "Concept")
//@Disabled
public class TestTensor extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    TensorFlowTest tensor = new TensorFlowTest(telemetry, hardwareMap, true);

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
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

    private boolean guess = false;

    private int failed3detects = 0;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            //tensor.initialize();
            tensor.getPosition();
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                if (tfod != null) {

                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.

                    String out = "UNKNOWN";
                    telemetry.addData("Status ", "Detecting Gold... Standby.");
                    telemetry.update();
                    if (tfod == null) {
                        telemetry.addData("test", "testing testing");
                        telemetry.update();

                    }


                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getRecognitions();

                    if (updatedRecognitions == null) {
                        telemetry.addData("test", "testing testing");
                        telemetry.update();

                    }

                    List<Recognition> filteredRecognitions = new ArrayList<>();
                    filteredRecognitions = updatedRecognitions;
                    for (Recognition recognition : updatedRecognitions) {
                        float height = recognition.getHeight();
                        float width = recognition.getWidth();
                        float ratio = height / width;
                        boolean used = false;
                        if (
                            //Check if its within normal detection size
                                height > 30 && height < 120 && width > 30 && width < 120 &&
                                        //Check if the ratio is "square" enough to be a detection
                                        ratio < 1.35 && ratio > 0.65 &&
                                        //Futher remove pit detections by ruling out detections higher up
                                        recognition.getTop() >= 100 && recognition.getTop() <= Integer.MAX_VALUE &&
                                        //And finally, check whether Tensorflow is happy with itself
                                        recognition.getConfidence() > 0.85) {
                            filteredRecognitions.add(recognition);
                            used = true;
                        }

                        telemetry.addData("h, w, r, c " + recognition.getLabel() + " " + used,
                                "%5.2f %5.2f %5.2f %5.2f", height, width, ratio, recognition.getConfidence());

                    }
       /* telemetry.addData("test","testing testing");
        telemetry.update();*/
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
                    telemetry.update();
                    if (filteredRecognitions.size() == 3) {
                        int goldMineralX = -1;
                        int silverMineral1X = -1;
                        int silverMineral2X = -1;
                        for (Recognition recognition : filteredRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMineralX = (int) recognition.getLeft();
                            } else if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();
                            } else {
                                silverMineral2X = (int) recognition.getLeft();
                            }
                        }
                        if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                            if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                telemetry.addData("Regular Mode Detection:", "LEFT");
                                out = "LEFT";
                            } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                telemetry.addData("Regular Mode Detection:", "RIGHT");
                                out = "RIGHT";
                            } else {
                                telemetry.addData("Regular Mode Detection:", "CENTER");
                                out = "CENTER";
                            }
                        }
                        telemetry.update();
                    } else {
                        failed3detects++; //Detecting 3 objects has failed, add to count
                    }
                    //If enabled, after 2000 failures, the routine can guess with just two objects.
                    try {
                        if (filteredRecognitions.size() == 2 && guess && failed3detects > 2000) {

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

                        } else if (filteredRecognitions.size() == 1 && guess && failed3detects > 2000) {
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
                    //return out;
                    if (tfod != null) {
                        tfod.shutdown();
                    }
                }
                    /*if(filteredRecognitions.size()>3) {
                        for (Recognition recognition : updatedRecognitions) {
                            float height = recognition.getHeight();
                            float width = recognition.getWidth();
                            float ratio = height / width;
                            float top = recognition.getTop();
                            boolean used = false;
                            for (int i = 0; filteredRecognitions.size() > 3 && i < 100; i++) {
                                top -= 100;
                                if (//Check if its within normal detection size
                                        height > 30 && height < 120 && width > 30 && width < 120 &&
                                                //Check if the ratio is "square" enough to be a detection
                                                ratio < 1.35 && ratio > 0.65 &&
                                                //Futher remove pit detections by ruling out detections higher up
                                                recognition.getTop() <= 100 &&
                                                //And finally, check whether Tensorflow is happy with itself
                                                recognition.getConfidence() > 0.85) {
                                    filteredRecognitions.add(recognition);
                                    used = true;
                                }

                                telemetry.addData("h, w, r, c " + recognition.getLabel() + " " + used,
                                        "%5.2f %5.2f %5.2f %5.2f", height, width, ratio, recognition.getConfidence());

                                telemetry.addData("in loop", "");
                                telemetry.addData("allsize:", updatedRecognitions.size());
                                telemetry.addData("filteredsize:", filteredRecognitions.size());
                                telemetry.update();

                            }
                        }
                            telemetry.addData("allsize:", updatedRecognitions.size());
                            telemetry.addData("filteredsize:", filteredRecognitions.size());
                            telemetry.update();

                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;
                            for (Recognition recognition : filteredRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    telemetry.addData("Regular Mode Detection:", "LEFT");
                                    out = "LEFT";
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    telemetry.addData("Regular Mode Detection:", "RIGHT");
                                    out = "RIGHT";
                                } else {
                                    telemetry.addData("Regular Mode Detection:", "CENTER");
                                    out = "CENTER";
                                }
                            }
                            telemetry.update();
                        }*/
                }
            }
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
        parameters.cameraDirection = CameraDirection.BACK;

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
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

}
