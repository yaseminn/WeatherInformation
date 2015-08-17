import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class TimeCompare {
	Date d1;
	Date d2;
	Date d3;

	public TimeCompare(String referenceDate, String dataDate1, String dataDate2)
			throws ParseException {
		this.d1 = new SimpleDateFormat("yyyy-MM-dd").parse(dataDate1);
		this.d2 = new SimpleDateFormat("yyyy-MM-dd").parse(dataDate2);
		this.d3 = new SimpleDateFormat("yyyy-MM-dd").parse(referenceDate);
	}

	public boolean isDateInInterval() {
		boolean result;
		
		if ((d1.compareTo(d3) <= 0) && (d2.compareTo(d3) >= 0)) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}
}