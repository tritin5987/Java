package com.example.thuchanh_tuan2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;

@Repository
public class JdbcPersistentTokenRepository implements PersistentTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcPersistentTokenRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        jdbcTemplate.update("INSERT INTO persistent_logins (username, series, token, last_used) VALUES (?, ?, ?, ?)",
                token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        jdbcTemplate.update("UPDATE persistent_logins SET token = ?, last_used = ? WHERE series = ?",
                tokenValue, lastUsed, series);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT username, series, token, last_used FROM persistent_logins WHERE series = ?",
                    (resultSet, i) -> new PersistentRememberMeToken(
                            resultSet.getString("username"),
                            resultSet.getString("series"),
                            resultSet.getString("token"),
                            resultSet.getTimestamp("last_used")),
                    seriesId
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void removeUserTokens(String username) {
        jdbcTemplate.update("DELETE FROM persistent_logins WHERE username = ?", username);
    }
}
