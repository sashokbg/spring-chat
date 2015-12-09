package bg.alexander.chat.controllers.formatters;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class JsonDateSerializer extends JsonSerializer<LocalTime> {
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Override
	public void serialize(LocalTime time, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		String formattedDate = dateFormat.format(time);
		gen.writeString(formattedDate);
	}
}
