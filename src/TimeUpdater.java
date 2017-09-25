import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by RaczeQ on 07/02/2017.
 */
public class TimeUpdater extends Kantor implements Runnable {
    private JLabel time;

    public TimeUpdater(JLabel timeLabel) {
        time = timeLabel;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                time.setText(sdf.format(cal.getTime()));
                wait(1000);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
