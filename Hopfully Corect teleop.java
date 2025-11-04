package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="2025 TeleOp - Special Drivetrain (If-Else)", group="Linear Opmode")
public class Sparkanauts extends LinearOpMode {

    private DcMotor frontLeft, backLeft, frontRight, backRight;
    private DcMotor Intake, flyWheel;
    private Servo flyWheelServo;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // --- Hardware Map ---
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");
        flyWheelServo = hardwareMap.get(Servo.class, "flyWheelServo");

        // --- Motor Directions ---
        frontLeft.setDirection(DcMotor.Direction.REVERSE);   // chain driven
        frontRight.setDirection(DcMotor.Direction.FORWARD);  // chain driven
        backLeft.setDirection(DcMotor.Direction.FORWARD);    // direct drive
        backRight.setDirection(DcMotor.Direction.REVERSE);   // direct drive

        Intake.setDirection(DcMotor.Direction.FORWARD);
        flyWheel.setDirection(DcMotor.Direction.REVERSE);

        // --- Run without encoders ---
        DcMotor[] motors = {frontLeft, backLeft, frontRight, backRight, Intake, flyWheel};
        for (DcMotor m : motors) {
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        flyWheelServo.setPosition(0.49);
        waitForStart();

        while (opModeIsActive()) {

            // ===== DRIVE CONTROL =====
            double y = -gamepad1.left_stick_y;   // forward/back
            double x = gamepad1.left_stick_x;   // turning
            double strafe = gamepad1.right_stick_x; // strafing

            // === FORWARD / BACKWARD ===
            if (y > 0.1) {  // forward
                frontLeft.setPower(1);
                frontRight.setPower(1);
                backLeft.setPower(1);
                backRight.setPower(1);
            } else if (y < -0.1) { // backward
                frontLeft.setPower(-1);
                frontRight.setPower(-1);
                backLeft.setPower(-1);
                backRight.setPower(-1);
            } 
            // === TURNING ===
            else if (x > 0.1) {  // turn right
                frontLeft.setPower(1);
                backLeft.setPower(1);
                frontRight.setPower(-1);
                backRight.setPower(-1);
            } else if (x < -0.1) { // turn left
                frontLeft.setPower(-1);
                backLeft.setPower(-1);
                frontRight.setPower(1);
                backRight.setPower(1);
            } 
            // === STRAFING ===
            else if (strafe > 0.1) {  // strafe right
                frontLeft.setPower(strafe);
                backLeft.setPower(-strafe);
                frontRight.setPower(-strafe);
                backRight.setPower(strafe);
            } else if (strafe < -0.1) {  // strafe left
                frontLeft.setPower(strafe);
                backLeft.setPower(-strafe);
                frontRight.setPower(-strafe);
                backRight.setPower(strafe);
            } 
            // === NEUTRAL ===
            else {
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
            }

            // ===== INTAKE ALWAYS ON =====
            Intake.setPower(1.0);

            // ===== FLYWHEEL CONTROL =====
            if (gamepad1.left_bumper) {
                flyWheel.setPower(0.75);
            } else if (gamepad1.left_trigger > 0.1) {
                flyWheel.setPower(0.9);
            } else {
                flyWheel.setPower(0);
            }

            // ===== SERVO FLICK =====
            if (gamepad1.right_bumper) {
                flyWheelServo.setPosition(0.27);
                sleep(250);
                flyWheelServo.setPosition(0.49);
                sleep(500);
            }

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
