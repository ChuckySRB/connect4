package com.mozzartbet.hackaton.connect4.mongo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JongoTimeModule extends SimpleModule {

	private static final long serialVersionUID = 1L;

	public JongoTimeModule() {
		addDeserializer(LocalDateTime.class, new JongoLocalDateTimeDeserializer());
		addSerializer(LocalDateTime.class, new JongoLocalDateTimeSerializer());
	}

	public static class JongoLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
		@Override
		public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d H:m:s z uuuu");
			ZonedDateTime dateTime = ZonedDateTime.parse(p.getText(), formatter);
			return LocalDateTime.ofInstant(dateTime.toInstant(), dateTime.getZone());
		}
	};

	public class JongoLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
		@Override
		public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			Date date  = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
			gen.writeObject(date);
		}

	};
}