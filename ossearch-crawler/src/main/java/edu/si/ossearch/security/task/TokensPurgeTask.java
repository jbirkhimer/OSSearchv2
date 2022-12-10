package edu.si.ossearch.security.task;

import edu.si.ossearch.security.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
@Transactional
public class TokensPurgeTask {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${ossearch.jwt.token-purge-task.cron.expression}")
    public void purgeExpired() {
        Instant now = Instant.now();
        log.warn("Purge Expired Refresh Tokens: ", now);
        refreshTokenRepository.deleteByExpiryDateLessThan(now);
        log.warn("Finished Purging Expired Refresh Tokens");
    }
}
