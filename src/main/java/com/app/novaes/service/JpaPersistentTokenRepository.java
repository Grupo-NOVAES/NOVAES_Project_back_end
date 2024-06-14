package com.app.novaes.service;

import com.app.novaes.model.PersistentLogin;
import com.app.novaes.repository.PersistentLoginRepository;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class JpaPersistentTokenRepository implements PersistentTokenRepository {

    private final PersistentLoginRepository persistentLoginRepository;

    public JpaPersistentTokenRepository(PersistentLoginRepository persistentLoginRepository) {
        this.persistentLoginRepository = persistentLoginRepository;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogin persistentLogin = new PersistentLogin();
        persistentLogin.setSeries(token.getSeries());
        persistentLogin.setUsername(token.getUsername());
        persistentLogin.setToken(token.getTokenValue());
        persistentLogin.setLastUsed(convertToLocalDateTime(token.getDate()));

        persistentLoginRepository.save(persistentLogin);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        Optional<PersistentLogin> optional = persistentLoginRepository.findById(series);
        if (optional.isPresent()) {
            PersistentLogin persistentLogin = optional.get();
            persistentLogin.setToken(tokenValue);
            persistentLogin.setLastUsed(convertToLocalDateTime(lastUsed));

            persistentLoginRepository.save(persistentLogin);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        Optional<PersistentLogin> optional = persistentLoginRepository.findById(seriesId);
        if (optional.isPresent()) {
            PersistentLogin persistentLogin = optional.get();
            return new PersistentRememberMeToken(
                    persistentLogin.getUsername(),
                    persistentLogin.getSeries(),
                    persistentLogin.getToken(),
                    convertToDate(persistentLogin.getLastUsed())
            );
        }
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        persistentLoginRepository.deleteByUsername(username);
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
