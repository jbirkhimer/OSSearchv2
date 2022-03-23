package edu.si.ossearch.nutch.kafka.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;

/**
 * @author jbirkhimer
 */
@Component
@Profile("camel")
public class NutchKafkaConsumerCamelRoutes extends RouteBuilder {
    /**
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement the routes using the Java fluent builder
     * syntax.
     *
     * @throws Exception can be thrown during configuration
     */
    @Override
    public void configure() throws Exception {

        from("kafka:{{nutch.kafka.topic-name}}").routeId("singleKafkaMessageConsumerRouteWithBatchAggregation")
                //.autoStartup(false)
                .log(LoggingLevel.DEBUG, "Headers:\n${headers}\nBody:\n${body}")

                //Process Kafka Headers
                .process(exchange -> {
                    Message out = exchange.getIn();

                    //Loop over raw headers
                    out.getHeader("kafka.HEADERS", RecordHeaders.class).forEach(header -> {
                        log.debug("header key {} header value {}", header.key(), new String(header.value()));
                        if (header.key().equals("record_id")) {
                            out.setHeader("recordID", new String(header.value()));
                        }
                    });

                    //Get kafka header mapped to camel header
                    out.setHeader("recordID", out.getHeader("record_id", String.class));
                    log.debug("debug here");
                })

                .process(exchange -> {
                    Message out = exchange.getIn();
                    String host = out.getBody(JSONObject.class).getJSONArray("host").getString(0);
                    host = URLEncoder.encode(host, "UTF-8").replace(".", "_");
                    out.setHeader("host", host);

                    String id = out.getBody(JSONObject.class).getJSONArray("id").getString(0);
                    id = URLEncoder.encode(id, "UTF-8");
                    String fileName = id + "_" + out.getHeader("kafka.TIMESTAMP", String.class) + ".json";
                    out.setHeader(Exchange.FILE_NAME, fileName);
                })

                .toD("seda:aggregateRecords?waitForTaskToComplete=Never&blockWhenFull=true&timeout=0")

                .convertBodyTo(String.class)
                .toD("file:{{nutch.kafka.output-dir}}/raw/${header.host}")
                .log(LoggingLevel.DEBUG, "output file: ${header.CamelFileNameProduced}");

        from("seda:aggregateRecords")
                .aggregate(header("host"), new GroupedBodyAggregationStrategy())
                    .completionSize(constant("{{camel.component.kafka.max-poll-records}}"))
                    .completionTimeout(constant("{{camel.batch.completion.timeout}}"))
                    .completeAllOnStop()
                    .forceCompletionOnStop()
                    .parallelProcessing()

                    .process(exchange -> exchange.getIn().setHeader("timestamp", System.currentTimeMillis()))

                    .setHeader(Exchange.FILE_NAME).simple("{{nutch.kafka.output-dir}}/batch/${header.host}/batch_${header.timestamp}")

                    .log(LoggingLevel.INFO, "Batch count: ${body.size}, file: ${header.CamelFileName}")

                    .split(body())
                        .toD("stream:file?closeOnDone=true&fileName=${header.CamelFileName}")
                    .end()
                    .log(LoggingLevel.INFO, "batch output file: ${header.CamelFileName}");


        /*from("kafka:{{nutch.kafka.topic-name}}?" +
                "groupId={{spring.kafka.consumer.group-id}}" +
                "&consumersCount=1" +
                "&maxPollRecords={{spring.kafka.consumer.max-poll-records}}" +
                "&autoOffsetReset={{spring.kafka.consumer.auto-offset-reset}}" +
                "&autoCommitEnable=false" +
                "&allowManualCommit=true" +
                "&breakOnFirstError=true").autoStartup(false)
                .routeId("batchKafkaConsumerRoute")

                .log(LoggingLevel.INFO, "Headers:\n${headers}\nBody:\n${body}")
                .process(exchange -> {
                    Message out = exchange.getIn();
                    log.debug("debug here");
                })

                .process(exchange -> {
                    // manually commit offset if it is last message in batch
                    Boolean lastOne = exchange.getIn().getHeader(KafkaConstants.LAST_RECORD_BEFORE_COMMIT, Boolean.class);
                    if (lastOne) {
                        KafkaManualCommit manual = exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                        if (manual != null) {
                            log.info("manually committing the offset for batch");
                            manual.commitSync();
                        } else {
                            log.info("NOT time to commit the offset yet");
                        }
                    }
                });*/


    }
}
