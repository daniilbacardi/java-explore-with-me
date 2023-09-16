package ru.practicum.service;

import java.time.format.DateTimeFormatter;

public abstract class CommonConstants {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
}
