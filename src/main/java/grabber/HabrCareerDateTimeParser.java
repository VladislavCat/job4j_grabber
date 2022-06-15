package grabber;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.parse(parse, ISO_ZONED_DATE_TIME);
    }
}
