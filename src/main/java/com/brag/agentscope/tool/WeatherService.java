package com.brag.agentscope.tool;

import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 天气服务工具类
 * 提供天气查询功能（模拟实现）
 */
@Service
public class WeatherService {

    // 模拟天气数据存储
    private final Map<String, CityWeather> weatherData;
    private final Random random;

    public WeatherService() {
        this.weatherData = new HashMap<>();
        this.random = new Random();

        // 初始化一些城市的天气数据
        initializeWeatherData();
    }

    /**
     * 获取城市的当前天气
     */
    @Tool(description = "获取指定城市的当前天气信息")
    public String getCurrentWeather(
            @ToolParam(name = "city", description = "城市名称（如：北京、上海、广州等）") String city) {

        CityWeather weather = weatherData.get(city.toLowerCase());
        if (weather == null) {
            return String.format("抱歉，暂不支持查询城市：%s 的天气信息", city);
        }

        // 模拟实时性，稍微改变温度
        int tempVariation = random.nextInt(5) - 2; // -2到+2度的随机变化
        int currentTemp = weather.getTemperature() + tempVariation;

        return String.format("%s当前天气：%s，温度：%d°C，湿度：%d%%，风力：%s",
                city, weather.getCondition(), currentTemp,
                weather.getHumidity(), weather.getWindSpeed());
    }

    /**
     * 获取城市的天气预报
     */
    @Tool(description = "获取指定城市的未来几天天气预报")
    public String getWeatherForecast(
            @ToolParam(name = "city", description = "城市名称") String city,
            @ToolParam(name = "days", description = "预报天数（1-7天）") int days) {

        if (days < 1 || days > 7) {
            return "预报天数必须在1-7天之间";
        }

        CityWeather weather = weatherData.get(city.toLowerCase());
        if (weather == null) {
            return String.format("抱歉，暂不支持查询城市：%s 的天气信息", city);
        }

        StringBuilder forecast = new StringBuilder();
        forecast.append(String.format("%s未来%d天气预报：\n", city, days));

        LocalDate today = LocalDate.now();
        for (int i = 1; i <= days; i++) {
            LocalDate date = today.plusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM月dd日"));

            // 模拟天气变化
            String condition = getRandomCondition();
            int tempMin = weather.getTemperature() - random.nextInt(5) - 2;
            int tempMax = tempMin + random.nextInt(8) + 5;

            forecast.append(String.format("%s：%s，%d°C-%d°C\n",
                    dateStr, condition, tempMin, tempMax));
        }

        return forecast.toString().trim();
    }

    /**
     * 获取空气质量信息
     */
    @Tool(description = "获取指定城市的空气质量指数（AQI）")
    public String getAirQuality(
            @ToolParam(name = "city", description = "城市名称") String city) {

        CityWeather weather = weatherData.get(city.toLowerCase());
        if (weather == null) {
            return String.format("抱歉，暂不支持查询城市：%s 的空气质量信息", city);
        }

        // 模拟AQI计算
        int aqi = 30 + random.nextInt(120); // 30-150的随机AQI
        String level = getAqiLevel(aqi);
        String suggestion = getAqiSuggestion(level);

        return String.format("%s空气质量：AQI %d，等级：%s\n建议：%s",
                city, aqi, level, suggestion);
    }

    /**
     * 比较两个城市的天气
     */
    @Tool(description = "比较两个城市的当前天气")
    public String compareWeather(
            @ToolParam(name = "city1", description = "第一个城市名称") String city1,
            @ToolParam(name = "city2", description = "第二个城市名称") String city2) {

        CityWeather weather1 = weatherData.get(city1.toLowerCase());
        CityWeather weather2 = weatherData.get(city2.toLowerCase());

        if (weather1 == null || weather2 == null) {
            String missing = weather1 == null ? city1 : city2;
            return String.format("抱歉，暂不支持查询城市：%s 的天气信息", missing);
        }

        String comparison = String.format("城市天气对比：\n");
        comparison += String.format("%s：%s，%d°C，湿度%d%%\n",
                city1, weather1.getCondition(), weather1.getTemperature(), weather1.getHumidity());
        comparison += String.format("%s：%s，%d°C，湿度%d%%\n",
                city2, weather2.getCondition(), weather2.getTemperature(), weather2.getHumidity());

        int tempDiff = weather1.getTemperature() - weather2.getTemperature();
        if (tempDiff > 0) {
            comparison += String.format("%s比%s高%d°C", city1, city2, tempDiff);
        } else if (tempDiff < 0) {
            comparison += String.format("%s比%s高%d°C", city2, city1, -tempDiff);
        } else {
            comparison += "两个城市的温度相同";
        }

        return comparison;
    }

    /**
     * 初始化天气数据
     */
    private void initializeWeatherData() {
        weatherData.put("北京", new CityWeather("晴", 25, 45, "3级"));
        weatherData.put("上海", new CityWeather("多云", 28, 70, "2级"));
        weatherData.put("广州", new CityWeather("阴", 30, 75, "1级"));
        weatherData.put("深圳", new CityWeather("小雨", 27, 80, "2级"));
        weatherData.put("杭州", new CityWeather("晴", 26, 65, "2级"));
        weatherData.put("南京", new CityWeather("多云", 24, 60, "3级"));
        weatherData.put("武汉", new CityWeather("阴", 29, 72, "2级"));
        weatherData.put("成都", new CityWeather("小雨", 23, 78, "1级"));
        weatherData.put("重庆", new CityWeather("阴", 27, 74, "1级"));
        weatherData.put("西安", new CityWeather("晴", 22, 50, "3级"));
    }

    /**
     * 获取随机天气状况
     */
    private String getRandomCondition() {
        String[] conditions = {"晴", "多云", "阴", "小雨", "中雨", "大雨", "雷阵雨"};
        return conditions[random.nextInt(conditions.length)];
    }

    /**
     * 根据AQI获取等级
     */
    private String getAqiLevel(int aqi) {
        if (aqi <= 50) return "优";
        if (aqi <= 100) return "良";
        if (aqi <= 150) return "轻度污染";
        if (aqi <= 200) return "中度污染";
        if (aqi <= 300) return "重度污染";
        return "严重污染";
    }

    /**
     * 根据AQI等级获取建议
     */
    private String getAqiSuggestion(String level) {
        switch (level) {
            case "优":
            case "良":
                return "空气质量良好，适合户外活动";
            case "轻度污染":
                return "敏感人群减少户外活动";
            case "中度污染":
                return "儿童、老人及呼吸道疾病患者减少户外活动";
            case "重度污染":
                return "所有人群减少户外活动，必要时佩戴口罩";
            case "严重污染":
                return "所有人群停止户外活动，佩戴专业口罩";
            default:
                return "请关注空气质量变化";
        }
    }

    /**
     * 城市天气数据类
     */
    private static class CityWeather {
        private final String condition;
        private final int temperature;
        private final int humidity;
        private final String windSpeed;

        public CityWeather(String condition, int temperature, int humidity, String windSpeed) {
            this.condition = condition;
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
        }

        public String getCondition() { return condition; }
        public int getTemperature() { return temperature; }
        public int getHumidity() { return humidity; }
        public String getWindSpeed() { return windSpeed; }
    }
}



