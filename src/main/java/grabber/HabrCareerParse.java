package grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        DateTimeParser dtp = new HabrCareerDateTimeParser();
        Parse parse = new HabrCareerParse(dtp);
        parse.list(PAGE_LINK).forEach(System.out::println);
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".style-ugc");
        return rows.text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        Elements rows = Jsoup.connect(link).get().select(".vacancy-card__inner");
        List<Post> rsl = new ArrayList<>();
        for (Element e : rows) {
            Element titleElem = e.select(".vacancy-card__title").first();
            String name = titleElem.text();
            String url = String.format("%s%s", SOURCE_LINK, titleElem.child(0).attr("href"));
            String desc = retrieveDescription(url);
            LocalDateTime dateVacancy = dateTimeParser.parse(e.select(".vacancy-card__date")
                    .first().child(0).attr("datetime"));
            rsl.add(new Post(name, url, dateVacancy, desc));
        }
        return rsl;
    }
}
