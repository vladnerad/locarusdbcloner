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
    private static final String connectionStr = "mongodb://root:root@localhost:27017/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false";
    private static final String dbName = "wheel_loaders";

    public static final MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(connectionStr), dbName));

    public static void main(String[] args) {
//        SpringApplication.run(LocarusdbclonerApplication.class, args);

//        MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(connectionStr), dbName));
//        addLoader("2294", "4OH027034", "2020-09-23", mongoOps);

        while (true) {
//            logger.info("New cycle");
            for (String loaderCollection : mongoOps.getCollectionNames()) {
                if (!loaderCollection.equals("1111")) {
                    System.out.println("Loader #" + loaderCollection);
                    // Today, now
                    Instant today = Instant.now().truncatedTo(ChronoUnit.MILLIS);
                    System.out.println("Now: " + today);
                    // Получить последнюю запись по времени
                    Query query = new Query().with(Sort.by(Sort.Direction.DESC, "time")).limit(1);
                    String lastTime = mongoOps.find(query, LocarusDataField.class, loaderCollection).get(0).getTime().toString();
                    System.out.println("Last record: " + lastTime);
                    String locNum = mongoOps.find(query, LocarusDataField.class, loaderCollection).get(0).getObjectID();

                    Message message = new LocarusJsonHelper(locNum, lastTime, today).getMessage();
                    if (message.getDescription() == null) {
                        for (LocarusDataField ldf : message.getResult().getData()) {
                            mongoOps.insert(ldf, loaderCollection);
//                            System.out.println("Inserted: " + ldf.getTime().toString());
                        }
                        System.out.println("Inserted " + message.getResult().getData().size() + " documents.");
                    } else System.out.println(message.getDescription());
                }
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
