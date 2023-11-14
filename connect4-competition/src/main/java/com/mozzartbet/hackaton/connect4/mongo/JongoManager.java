package com.mozzartbet.hackaton.connect4.mongo;

import static com.mongodb.MongoCredential.*;
import static java.util.Arrays.*;

import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;

import com.fasterxml.jackson.databind.MapperFeature;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class JongoManager {

	static final String DB_NAME = "connect4-hackaton";

	enum JongoHolder {
		INSTANCE;

		final MongoClient client;
		final DB db;
		final Jongo jongo;

		@SuppressWarnings("deprecation")
		JongoHolder() {
			client = new MongoClient(new ServerAddress("localhost", 27017),
					asList(createCredential("root", "admin", "root!".toCharArray())));
			db = client.getDB(DB_NAME);
			jongo = new Jongo(db,
					new JacksonMapper.Builder()
							.registerModule(new JongoTimeModule())
							.enable(MapperFeature.AUTO_DETECT_GETTERS)
							.build());
		}
	}

	public static Jongo jongo() {
		return JongoHolder.INSTANCE.jongo;
	}

}
