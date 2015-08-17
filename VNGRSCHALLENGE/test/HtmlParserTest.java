import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class HtmlParserTest {

	private static final String CONTENTATT = "<b>max</b> : <span>21°</span>";
	private static final String URL = "https://convertale.com/challenge/v1/weather/all.html";
	private static final String CONTENT = "<!DOCTYPE HTML> \n"
			+ "<html>\n"
			+ "\t<head>\n"
			+ "\t\t<title></title>\n"
			+ "\t\t<meta http-equiv=\"content-type\" content=\"\text/html; charset=utf-8\" />\n"
			+ "\t</head>\n" 
			+ "\t<body>\n" 
			+ "\t\t<h1>DAY : 2012-12-01</h1>\n" 
			+ "\t\t<section>\n"
			+ "\t\t\t<div id=\"mean\">\n" 
			+ "\t\t\t\t<b>mean</b> : <span>16°</span>\n" 
			+ "\t\t\t</div>\n"
			+ "\t\t\t<div id=\"max\">\n" 
			+ "\t\t\t\t<b>max</b> : <span>18°</span>\n" 
			+ "\t\t\t</div>\n"
			+ "\t\t\t<div id=\"min\">\n" 
			+ "\t\t\t\t<b>min</b> : <span>14°</span>\n"
			+ "\t\t\t</div>\n"
			+ "\t\t</section>\n" 
			+ "\t</body>\n" 
			+ "</html>";

	private static final String CONTENT1 =	"<!DOCTYPE HTML>"
	+"<html>"
	+"<head>"
	+"<title></title>"
	+"<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />"
	+"</head>"
	+"<body>"
	+"<h1>DAY : 2012-12-01</h1>"
	+"<section>"
	+"<div id=\"mean\">"
	+"<b>mean</b> : <span>16°</span>"
	+"</div>"
	+"<div id=\"max\">"
	+"<b>max</b> : <span>18°</span>"
	+"</div>"
	+"<div id=\"min\">"
	+"<b>min</b> : <span>14°</span>"
	+"</div>"
	+"</section>"
	+"</body>"
	+"</html>";
	@Test
	public void getAttributesTest() {
		String result = HtmlParser.getAttributes(CONTENTATT, "max");
		Assert.assertEquals("21", result);
	}

	@Test
	public void extractUrlsTest() {
		List<String> urlList = new ArrayList<>();
		List<String> result = new ArrayList<>();
		urlList.add("<a href=\"http://www.convertale.com/challenge/intern/weather/2012-12-01.html\">2012-12-01.html</a>");
		result = HtmlParser.extractUrls(urlList);

		Assert.assertEquals("http://www.convertale.com/challenge/intern/weather/2012-12-01.html",result.get(0));

	}

	@Test
	public void dateMatcherTest() {
		String url = "<a href=\"http://www.convertale.com/challenge/intern/weather/2012-12-01.html\">2012-12-01.html</a>";
		String result = HtmlParser.dateMatcher(url);
		Assert.assertEquals("2012-12-01",  result);
	}
	
	@Test
	public void getUrlsContentTest() throws IOException{
		String url = "http://www.convertale.com/challenge/intern/weather/2012-12-01.html";
		String result = HtmlParser.getUrlsContent(url);
		System.out.println(result);
		System.out.println("---");
		System.out.println(readFile());
		Assert.assertEquals(readFile(),result);
	}
	
	@Test
	public void getUrlsTest() throws IOException{
		
		List<String> urlList = new ArrayList<>();
		urlList = HtmlParser.getUrls(URL);
		System.out.println(urlList.get(1));
		Assert.assertEquals("<a href=\"http://www.convertale.com/challenge/intern/weather/2012-12-01.html\">2012-12-01.html</a>", urlList.get(0));
		Assert.assertEquals("<a href=\"http://www.convertale.com/challenge/intern/weather/2012-12-16.html\">2012-12-16.html</a>",urlList.get(15).replaceAll("\t", ""));
	}
	
	/*
	@Test
	public void parseHtmlToWeatherInfoTest() throws IOException, ParseException{
		List<WeatherInfo> weathersList = HtmlParser.parseHtmlToWeatherInfo(URL, "2014-05", "2014-06");
		Assert.assertEquals("2014-05-01 min: 11 max: 13 mean: 12",weathersList.get(0).toString());
	}
*/
	
	public String readFile() throws IOException{
		FileInputStream inputStream = new FileInputStream("foo.txt");
		String content = null;
		try {
		    content = IOUtils.toString(inputStream);
		} finally {
		    inputStream.close();
		}
		
		return content;
	}
}