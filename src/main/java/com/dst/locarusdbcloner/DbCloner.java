package com.dst.locarusdbcloner;

import com.dst.locarusdbcloner.response.LocarusDataField;
import com.dst.locarusdbcloner.response.Message;
import com.mongodb.client.MongoClients;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DbCloner {

//    private static final String connectionStr = "mongodb://root:root@localhost:27017/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false";
    private static final String connectionStr = "mongodb://root:root1@192.168.210.235:27017/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false";
    private static final String dbName = "wheel_loaders";
    private static final String dbLoadersList = "loaders_list";
    private static final String collAllLoaders = "all_loaders";

    public static final MongoOperations mongoOps = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(connectionStr), dbName));
    public static final MongoOperations mongoOpsLoadList = new MongoTemplate(new SimpleMongoClientDatabaseFactory(MongoClients.create(connectionStr), dbLoadersList));

    public void operate(){
        for (Loader loader: mongoOpsLoadList.findAll(Loader.class, collAllLoaders)){
            String loaderNum = String.valueOf(loader.getSerialNumber());
//                System.out.println(loaderNum);
            String locNum = loader.getLocarusNumber();
            // Today, now
            Instant today = Instant.now().truncatedTo(ChronoUnit.MILLIS);
            if (mongoOps.collectionExists(loaderNum)){
                System.out.println("Loader #" + loader.getSerialNumber());
//                    System.out.println("Now: " + today);
                // Получить последнюю запись по времени
                Query query = new Query().with(Sort.by(Sort.Direction.DESC, "time")).limit(1);
                String lastTime = mongoOps.find(query, LocarusDataField.class, loaderNum).get(0).getTime()/*.toString()*/;
                System.out.println("Last record: " + lastTime);
//                    String locNum = mongoOps.find(query, LocarusDataField.class, loaderNum).get(0).getObjectID();
                List<String> urls = LocarusJsonHelper.getQueriesList(locNum, lastTime, today);
                for (String url: urls) {
                    System.out.println("Getting URL: " + url);
                    Message message = LocarusJsonHelper.getMessage(url);
                    if (message != null) {
                        if (message.getDescription() == null) {
                            for (LocarusDataField ldf : message.getResult().getData()) {
                                mongoOps.insert(ldf, loaderNum);
//                            System.out.println("Inserted: " + ldf.getTime().toString());
                            }
                            System.out.println("Inserted " + message.getResult().getData().size() + " documents.");
                        } else System.out.println(message.getDescription() + " Code: " + message.getCode());
                    }
                }
            } else {
                System.out.println("Creating new collection...");
                String dateFrom = loader.getRecordingSince();
                List<String> urls = LocarusJsonHelper.getQueriesList(locNum, dateFrom + "T00:00:00Z", today);
                System.out.println(urls);
                for (String url: urls) {
                    System.out.println("Getting URL: " + url);
                    Message message = LocarusJsonHelper.getMessage(url);
                    if (message != null) {
                        if (message.getDescription() == null) {
                            mongoOps.indexOps(loaderNum).ensureIndex(new Index().on("time", Sort.Direction.ASC).unique());
                            for (LocarusDataField ldf : message.getResult().getData()) {
                                try {
                                    mongoOps.insert(ldf, loaderNum);
                                } catch (DataIntegrityViolationException ignored) {}
                            }
//                            message.getResult().getData().forEach(l -> mongoOps.insert(l, loaderNum));
                            System.out.println("Inserted " + message.getResult().getData().size() + " documents.");
                            System.out.println("Created collection: " + loaderNum);
                        } else System.out.println(message.getDescription());
                    }
                }
            }
        }
    }
}
