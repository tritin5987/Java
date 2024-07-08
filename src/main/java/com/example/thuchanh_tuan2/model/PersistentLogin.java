package com.example.thuchanh_tuan2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;


import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String series;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(nullable = false)
    private Date lastUsed;

    public PersistentLogin(PersistentRememberMeToken token) {
        this.username = token.getUsername();
        this.series = token.getSeries();
        this.token = token.getTokenValue();
        this.lastUsed = token.getDate();
    }

    public PersistentRememberMeToken toPersistentRememberMeToken() {
        return new PersistentRememberMeToken(username, series, token, lastUsed);
    }
}
