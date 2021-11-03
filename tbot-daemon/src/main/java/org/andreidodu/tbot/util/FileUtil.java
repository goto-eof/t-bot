package org.andreidodu.tbot.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {

	public static <T> void save(String filename, T obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File file = new File(filename);
			mapper.writeValue(file, obj);
			log.info("salvataggio effettuato");
		} catch (IOException e) {
			log.info(e.toString());
		}
	}

	public static <T> T load(String filename, Class<T> typeParameterClass) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			T obj = mapper.readValue(new File(filename), typeParameterClass);
			log.info("lettura effettuata");
			return obj;
		} catch (IOException e) {
			log.info(e.toString());
		}
		return null;
	}

}
