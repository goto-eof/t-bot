package org.andreidodu.tbot.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

@Component
public class ExceptionUtils {

	public String getStackTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		printWriter.close(); // surprise no IO exception here
		try {
			stringWriter.close();
		} catch (IOException e) {
			//
		}
		return stringWriter.toString();
	}

}
