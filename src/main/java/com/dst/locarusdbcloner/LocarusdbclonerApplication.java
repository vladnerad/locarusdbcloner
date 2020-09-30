package com.dst.locarusdbcloner;

import com.dst.locarusdbcloner.response.LocarusDataField;
import com.dst.locarusdbcloner.response.Message;
import com.mongodb.client.MongoClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class LocarusdbclonerApplication {

    private static final Logger logger = LogManager.getLogger(LocarusdbclonerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LocarusdbclonerApplication.class, args);

        MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(), "wheel_loaders"));
        addLoader("1111", "4OH027034", "2020-09-23", mongoOps);

        while (true) {
//            logger.info("New cycle");
            for (String loaderCollection : mongoOps.getCollectionNames()) {
                // Today, now
                Instant today = Instant.now().truncatedTo(ChronoUnit.MILLIS);
                System.out.println("Now: " + today);
                // Получить последнюю запись по времени
                Query query = new Query().with(Sort.by(Sort.Direction.DESC, "time")).limit(1);
                String lastTime = mongoOps.find(query, LocarusDataField.class, loaderCollection).get(0).getTime().toString();
                System.out.println("Last record:" + lastTime);
                String locNum = mongoOps.find(query, LocarusDataField.class, loaderCollection).get(0).getObjectID();

                Message message = new LocarusJsonHelper(locNum, lastTime, today).getMessage();
                if (message.getDescription() == null) {
                    for (LocarusDataField ldf : message.getResult().getData()) {
                        mongoOps.insert(ldf, loaderCollection);
                        System.out.println("Inserted" + ldf.getTime().toString());
                    }
                } else System.out.println(message.getDescription());
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addLoader(String dozerNum, String locNum, String dayFrom, MongoOperations mongoOps) {
        if (!mongoOps.collectionExists(dozerNum)) {
            Instant today = Instant.now().truncatedTo(ChronoUnit.MILLIS);
            Message message = new LocarusJsonHelper(locNum, dayFrom, today).getMessage();

            if (message.getDescription() == null) {
                for (LocarusDataField ldf : message.getResult().getData()) {
                    mongoOps.insert(ldf, dozerNum);
                }
            } else System.out.println(message.getDescription());
        } else {
            System.out.println("Collection already exists: " + dozerNum);
        }
    }
}
