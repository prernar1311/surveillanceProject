import android.hardware.Sensors;

public class AccelerometerReader {

	/** True when the Accelerometer-functionality is basically available. */
	boolean accelerometerAvailable = false;
	boolean isEnabled = false;

	/**
	 * Sets up an AccelerometerReader. Checks if Accelerometer is available on
	 * this device and throws UnsupportedOperationException if not .
	 * 
	 * @param doEnable :
	 *            enables the devices Accelerometer 
	 *            initially (if sensor available)
	 * @throws UnsupportedOperationException
	 *             if Accelerometer is not available on this device.
	 */
	public AccelerometerReader(boolean doEnable)
			throws UnsupportedOperationException {

		/* Check once here in the constructor if an
		 * Accelerometer is available on this device. */
		for (String aSensor : Sensors.getSupportedSensors())
			if (aSensor.equals(Sensors.SENSOR_ACCELEROMETER))
				accelerometerAvailable = true;

		if (!accelerometerAvailable)
			throw new UnsupportedOperationException(
					"Accelerometer is not available.");

		if (doEnable)
			setEnableAccelerometer(true);
	}

	/**
	 * En/Dis-able the Accelerometer.
	 * 
	 * @param doEnable
	 *            <code>true</code> for enable.<br>
	 *            <code>false</code> for disable.
	 * @throws UnsupportedOperationException
	 */
	public void setEnableAccelerometer(boolean doEnable)
			throws UnsupportedOperationException {
		if (!accelerometerAvailable)
			throw new UnsupportedOperationException(
					"Accelerometer is not available.");

		/* If should be enabled and not already is: */
		if (doEnable && !this.isEnabled) {
			Sensors.enableSensor(Sensors.SENSOR_ACCELEROMETER);
			this.isEnabled = true;
		} else /* If should be disabled and not already is: */
		if (!doEnable && this.isEnabled) {
			Sensors.disableSensor(Sensors.SENSOR_ACCELEROMETER);
			this.isEnabled = false;
		}
	}

	/**
	 * Read out the values currently provided by the Accelerometer.
	 * 
	 * @return the current Accelerometer-values.
	 * @throws UnsupportedOperationException
	 *             if Accelerometer is not available on this device.
	 * @throws IllegalStateException
	 *             if Accelerometer was set to disabled.
	 */
	public float[] readAccelerometer() throws UnsupportedOperationException, IllegalStateException {
		if (!accelerometerAvailable)
			throw new UnsupportedOperationException(
					"Accelerometer is not available.");

		if (!this.isEnabled)
			throw new IllegalStateException(
					"Accelerometer was set to disabled!");
		/* Get number of sensor-values the sensor will return. Could be
		 * variable, depending of the amount of axis (1D, 2D or 3D
		 * accelerometer). */
		int sensorValues = Sensors
				.getNumSensorValues(Sensors.SENSOR_ACCELEROMETER);
		float[] out = new float[sensorValues];

		/* Make the OS fill the array we passed. */
		Sensors.readSensor(Sensors.SENSOR_ACCELEROMETER, out);

		/* And return it. */
		return out;
	}
}