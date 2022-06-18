package grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
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
        try (PreparedStatement ps = cn.prepareStatement("select * from vacancies where id = ?")) {
            try (ResultSet rs = ps.executeQuery()) {
                rsl = createPostOnResultSet(rs);
            }
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
}
