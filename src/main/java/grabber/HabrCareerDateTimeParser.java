package grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.parse(parse, ISO_OFFSET_DATE_TIME);
    }

    public static void main(String[] args) {
        DateTimeParser hr = new HabrCareerDateTimeParser();
        System.out.println(hr.parse("2022-06-15T15:24:03+03:00"));
    }
}
