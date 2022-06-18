package grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cn.prepareStatement("insert into vacancies(title, url"
                + ", created_vacancies, description) values(?, ?, ?, ?)")) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getLink());
            ps.setTimestamp(3, Timestamp.valueOf(post.getCreated()));
            ps.setString(4, post.getDescription());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement("select * from vacancies")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rsl.add(createPostOnResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
        }

    @Override
    public Post findById(int id) {
        Post rsl = null;
        try (PreparedStatement ps = cn.prepareStatement("select * from vacancies where id_vacancies = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                rsl = createPostOnResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public boolean deleteAllPost() {
        boolean rsl = false;
        try (PreparedStatement ps = cn.prepareStatement("delete from vacancies")) {
            rsl = ps.executeUpdate() != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    private Post createPostOnResultSet(ResultSet rs) throws SQLException {
        return new Post(rs.getInt(1), rs.getString(2),
                rs.getString(3), rs.getTimestamp(4).toLocalDateTime(), rs.getString(5));
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(properties);
        psqlStore.save(new Post("title1", "", LocalDateTime.now(), "yyyy"));
        psqlStore.save(new Post("title2", "test2", LocalDateTime.now(), "xxxxx"));
        psqlStore.save(new Post("title3", "test1", LocalDateTime.now(), "zzzzz"));
        List<Post> allPost = psqlStore.getAll();
        allPost.forEach(System.out::println);
        System.out.println(psqlStore.findById(allPost.get(1).getId()));
        psqlStore.deleteAllPost();
    }
}
