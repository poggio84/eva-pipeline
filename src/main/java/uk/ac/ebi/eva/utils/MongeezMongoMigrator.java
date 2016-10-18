package uk.ac.ebi.eva.utils;

import com.mongodb.Mongo;
import org.mongeez.Mongeez;
import org.springframework.core.io.ClassPathResource;

import java.net.UnknownHostException;

/**
 * Created by diego on 18/10/2016.
 */
public class MongeezMongoMigrator {

    public static void main(String[] args) throws UnknownHostException {
        Mongeez mongeez = new Mongeez();
        mongeez.setFile(new ClassPathResource("/mongeez.xml"));
        mongeez.setMongo(new Mongo("127.0.0.1", 27017));
        mongeez.setDbName("dbName");
        mongeez.process();
    }

}
