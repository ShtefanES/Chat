package ru.eshtefan.recordaudio.handler;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DateHandler предоставляет методы для обработки даты, времени.
 * Created by eshtefan on 03.10.2017.
 */

public class DateHandler {
    private final String LOG = getClass().getSimpleName();

    /**
     * Преобразует unix timestamp в миллисекундах в unix timestamp в миллисекундах в 00:00 данного дня с учетом locale, например, на вход 1508987873000(26 октября 2017 г., 10:17:53) выход 1508950800000(26 октября 2017 г., 00:00:00) с учетой местной time zone.
     *
     * @param timestamp unix timestamp в миллисекундах.
     * @return преобразованный unix timestamp в миллисекундах, для времени в 00:00 с учетом locale.
     */
    private long processDate(long timestamp) {
        Date processedDate = null;
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());

        SimpleDateFormat partDateFormat = new SimpleDateFormat("MM/dd/yyyy ", Locale.getDefault());
        String partDate = partDateFormat.format(new Date(timestamp));

        stringBuilder.append(partDate);
        stringBuilder.append("00:00");
        try {
            processedDate = format.parse(stringBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return processedDate.getTime();
    }

    /**
     * Преобразует unix timestamp в миллисекундах в дату-строку с учетом "сегодня", "вчера", например, на вход 1508987873000(26 октября 2017 г., 10:17:53) выход "26 октября 2017 г." или "сегодня" если код выполняется 26 октября 2017 г.
     *
     * @param timestamp unix timestamp в миллисекундах.
     * @return дату-строку в формате "d MMM yyyy г." или "сегодня" или "вчера".
     */
    public String getDateStr(long timestamp) {
        String result;
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        long todayMilisec = processDate(today.getTimeInMillis());
        //     Log.w(LOG,"todayMilisec " + todayMilisec);
        long yesterdayMilisec = processDate(yesterday.getTimeInMillis());
        //     Log.w(LOG,"yesterdayMilisec " + yesterdayMilisec);
        long incomingDate = processDate(timestamp);
        //     Log.w(LOG,"incomingDate " + incomingDate);

        if (incomingDate == todayMilisec) {
            result = "сегодня";
        } else if (incomingDate == yesterdayMilisec) {
            result = "вчера";
        } else {
            result = new SimpleDateFormat("d MMM yyyy г.").format(new Date(incomingDate));
        }
        return result;
    }

    /**
     * Проверяет две даты в миллисекундах принадлежат ли они одному и тому же дню.
     *
     * @param currentDate  unix timestamp проверяемой дыты в миллисекундах.
     * @param previousDate unix timestamp предыдущей даты(для ранее полученного сообщения) в миллисекундах.
     * @return возвращает true если даты принадлежат одному и тому же дню, иначе вернет false.
     */
    public boolean isDatesBelongToSameDay(long currentDate, long previousDate) {
        if (previousDate == 0) {
            return false;
        }
        long currentDateProcessed = processDate(currentDate);
        long previousDateProcessed = processDate(previousDate);

        if (currentDateProcessed == previousDateProcessed) {
            return true;
        }
        return false;
    }

    /**
     * Преобразует миллисекунды в строку формата "mm:ss".
     *
     * @param duration миллисекунды, которое необходимо преобразовать.
     * @return строку в формате "mm:ss".
     */
    public String formatDurationAsMinAndSec(long duration) {
        int durationSec = (int) duration / 1000;
        int min = durationSec / 60;
        int sec = durationSec % 60;

        return String.format("%02d:%02d", min, sec);
    }
}
