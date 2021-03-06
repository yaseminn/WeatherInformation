/**
 * @author yasemin
 * Taking the .csv file under the spark-1.3.1-bin-hadoop2.4 
 * Calculating monthly averages of min, max, mean values and saving .txt format 
 * under the spark-1.3.1-bin-hadoop2.4/train_results directory.  
 * 
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class MonthlyAverage {

	public static void main(String[] args) throws IOException, ParseException {
		SparkConf sparkConf = new SparkConf().setAppName("MonthlyAverage");
		JavaSparkContext ctx = new JavaSparkContext(sparkConf);
		ReadConfigFile config = new ReadConfigFile();
		
		// html section
		String url = config.getProperty("url");
		System.out.println("--Getting url : "+url);
		String date1 = config.getProperty("date1");
		System.out.println("--Getting date1 : " + date1);
		String date2 = config.getProperty("date2");
		System.out.println("--Getting date2 : " +date2);
		System.out.println("--Source & config file are read");
		List<WeatherInfo> weathersList = HtmlParser.parseHtmlToWeatherInfo(url,
				date1, date2);
		
		WeatherCSVWriter writer = new WeatherCSVWriter();
		writer.writeCsv(weathersList, "weather.csv");
		
		// spark section
		String path = "weather.csv";

		JavaPairRDD<String, List<Double>> weatherPair = readCsv(ctx, path);
		weatherPair.saveAsTextFile("ResultVNGRS/RawData");
		System.out.println("--Raw Data is obtained");
		
		Map<String, Object> keyCount = weatherPair.countByKey();

		JavaPairRDD<String, List<Double>> sumWeather = sumList(weatherPair);
		sumWeather.saveAsTextFile("ResultVNGRS/sumWeather");
		System.out.println("--The Sum of Max & Min & Mean are obtained");
		
		JavaPairRDD<String, List<Double>> averageWeatherWeather = averageList(
				sumWeather, keyCount);
		averageWeatherWeather.saveAsTextFile("ResultVNGRS/averageWeather");
		System.out.println("--The Average of Max & Min & Mean are obtained");
		
		PrintWriter writer2 = new PrintWriter("train_results/result.txt",
				"UTF-8");
		for (Tuple2<?, ?> tuple : averageWeatherWeather.collect()) {
			System.out.println("averageWeatherWeather " + tuple._1() + ": "
					+ tuple._2());
			writer2.println(tuple._1() + ":" + tuple._2());
		}
		
		System.out.println("--The Train Results are writed");
		writer2.close();
	}

	/*
	 * Reading .csv file from given path, saving to RDD format.
	 */
	public static JavaPairRDD<String, List<Double>> readCsv(
			JavaSparkContext ctx, String filePath) {

		JavaPairRDD<String, List<Double>> weatherLogs = ctx.textFile(filePath)
				.mapToPair(new PairFunction<String, String, List<Double>>() {

					@Override
					public Tuple2<String, List<Double>> call(String arg0)
							throws Exception {
						String[] splitted = arg0.split(",", -1);

						List<Double> result = new ArrayList<Double>();

						for (int i = 1; i < splitted.length; ++i) {
							result.add(Double.valueOf((splitted[i])));
						}

						String[] splittedDate = splitted[0].split("-");

						return new Tuple2<String, List<Double>>(splittedDate[0]
								+ "-" + splittedDate[1], result);
					}
				});

		
		return weatherLogs;

	}

	/*
	 * Caltulating the monthly sum of given PairRDD
	 */
	public static JavaPairRDD<String, List<Double>> sumList(
			JavaPairRDD<String, List<Double>> weatherPair) {

		JavaPairRDD<String, List<Double>> sumWeather = weatherPair
				.reduceByKey(new Function2<List<Double>, List<Double>, List<Double>>() {

					@Override
					public List<Double> call(List<Double> l1, List<Double> l2)
							throws Exception {
						List<Double> result = new ArrayList<Double>();

						for (int i = 0; i < l1.size(); ++i) {
							result.add(l1.get(i) + l2.get(i));
						}

						return result;
					}
				});

		
		return sumWeather;
	}

	/*
	 * Caltulating the average of monthly values
	 */
	public static JavaPairRDD<String, List<Double>> averageList(
			JavaPairRDD<String, List<Double>> sumWeather,
			final Map<String, Object> keyCount) {

		JavaPairRDD<String, List<Double>> averageWeather = sumWeather
				.mapToPair(new PairFunction<Tuple2<String, List<Double>>, String, List<Double>>() {

					@Override
					public Tuple2<String, List<Double>> call(
							Tuple2<String, List<Double>> arg0) throws Exception {

						List<Double> result = new ArrayList<Double>();

						for (Entry<String, Object> entry : keyCount.entrySet()) {
							if (entry.getKey().equalsIgnoreCase(arg0._1())) {
								for (Double d : arg0._2()) {
									result.add(Math.floor((d / Integer
											.parseInt(entry.getValue()
													.toString())) * 100) / 100);
								}
							}
						}

						return new Tuple2<String, List<Double>>(arg0._1(),
								result);
					}
				});

		
		return averageWeather;

	}

}
