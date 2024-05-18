package com.justdo.plug.post.global.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateParser {

    public static String dateTimeParse(LocalDateTime now) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm분");

        String formatDate = now.format(formatter);

        return formatDate.replace("AM", "오전").replace("PM", "오후");
    }
}
