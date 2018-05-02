package pompip.cn.calendaview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pompip.cn.calendarlibrary.CalendarFragment;
import pompip.cn.calendarlibrary.CalendarSelectActivity;
import pompip.cn.calendarlibrary.OnCalendarSelectedListener;

public class MainActivity extends AppCompatActivity {

    private RecyclerView horizontal_calender_recycler_view;
    private OnCalendarSelectedListener onCalendarSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horizontal_calender_recycler_view = findViewById(R.id.horizontal_calender_recycler_view);
        findViewById(R.id.tv_to_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarSelectActivity.class));
            }
        });
        initCalendar();
        onCalendarSelectedListener = new OnCalendarSelectedListener() {
            @Override
            public void onSelected(Date date) {
                String format = new SimpleDateFormat("yyyy-MM-dd E", Locale.CHINA).format(date);
                Toast.makeText(MainActivity.this, format, Toast.LENGTH_SHORT).show();
            }
        };
        horizontal_calender_recycler_view.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontal_calender_recycler_view.setAdapter(new HorizontalAdapter());
        horizontal_calender_recycler_view.scrollToPosition(dateArrayList.size()-1);


    }

    ArrayList<Date> dateArrayList = new ArrayList<>();

    void initCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-10);
        for (int i = 0; i < 10; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            dateArrayList.add(calendar.getTime());
        }
    }


    class HorizontalAdapter extends RecyclerView.Adapter {

        Calendar now = Calendar.getInstance(Locale.CHINA);
        Calendar current = Calendar.getInstance(Locale.CHINA);

        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EE", Locale.CHINA);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MM-dd", Locale.CHINA);
        int selectPosition = dateArrayList.size()-1;

        HorizontalAdapter(){
            now.setTime(new Date());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_calendar, parent, false);
            return new RecyclerView.ViewHolder(inflate) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            TextView week_day = holder.itemView.findViewById(R.id.week_day);
            TextView month_day = holder.itemView.findViewById(R.id.month_day);
            final Date item = dateArrayList.get(position);
            current.setTime(item);
            if (current.get(Calendar.DAY_OF_YEAR) ==now.get(Calendar.DAY_OF_YEAR)){

                week_day.setText("今天");
            }else {
                week_day.setText(weekDayFormat.format(item));
            }

            month_day.setText(monthDayFormat.format(item));
            int color = getResources().getColor(position != selectPosition ? android.R.color.white : R.color.blue);
            week_day.setTextColor(color);
            month_day.setTextColor(color);
            holder.itemView.setSelected(position == selectPosition);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int oldPosition = selectPosition;
                    selectPosition = position;
                    horizontal_calender_recycler_view.scrollToPosition(selectPosition);
                    notifyItemChanged(selectPosition);
                    notifyItemChanged(oldPosition);
                    if (onCalendarSelectedListener!=null){
                        onCalendarSelectedListener.onSelected(item);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return dateArrayList.size();
        }
    }


}
