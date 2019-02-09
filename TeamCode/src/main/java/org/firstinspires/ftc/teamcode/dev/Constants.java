package org.firstinspires.ftc.teamcode.dev;

/**
 * A list of constants used by the rest of the robot code. This include physics constants as well as constants
 * determined through calibrations.
 */
public class Constants {
    public static final double kLooperDt = 0.01;

    /* ROBOT PHYSICAL CONSTANTS */

    // Wheels
    public static final double kDriveWheelTrackWidthInches = 25.54;
    public static final double kDriveWheelDiameterInches = 3.92820959548 * 0.99;
    public static final double kDriveWheelRadiusInches = kDriveWheelDiameterInches / 2.0;
    public static final double kTrackScrubFactor = 1.0;  // Tune me!

    // Tuned dynamics
    public static final double kRobotLinearInertia = 60.0;  // kg TODO tune
    public static final double kRobotAngularInertia = 10.0;  // kg m^2 TODO tune
    public static final double kRobotAngularDrag = 12.0;  // N*m / (rad/sec) TODO tune
    public static final double kDriveVIntercept = 1.055;  // V
    public static final double kDriveKv = 0.135;  // V per rad/s
    public static final double kDriveKa = 0.012;  // V per rad/s^2

    // Geometry
    public static final double kCenterToFrontBumperDistance = 38.25 / 2.0;
    public static final double kCenterToRearBumperDistance = 38.25 / 2.0;
    public static final double kCenterToSideBumperDistance = 33.75 / 2.0;

    /* CONTROL LOOP GAINS */

    // Gearing and mechanical constants.
    public static final double kDriveDownShiftVelocity = 9.5 * 12.0;  // inches per second
    public static final double kDriveDownShiftAngularVelocity = Math.PI / 2.0; // rad/sec
    public static final double kDriveUpShiftVelocity = 11.0 * 12.0;  // inches per second

    public static final double kPathKX = 4.0;  // units/s per unit of error
    public static final double kPathLookaheadTime = 0.4;  // seconds to look ahead along the path for steering
    public static final double kPathMinLookaheadDistance = 24.0;  // inches

    // PID gains for drive velocity loop (LOW GEAR)
    // Units: setpoint, error, and output are in ticks per second.
    public static final double kDriveLowGearVelocityKp = 0.9;
    public static final double kDriveLowGearVelocityKi = 0.0;
    public static final double kDriveLowGearVelocityKd = 10.0;
    public static final double kDriveLowGearVelocityKf = 0.0;
    public static final int kDriveLowGearVelocityIZone = 0;
    public static final double kDriveVoltageRampRate = 0.0;

    // PID gains for elevator velocity loop (HIGH GEAR)
    // Units: setpoint, error, and output are in native units per 100ms.
    // Elevator encoder is CTRE mag encoder which is 4096 native units per revolution.
    public static final double kElevatorHighGearKp = 0.15;
    public static final double kElevatorHighGearKi = 0.0;//0.0;
    public static final double kElevatorHighGearKd = 4.0;
    public static final double kElevatorHighGearKf = 0.06; // lower speed:  0.08;
    public static final double kElevatorJogKp = 0.1;
    public static final double kElevatorJogKd = 3.0;
    public static final double kElevatorFeedforwardNoCube = -0.06;//33000;
    public static final double kElevatorFeedforwardWithCube = -0.07;//33000;

    public static final int kElevatorHighGearMaxIntegralAccumulator = 500000; //todo: tune me
    public static final int kElevatorHighGearIZone = 0;
    public static final int kElevatorHighGearDeadband = 0;
    public static final int kElevatorHighGearCruiseVelocity = 12500;
    public static final int kElevatorHighGearAcceleration = 27000;//33000;
    public static final double kElevatorEpsilon = 1.0;//33000;
    public static final double kElevatorRampRate = 0.1;

    public static final double kWristKp = 3.0;
    public static final double kWristKi = 0.0;
    public static final double kWristKd = 50.0;
    public static final double kWristKf = 1.05;
    public static final double kWristJogKp = 2.0;
    public static final double kWristJogKd = 40.0;
    public static final double kWristKaWithCube = 0.006;
    public static final double kWristKaWithoutCube = 0.003;
    public static final double kWristKfMultiplierWithCube = 0.15;
    public static final double kWristKfMultiplierWithoutCube = 0.1;
    public static final double kWristElevatorAccelerationMultiplier = -1.0;
    public static final double kWristEpsilon = 2.0;

    public static final int kWristMaxIntegralAccumulator = 500000; //todo: tune me
    public static final int kWristIZone = 500; //todo: tune me
    public static final int kWristDeadband = 5; //todo: tune me
    public static final int kWristCruiseVelocity = 2500; //todo: tune me
    public static final int kWristAcceleration = 2500; //2000 //todo: tune me
    public static final double kWristRampRate = 0.001;
    public static final double kAutoWristRampRate = 0.01;

    // Do not change anything after this line unless you rewire the robot and
    // update the spreadsheet!
    // Port assignments should match up with the spreadsheet here:
    // https://docs.google.com/spreadsheets/d/179YszqnEWPWInuHUrYJnYL48LUL7LUhZrnvmNu1kujE/edit#gid=0

    /* I/O */
    // (Note that if multiple talons are dedicated to a mechanism, any sensors
    // are attached to the master)

    public static final int kCANTimeoutMs = 10; //use for on the fly updates
    public static final int kLongCANTimeoutMs = 100; //use for constructors

    // Drive
    public static final int kLeftDriveMasterId = 5;
    public static final int kLeftDriveSlaveAId = 6;
    public static final int kLeftDriveSlaveBId = 7;
    public static final int kRightDriveMasterId = 12;
    public static final int kRightDriveSlaveAId = 13;
    public static final int kRightDriveSlaveBId = 14;

    // Followers
    public static final int kFollowerLeftAChannelId = 2;
    public static final int kFollowerLeftBChannelId = 3;
    public static final int kFollowerRightAChannelId = 0;
    public static final int kFollowerRightBChannelId = 1;
    public static final int kFollowerRearAChannelId = 4;
    public static final int kFollowerRearBChannelId = 5;

    // Intake
    public static final int kIntakeLeftMasterId = 9;
    public static final int kIntakeRightMasterId = 10;
    public static final int kCanifierId = 0;

    // Elevator
    public static final int kElevatorMasterId = 11;
    public static final int kElevatorRightSlaveId = 8;
    public static final int kElevatorLeftSlaveAId = 1;
    public static final int kElevatorLeftSlaveBId = 2;

    // Wrist
    public static final int KWristMasterId = 15;

    // Control Board
    public static final boolean kUseGamepadForDriving = false;
    public static final boolean kUseGamepadForButtons = true;

    public static final int kDriveGamepadPort = 0;
    public static final int kButtonGamepadPort = 2;
    public static final int kMainThrottleJoystickPort = 0;
    public static final int kMainTurnJoystickPort = 1;
    public static final double kJoystickThreshold = 0.5;
    public static final double kJoystickJogThreshold = 0.4;

    // Height in in after applying turn factor.
    public static final double kElevatorLowSensitivityThreshold = 50.0;
    public static final double kLowSensitivityFactor = 1.0 / 4.0;

    public static final double kElevatorThrottleDeadband = 0.3;
    public static final double kMinShootTimeSec = 0.2;
    public static final double kJazzHandsEpsilon = 2.5;

    public static final double kKickstandToggleRumbleTime = 0.5;
    public static final double kKickstandDelay = 1.0;

}

