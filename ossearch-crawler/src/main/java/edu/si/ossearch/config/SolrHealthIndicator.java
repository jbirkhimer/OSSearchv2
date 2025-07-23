package edu.si.ossearch.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author jbirkhimer
 */
@Component("solrHealthIndicator")
public class SolrHealthIndicator implements HealthIndicator {

    @Autowired
    @Qualifier("master")
    private SolrClient masterClient;

    @Autowired
    @Qualifier("slave")
    private SolrClient slaveClient;

    @Value(value = "${spring.data.solr.collection}")
    @NonNull
    String solrCollection;

    @Override
    public Health health() {
        Health.Builder builder = Health.up();

        // Master check
        try {
            SolrPingResponse ping = masterClient.ping(solrCollection);
            if (ping.getStatus() == 0) {
                builder.withDetail("master.status", "UP");
                builder.withDetail("master.qTime", ping.getQTime());
            } else {
                builder.down().withDetail("master.status", "DOWN");
            }
        } catch (Exception e) {
            builder.down().withDetail("master.error", e.getMessage());
        }

        // Slave check
        try {
            SolrPingResponse ping = slaveClient.ping(solrCollection);
            if (ping.getStatus() == 0) {
                builder.withDetail("slave.status", "UP");
                builder.withDetail("slave.qTime", ping.getQTime());
            } else {
                builder.down().withDetail("slave.status", "DOWN");
            }
        } catch (Exception e) {
            builder.down().withDetail("slave.error", e.getMessage());
        }

        return builder.build();
    }
}
