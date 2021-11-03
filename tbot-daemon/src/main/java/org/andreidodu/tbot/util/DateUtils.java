package org.andreidodu.tbot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class DateUtils {

	public String formateDateHumanable(String dateStr, String dateFormatFrom, String localeFrom, String dateFormatTo, String localeTo) {
		Locale local = calculateLocale(localeFrom);
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(dateFormatFrom, local);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(dateFormatTo, calculateLocale(localeTo));
		LocalDateTime date = LocalDateTime.parse(dateStr, inputFormatter);
		String formattedDate = outputFormatter.format(date);
		return formattedDate;
	}

	public String formateDate(String dateStr, String dateFormat, String locale) {
		Locale local = calculateLocale(locale);
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(dateFormat, local);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY);
		LocalDate date = LocalDate.parse(dateStr, inputFormatter);
		String formattedDate = outputFormatter.format(date);
		return formattedDate;
	}

	public String todayToString() {
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ITALY);
		return outputFormatter.format(LocalDateTime.now());
	}

	public Date toDate(String pattern, String locale, String dateStr) throws ParseException {
		return org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, calculateLocale(locale), pattern);
	}

	public Locale calculateLocale(String locale) {
		switch (locale) {
		case "ITALY":
			return Locale.ITALY;
		case "UK":
			return Locale.UK;
		default:
			return Locale.UK;
		}
	}

	public Date parseDate(List<String> dateFormatsFrom, String dateLocalFrom, String dateStr) throws ParseException {
		String[] itemsArray = new String[dateFormatsFrom.size()];
		String[] dtFormats = dateFormatsFrom.toArray(itemsArray);
		Date date = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, calculateLocale(dateLocalFrom), dtFormats);
		return date;
	}

	public int toSeconds(String dateStr) throws ParseException {
		Date date = new SimpleDateFormat("HH:mm:ss").parse(dateStr);
		return dateTimeToSeconds(date);
	}

	public int toSeconds(Date date) throws ParseException {
		return dateTimeToSeconds(date);
	}

	private int dateTimeToSeconds(Date date) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date);
		int hhStart = calendar1.get(Calendar.HOUR_OF_DAY);
		int mmStart = calendar1.get(Calendar.MINUTE);
		int ssStart = calendar1.get(Calendar.SECOND);
		int totSeconds = (hhStart * 60 + mmStart) * 60 + ssStart;
		return totSeconds;
	}

	public boolean isNowInInterval(String configPrefix, String date1Str, String date2Str) {
		try {
			int startSeconds = this.toSeconds(date1Str);
			int currentSeconds = this.toSeconds(new Date());
			int endSeconds = this.toSeconds(date2Str);
			boolean caseA = endSeconds > startSeconds && currentSeconds >= startSeconds && currentSeconds <= endSeconds;
			boolean caseB = endSeconds < startSeconds && (currentSeconds >= startSeconds || currentSeconds <= endSeconds);
			boolean caseC = endSeconds == startSeconds && startSeconds == currentSeconds;
			boolean isIn = caseA || caseB || caseC;
			return isIn;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String argv[]) {
		DateUtils du = new DateUtils();
		try {
			System.out.println(du.toSeconds("00:00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
