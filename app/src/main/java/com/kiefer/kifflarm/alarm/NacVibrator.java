
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Vibrate the device.
 */
public class NacVibrator
{

    /**
     * Vibrator object.
     */
    private final Vibrator vibrator;

    /**
     * Flag if the vibrator is running.
     */
    private boolean isRunning = false;

    /**
     * Constructor.
     *
     * @param context The application context.
     */
    @SuppressWarnings("deprecation")
    public NacVibrator(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            // Get the manager
            VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);

            // Get the vibrator
            this.vibrator = manager.getDefaultVibrator();
        }
        // Use the old API
        else
        {
            this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    /**
     * @return Flag if the vibrator is running.
     */
    public boolean isRunning()
    {
        return this.isRunning;
    }

    /**
     * Set the flag if the vibrator is running.
     *
     * @param running Flag if the vibrator is running.
     */
    public void setRunning(boolean running)
    {
        this.isRunning = running;
    }

    /**
     * Cleanup any resources.
     */
    public void cleanup()
    {
        // Stop any current vibrations
        this.vibrator.cancel();

        // Clear the flag
        this.isRunning = false;
    }

    /**
     * Vibrate the device using on/off timings.
     */
    @SuppressWarnings("deprecation")
    private void vibrate(List<Long> timings)
    {
        // API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // Match amplitudes to the timings list (0 for pause, DEFAULT_AMPLITUDE for vibrate)
            List<Integer> amplitudes = new ArrayList<>();
            int repeatPattern = timings.size() / 3;

            // Timings list is built around a pause, vibrate, pause sequence, so build the
            // amplitude list the same way
            for (int i = 0; i < repeatPattern; i++)
            {
                amplitudes.add(0);
                amplitudes.add(VibrationEffect.DEFAULT_AMPLITUDE);
                amplitudes.add(0);
            }

            // This vibration sequence uses a pattern that ends with a different wait than
            // wait in between vibrations. Add another wait at the end to account for this pattern
            if (amplitudes.size() != timings.size())
            {
                amplitudes.add(0);
            }

            // Create a vibration that will repeat indefinitely (that is what the 0 is for)
            VibrationEffect effect = VibrationEffect.createWaveform(
                    toLongArray(timings), toIntArray(amplitudes), 0);

            // Vibrate (API 33+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                VibrationAttributes attr = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM);
                this.vibrator.vibrate(effect, attr);
            }
            // Vibrate (API 26-32)
            else
            {
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                this.vibrator.vibrate(effect, attr);
            }
        }
        // API 25-
        else
        {
            // Vibrate
            this.vibrator.vibrate(toLongArray(timings), 0);
        }

        // Set the flag
        this.isRunning = true;
    }

    /**
     * Vibrate the device for an alarm.
     *
     * @param alarm The alarm to vibrate for.
     */
    /*
    public void vibrateAlarm(NacAlarm alarm)
    {
        // Vibrate with a pattern
        if (alarm.getShouldVibratePattern())
        {
            NacLog.i("Vibrating for " + alarm.getVibrateDuration() + " ms, then waiting for "
                    + alarm.getVibrateWaitTime() + " ms. Repeat " + alarm.getVibrateRepeatPattern()
                    + " times, then wait for " + alarm.getVibrateWaitTimeAfterPattern() + " ms (indefinitely)");

            vibrateWithPattern(
                    alarm.getVibrateDuration(),
                    alarm.getVibrateWaitTime(),
                    alarm.getVibrateRepeatPattern(),
                    alarm.getVibrateWaitTimeAfterPattern());
        }
        // Vibrate normally
        else
        {
            NacLog.i("Vibrating for " + alarm.getVibrateDuration() + " ms, then waiting for "
                    + alarm.getVibrateWaitTime() + " ms (indefinitely)");

            vibrateNormally(alarm.getVibrateDuration(), alarm.getVibrateWaitTime());
        }
    }

     */

    /**
     * Vibrate the device normally.
     *
     * @param duration Amount of time (ms) to vibrate for.
     * @param wait Amount of time (ms) to wait after vibrating.
     */
    public void vibrateNormally(long duration, long wait)
    {
        // Vibrate pattern will be: pause 0ms, vibrate <duration> ms, pause <wait> ms
        List<Long> timings = Arrays.asList(0L, duration, wait);

        // Vibrate
        vibrate(timings);
    }

    /**
     * Vibrate the device with a pattern.
     *
     * @param duration Amount of time (ms) to vibrate for.
     * @param wait Amount of time (ms) to wait after vibrating.
     * @param repeatPattern Number of times to repeat a pattern.
     * @param waitAfterPattern Amount of time (ms) to wait after a pattern is complete.
     */
    public void vibrateWithPattern(
            long duration,
            long wait,
            int repeatPattern,
            long waitAfterPattern)
    {
        List<Long> timings = new ArrayList<>();

        // Vibrate pattern will be: pause 0ms, vibrate <duration> ms, pause <wait> ms
        // Repeat this for <repeatPattern> times
        for (int i = 0; i < repeatPattern; i++)
        {
            timings.add(0L);
            timings.add(duration);
            timings.add(wait);
        }

        // Lastly, add a wait of <waitAfterPattern> ms
        timings.add(waitAfterPattern);

        // Vibrate
        vibrate(timings);
    }

    /**
     * Convert a list of longs into a primitive long array.
     *
     * @param list The list of longs.
     *
     * @return The primitive long array.
     */
    private static long[] toLongArray(List<Long> list)
    {
        long[] array = new long[list.size()];

        for (int i = 0; i < list.size(); i++)
        {
            array[i] = list.get(i);
        }

        return array;
    }

    /**
     * Convert a list of ints into a primitive int array.
     *
     * @param list The list of ints.
     *
     * @return The primitive int array.
     */
    private static int[] toIntArray(List<Integer> list)
    {
        int[] array = new int[list.size()];

        for (int i = 0; i < list.size(); i++)
        {
            array[i] = list.get(i);
        }

        return array;
    }

}
