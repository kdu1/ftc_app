package org.firstinspires.ftc.teamcode.dev;

/* This is code to drive Tetrix "friend" based on sensor readings
 * It is modified from Tetrix code
 */
public class DriveSensorsTetrix {
//    #include <PRIZM.h>
//    #include <ChainableLED.h>
//#define NUM_LEDS 2
//    ChainableLED leds(2, 9, NUM_LEDS);

//    PRIZM prizm;

    int TrackPos = 0;
    int SonicHead = 0;
    int HeadPosition = 90;

    int MapMotor1;
    int MapMotor2;

    int MapDir;

    char a;

    void setup() {
/*
        leds.init();

        prizm.PrizmBegin();

        delay(1000);

        Serial.begin(9600);

        prizm.setServoSpeed(1,70);

        prizm.setServoPosition(1,0);

        prizm.setServoPosition(2,30);

        prizm.setMotorInvert(2,1);

        while (prizm.readSonicSensorCM(3) > 100){      // sweep head back and forth here until an object is detected
            HeadPosition = prizm.readServoPosition(1);
            if (HeadPosition == 0){prizm.setServoPosition(1,180);}
            if (HeadPosition == 180){prizm.setServoPosition(1,0);}

        }
        delay(200); */
    }

    void loop() {
        GetHeadAngle();

        GetSonicSensor();

        MapPath();
    }


    void GetSonicSensor(){
        /*delay(10);
        SonicHead = prizm.readSonicSensorCM(3); */
    }

    void GetHeadAngle(){
        /*HeadPosition = prizm.readServoPosition(1);
        if (HeadPosition == 0){prizm.setServoPosition(1,180);}
        if (HeadPosition == 180){prizm.setServoPosition(1,0);}
*/
    }

    void MapPath(){
/*

        if (SonicHead < 20){

            prizm.setServoPosition(2,30);
            prizm.setMotorPowers(0,0);
            delay(1000);

        }


        if (SonicHead <=150 && SonicHead >=20){
            MapDir = map(HeadPosition, 0, 180, -40, 40); ??? what is map function
            prizm.setMotorPowers(30 + MapDir, 35 - MapDir);
            prizm.setServoPosition(2,150);

        }


        if (SonicHead > 100) {
            prizm.setMotorPowers(25,30);
            prizm.setServoPosition(2,30);
            prizm.setRedLED(LOW);}
*/
    }
}
