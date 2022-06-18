package grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    private static final int COUNT_PAGE = 5;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) {
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
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        for (int i = 0; i < COUNT_PAGE; i++) {
            Elements rows;
            try {
                rows = Jsoup.connect(link + "?page=" + i).get().select(".vacancy-card__inner");
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
            rows.forEach(row -> rsl.add(generatePost(row)));
        }
        return rsl;
    }

    private Post generatePost(Element elem) {
        Post post = null;
        try {
            Element titleElem = elem.select(".vacancy-card__title").first();
            String name = titleElem.text();
            String url = String.format("%s%s", SOURCE_LINK, titleElem.child(0).attr("href"));
            String desc = retrieveDescription(url);
            LocalDateTime dateVacancy = dateTimeParser.parse(elem.select(".vacancy-card__date")
                    .first().child(0).attr("datetime"));
            post = new Post(name, url, dateVacancy, desc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }
}