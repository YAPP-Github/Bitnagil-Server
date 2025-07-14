package bitnagil.bitnagil_backend.global.utils;

import java.time.LocalDateTime;

public class TimeUtils {
    public static final LocalDateTime END_DATE_TIME =
        LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    public static final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.now();
}
