package com.googlecode.totallylazy.time;

import com.googlecode.totallylazy.functions.Curried2;

import java.util.Date;

import static java.util.Calendar.SECOND;

public class Seconds {
    public static Date add(Date date, int amount) {
        return Dates.add(date, SECOND, amount);
    }

    public static Date subtract(Date date, int amount) {
        return Dates.subtract(date, SECOND, amount);
    }

    public static Long between(Date start, Date end) {
        return (end.getTime() - start.getTime()) / 1000L;
    }

    public static long sinceEpoch(Date time) {
        return time.getTime() / 1000;
    }

    public static class functions {
        public static Curried2<Date, Integer, Date> add = Seconds::add;

        public static Curried2<Date, Integer, Date> add()  {
            return add;
        }

        public static Curried2<Date, Integer, Date> subtract = Seconds::subtract;

        public static Curried2<Date, Integer, Date> subtract()  {
            return subtract;
        }

        public static Curried2<Date, Date, Long> between = Seconds::between;

        public static Curried2<Date, Date, Long> between()  {
            return between;
        }
    }
}