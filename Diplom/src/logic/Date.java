package logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date {
	private Integer day;
	private String month;
	private Integer year;
	
	public Date() {
		this.day = 1;
		this.month = "������";
		this.year = 1990;
	}

	public Date(Integer day, String month, Integer year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public void setDay(Integer day) {
		this.day = day;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getDay() {
		return day;
	}
	
	public String getMonth() {
		return month;
	}
	
	public Integer getYear() {
		return year;
	}
	
	private static String convertMonth(int mon) {
		switch(mon){
    	case 1: return "������";
    	case 2: return "�������";
    	case 3: return "����";
    	case 4: return "������";
    	case 5: return "���";
    	case 6: return "����";
    	case 7: return "����";
    	case 8: return "������";
    	case 9: return "��������";
    	case 10: return "�������";
    	case 11: return "������";
    	case 12: return "�������";
    	}
		return null;
	}
	
	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
    	int day = cal.get(Calendar.DAY_OF_MONTH);
    	int mon = cal.get(Calendar.MONTH) + 1;
    	String month = convertMonth(mon);
    	int year = cal.get(Calendar.YEAR);
    	
    	Date date = new Date(day, month, year);
    	
    	return date;
	}
	
	@SuppressWarnings("deprecation")
	public static Date convert(String d) {
		Calendar cal = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		java.util.Date result = null;
		try {
			result = df.parse(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.set(result.getYear()+1900, result.getMonth(), result.getDate());
		int day = cal.get(Calendar.DAY_OF_MONTH);
    	int mon = cal.get(Calendar.MONTH) + 1;
    	String month = convertMonth(mon);
    	int year = cal.get(Calendar.YEAR);
		Date date = new Date(day, month, year);
		return date;	
	}
	
	public String toString() {
		if(day == 0 && month == null && year == 0)
			return "";
		return day + " " + month + " " + year;
	}
}

