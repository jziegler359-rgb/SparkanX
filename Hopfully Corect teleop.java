package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="2025 TeleOp - Final Mecanum", group="Linear Opmode")
public class Sparkanauts extends LinearOpMode {

    // Drive motors
    private DcMotor frontLeft = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backRight = null;

    // Mechanisms
    private DcMotor Intake = null;
    private DcMotor flyWheel = null;
    private Servo flyWheelServo = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // --- Hardware mapping ---
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        Intake = hardwareMap.get(DcMotor.class, "Intake");
        flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");
        flyWheelServo = hardwareMap.get(Servo.class, "flyWheelServo");

        // --- Motor directions (accounting for chain drive) ---
        frontLeft.setDirection(DcMotor.Direction.REVERSE);   // Chain-driven
        frontRight.setDirection(DcMotor.Direction.REVERSE);  // Chain-driven
        backLeft.setDirection(DcMotor.Direction.FORWARD);    // Direct
        backRight.setDirection(DcMotor.Direction.FORWARD);   // Direct

        Intake.setDirection(DcMotor.Direction.FORWARD);
        flyWheel.setDirection(DcMotor.Direction.REVERSE);

        // --- Disable encoders for smooth control ---
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flyWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // --- Initialize servo ---
        flyWheelServo.setPosition(0.49);  // Neutral / 90°

        // --- Wait for start ---
        waitForStart();
        telemetry.addData("Status", "Running");
        telemetry.update();

        while (opModeIsActive()) {

            // ====== DRIVE CONTROL ======
            double drive = -gamepad1.left_stick_y;  // Forward/Back
            double turn = gamepad1.left_stick_x;    // Turn
            double strafe = gamepad1.right_stick_x; // Strafe left/right

            // Mecanum drive formula
            double frontLeftPower  = drive + turn + strafe;
            double backLeftPower   = drive + turn - strafe;
            double frontRightPower = drive - turn - strafe;
            double backRightPower  = drive - turn + strafe;

            // Normalize powers so no motor goes above 100%
            double max = Math.max(1.0, Math.max(
                Math.abs(frontLeftPower),
                Math.max(Math.abs(backLeftPower),
                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower)))
            ));

            frontLeft.setPower(frontLeftPower / max);
            backLeft.setPower(backLeftPower / max);
            frontRight.setPower(frontRightPower / max);
            backRight.setPower(backRightPower / max);

            // ====== INTAKE (Right Trigger, 100%) ======
            if (gamepad1.right_trigger > 0.1) {
                Intake.setPower(1.0);
            } else {
                Intake.setPower(0);
            }

            // ====== FLYWHEEL (Left Trigger 90%, Left Bumper 75%) ======
            if (gamepad1.left_bumper) {
                flyWheel.setPower(0.75);
            } else if (gamepad1.left_trigger > 0.1) {
                flyWheel.setPower(0.9);
            } else {
                flyWheel.setPower(0);
            }

            // ====== SERVO FLICK (Right Bumper) ======
            if (gamepad1.right_bumper) {
                flyWheelServo.setPosition(0.27); // Push
                sleep(300);
                flyWheelServo.setPosition(0.49); // Reset
                sleep(300);
            }

            // ====== Telemetry ======
            telemetry.addData("Drive", "F:%.2f  T:%.2f  S:%.2f", drive, turn, strafe);
            telemetry.addData("Servo", "Pos: %.2f", flyWheelServo.getPosition());
            telemetry.addData("Intake", "Power: %.2f", Intake.getPower());
            telemetry.addData("FlyWheel", "Power: %.2f", flyWheel.getPower());
            telemetry.update();
        }
    }
}
