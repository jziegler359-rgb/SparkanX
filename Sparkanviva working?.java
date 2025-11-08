@Override
public void runOpMode() throws InterruptedException {
    try {
        // ---------- DRIVETRAIN SETUP ----------
        FRW = hardwareMap.get(DcMotor.class, "FRW");
        FLW = hardwareMap.get(DcMotor.class, "FLW");
        BRW = hardwareMap.get(DcMotor.class, "BRW");
        BLW = hardwareMap.get(DcMotor.class, "BLW");

        // Set all motors to brake mode
        FRW.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FLW.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BRW.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BLW.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BRW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BLW.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // More consistent motor directions for tank drive
        FRW.setDirection(DcMotor.Direction.REVERSE);
        BRW.setDirection(DcMotor.Direction.REVERSE); // Changed to REVERSE
        FLW.setDirection(DcMotor.Direction.FORWARD);
        BLW.setDirection(DcMotor.Direction.FORWARD); // Changed to FORWARD

        // ---------- SUBSYSTEMS WITH ERROR HANDLING ----------
        try {
            intake = new IntakeTeleOp(hardwareMap);
            outtake = new OuttakeTeleOp(hardwareMap);
        } catch (Exception e) {
            telemetry.addData("Error", "Subsystem initialization failed: " + e.getMessage());
            telemetry.update();
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // ----- Tank Drive -----
            double leftPower = -gamepad1.left_stick_y;
            double rightPower = -gamepad1.right_stick_y;

            FLW.setPower(leftPower);
            BLW.setPower(leftPower);
            FRW.setPower(rightPower);
            BRW.setPower(rightPower);

            // ----- Intake with null check -----
            if (intake != null) {
                if (gamepad2.right_trigger > 0.1) {
                    intake.in();
                } else if (gamepad2.left_trigger > 0.1) {
                    intake.out();
                } else {
                    intake.stop();
                }
            }

            // ----- Outtake with null check -----
            if (outtake != null) {
                if (gamepad2.right_bumper) {
                    outtake.in();
                } else if (gamepad2.left_bumper) {
                    outtake.out();
                } else {
                    outtake.stop();
                }
            }

            telemetry.addData("Left Power", leftPower);
            telemetry.addData("Right Power", rightPower);
            telemetry.update();
        }
    } catch (Exception e) {
        telemetry.addData("Fatal Error", e.getMessage());
        telemetry.update();
    }
}
