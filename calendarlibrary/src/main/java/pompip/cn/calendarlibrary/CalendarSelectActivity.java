package pompip.cn.calendarlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_select);
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag("calendarFragment");
        calendarFragment.setOnCalendarSelectedListener(new CalendarFragment.OnCalendarSelectedListener() {
            @Override
            public void onSelected(Date date) {
                Toast.makeText(CalendarSelectActivity.this, format.format(date), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
