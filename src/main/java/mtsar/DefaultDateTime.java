package mtsar;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class DefaultDateTime {
    public static Timestamp get() {
        return Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}
