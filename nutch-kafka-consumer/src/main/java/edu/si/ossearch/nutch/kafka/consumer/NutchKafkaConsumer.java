package edu.si.ossearch.nutch.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jbirkhimer
 */
@Slf4j
@Service
@Profile("kafkaListener")
public class NutchKafkaConsumer {

    @Value(value = "${nutch.kafka.output-dir}")
    private String outputDir;

    @KafkaListener(id = "${spring.kafka.consumer.client-id}", topics = "${nutch.kafka.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload List<String> messages,
//                        @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) Integer key,
//                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
                        @Headers MessageHeaders messageHeaders
    ) throws IOException {

        log.debug("Consumed message: {}", messages);

        List<Object> kafka_batchConvertedHeaders = new ArrayList<>();

        messageHeaders.keySet().forEach(key -> {
            Object value = messageHeaders.get(key);
            if (key.equals("X-Custom-Header")){
                log.debug("{}: {}", key, new String((byte[])value));
            } else if (key.equals("kafka_batchConvertedHeaders")){
                if (value instanceof ArrayList) {
                    kafka_batchConvertedHeaders.addAll((ArrayList) value);
                }
                log.debug("debug here");
            } else {
                log.debug("{}: {}", key, value);
            }
        });
//        String ts = null;

        Files.createDirectories(Paths.get(outputDir + "/batch"));
        String fileName = outputDir + "/batch/batch_" + ts;

        try {
            log.info("Batch count: {}, file: {}}", messages.size(), fileName);
            Files.write(Paths.get(fileName), messages);
        } catch (IOException e) {
            log.error("Unable to write batch {}", fileName, e);
        }

        Files.createDirectories(Paths.get(outputDir + "/raw"));

        messages.forEach(message -> {
            String id = null;
            String file_name = null;
            try {
                id = new JSONObject(message).getJSONArray("id").getString(0);
                id = URLEncoder.encode(id, "UTF-8");
                file_name = id + "_" + ts + ".json";
                Files.write(Paths.get(outputDir + "/raw/" + file_name), message.getBytes(StandardCharsets.UTF_8));
            } catch (UnsupportedEncodingException e) {
                log.error("Unable encode id: {} for record", id, e);
            } catch (IOException e) {
                log.error("Unable to write raw record file: {}", file_name, e);
            }
        });
    }
}
