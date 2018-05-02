package pompip.cn.calendarlibrary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;


public class CalendarFragment extends Fragment {
    OnCalendarSelectedListener onCalendarSelectedListener;

    public void setOnCalendarSelectedListener(OnCalendarSelectedListener onCalendarSelectedListener) {
        this.onCalendarSelectedListener = onCalendarSelectedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calenda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMonth(new Date());
        RecyclerView recycler_view = view.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        CalendarAdapter adapter = new CalendarAdapter();
        recycler_view.setAdapter(adapter);
        recycler_view.scrollToPosition(adapter.getItemCount()-1);

    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.CHINA);
    SimpleDateFormat weekFormat = new SimpleDateFormat("E", Locale.CHINA);

    class CalendarAdapter extends RecyclerView.Adapter {
        private final RecyclerView.RecycledViewPool recycledViewPool;
        List<Date> monthList;
        List<List<DateInfo>> dateInfoList;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);

        CalendarAdapter() {
            monthList = new ArrayList<>(monthMap.keySet());
            dateInfoList = new ArrayList<>(monthMap.values());
            recycledViewPool = new RecyclerView.RecycledViewPool();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calenda, parent, false);
            return new RecyclerView.ViewHolder(inflate) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View itemView = holder.itemView;
            Date month = monthList.get(position);
            List<DateInfo> dateList = dateInfoList.get(position);
            RecyclerView item_recycler_view = itemView.findViewById(R.id.item_recycler_view);
            TextView text_view = itemView.findViewById(R.id.text_view);
            text_view.setText(dateFormat.format(month));
            item_recycler_view.setRecycledViewPool(recycledViewPool);
            item_recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 7));
            item_recycler_view.setAdapter(new MonthAdapter(dateList));
        }

        @Override
        public int getItemCount() {
            return monthMap.size();
        }
    }

    class MonthAdapter extends RecyclerView.Adapter {
        List<DateInfo> dateList;

        private MonthAdapter(List<DateInfo> dateList) {
            this.dateList = dateList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_calendar, parent, false);
            return new RecyclerView.ViewHolder(itemView) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View itemView = holder.itemView;
            final DateInfo dateInfo = dateList.get(position);
            TextView day = itemView.findViewById(R.id.day);
            TextView week_day = itemView.findViewById(R.id.week_day);
            if (dateInfo.type == DateInfo.CURRENT_MONTH) {
                day.setText(dateFormat.format(dateInfo.getDate()));
//                week_day.setText(weekFormat.format(dateInfo.getDate()));
                week_day.setVisibility(View.GONE);

                if (dateInfo.isToday) {
                    itemView.setBackground(getResources().getDrawable(R.drawable.bg_item_today));
                    day.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    itemView.setBackground(null);
                    day.setTextColor(getResources().getColor(dateInfo.isWeekend() ? R.color.blue : R.color.black));
                }
            } else {
                day.setText("");
                week_day.setText("");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if ( onCalendarSelectedListener!=null){
                      onCalendarSelectedListener.onSelected(dateInfo.getDate());
                  }
                }
            });

        }

        @Override
        public int getItemCount() {
            return dateList.size();
        }
    }

    TreeMap<Date, List<DateInfo>> monthMap = new TreeMap<>();

    private void initMonth(Date currentData) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(currentData);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-12);
        for (int i = 0; i < 12; i++) { //计算今年的月份
            calendar.set(Calendar.MONTH,calendar.get( Calendar.MONTH) + 1);
            Date date = calendar.getTime();
            List<DateInfo> dateInfoList = initDataList(date);
            monthMap.put(date, dateInfoList);
        }

    }


    private List<DateInfo> initDataList(Date KEY_DATE) {
        List<DateInfo> dateList = new ArrayList<>();
        Calendar todayCalender = Calendar.getInstance(Locale.CHINA);
        todayCalender.setTime(new Date());


        Calendar calendar = Calendar.getInstance(Locale.CHINA); //获取China区Calendar实例，实际是GregorianCalendar的一个实例
        calendar.setTime(KEY_DATE); //初始化日期
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  //获得当前日期所在月份有多少天（或者说day的最大值)，用于后面的计算

        Calendar calendarClone = (Calendar) calendar.clone(); //克隆一个Calendar再进行操作，避免造成混乱
        calendarClone.set(Calendar.DAY_OF_MONTH, 1);  //将日期调到当前月份的第一天
        int startDayOfWeek = calendarClone.get(Calendar.DAY_OF_WEEK); //获得当前日期所在月份的第一天是星期几
        calendarClone.set(Calendar.DAY_OF_MONTH, maxDay); //将日期调到当前月份的最后一天
        int endDayOfWeek = calendarClone.get(Calendar.DAY_OF_WEEK); //获得当前日期所在月份的最后一天是星期几

        /**
         * 计算上一个月在本月日历页出现的那几天.
         * 比如，startDayOfWeek = 3，表示当月第一天是星期二，所以日历向前会空出2天的位置，那么让上月的最后两天显示在星期日和星期一的位置上.
         */
        int startEmptyCount = startDayOfWeek - 1; //上月在本月日历页因该出现的天数。
        Calendar preCalendar = (Calendar) calendar.clone();  //克隆一份再操作
        preCalendar.set(Calendar.DAY_OF_MONTH, 1); //将日期调到当月第一天
        preCalendar.add(Calendar.DAY_OF_MONTH, -startEmptyCount); //向前推移startEmptyCount天
        for (int i = 0; i < startEmptyCount; i++) {
            DateInfo dateInfo = new DateInfo(); //使用DateInfo来储存所需的相关信息
            dateInfo.setDate(preCalendar.getTime());
            dateInfo.setWeekday(preCalendar.get(Calendar.DAY_OF_WEEK));
            dateInfo.setType(DateInfo.PRE_MONTH); //标记日期信息的类型为上个月
            dateList.add(dateInfo); //将日期添加到数组中
            preCalendar.add(Calendar.DAY_OF_MONTH, 1); //向后推移一天
        }

        /**
         * 计算当月的每一天日期
         */
        calendar.set(Calendar.DAY_OF_MONTH, 1); //由于是获取当月日期信息，所以直接操作当月Calendar即可。将日期调为当月第一天
        for (int i = 0; i < maxDay; i++) {
            DateInfo dateInfo = new DateInfo();
            dateInfo.setDate(calendar.getTime());
            dateInfo.setType(DateInfo.CURRENT_MONTH);  //标记日期信息的类型为当月
            dateInfo.setWeekday(calendar.get(Calendar.DAY_OF_WEEK));
            dateInfo.setIsToday(todayCalender.get(Calendar.DAY_OF_YEAR) ==calendar.get(Calendar.DAY_OF_YEAR));
            dateList.add(dateInfo);
            calendar.add(Calendar.DAY_OF_MONTH, 1); //向后推移一天
        }

        /**
         * 计算下月在本月日历页出现的那几天。
         * 比如，endDayOfWeek = 6，表示当月第二天是星期五，所以日历向后会空出1天的位置，那么让下月的第一天显示在星期六的位置上。
         */
        int endEmptyCount = 7 - endDayOfWeek; //下月在本月日历页上因该出现的天数
        Calendar afterCalendar = (Calendar) calendar.clone(); //同样，克隆一份在操作
        for (int i = 0; i < endEmptyCount; i++) {
            DateInfo dateInfo = new DateInfo();
            dateInfo.setDate(afterCalendar.getTime());
            dateInfo.setWeekday(afterCalendar.get(Calendar.DAY_OF_WEEK));
            dateInfo.setType(DateInfo.AFTER_MONTH); //将DateInfo类型标记为下个月
            dateList.add(dateInfo);
            afterCalendar.add(Calendar.DAY_OF_MONTH, 1); //向后推移一天
        }

        return dateList;
    }

    private static class DateInfo {
        private static final int PRE_MONTH = 1;
        private static final int CURRENT_MONTH = PRE_MONTH + 1;
        private static final int AFTER_MONTH = CURRENT_MONTH + 1;

        private Date date;
        private int type;
        private int weekday;
        private boolean isToday;

        public int getWeekday() {
            return weekday;
        }

        public void setWeekday(int weekday) {
            this.weekday = weekday;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isWeekend() {
            return weekday == Calendar.SUNDAY || weekday == Calendar.SATURDAY;
        }

        public boolean isToday() {
            return isToday;
        }

        public void setIsToday(boolean isToday) {
            this.isToday = isToday;
        }

    }

    public interface OnCalendarSelectedListener{
        void onSelected(Date date);
    }
}
