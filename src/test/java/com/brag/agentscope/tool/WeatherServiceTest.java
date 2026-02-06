package com.brag.agentscope.tool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * WeatherService工具的单元测试
 */
public class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService();
    }

    @Test
    void testGetCurrentWeather() {
        // 测试支持的城市
        String beijingWeather = weatherService.getCurrentWeather("北京");
        assertTrue(beijingWeather.contains("北京"));
        assertTrue(beijingWeather.contains("°C"));
        assertTrue(beijingWeather.contains("湿度"));
        assertTrue(beijingWeather.contains("风力"));

        String shanghaiWeather = weatherService.getCurrentWeather("上海");
        assertTrue(shanghaiWeather.contains("上海"));

        // 测试不支持的城市
        String unknownCityWeather = weatherService.getCurrentWeather("火星");
        assertTrue(unknownCityWeather.contains("暂不支持"));
        assertTrue(unknownCityWeather.contains("火星"));
    }

    @Test
    void testGetWeatherForecast() {
        // 测试有效的预报天数
        String forecast = weatherService.getWeatherForecast("北京", 3);
        assertTrue(forecast.contains("北京"));
        assertTrue(forecast.contains("天气预报"));
        assertTrue(forecast.contains("°C-"));
        assertTrue(forecast.lines().count() > 3); // 应该有多行

        // 测试边界值
        String oneDayForecast = weatherService.getWeatherForecast("上海", 1);
        assertTrue(oneDayForecast.contains("1"));

        String sevenDayForecast = weatherService.getWeatherForecast("广州", 7);
        assertTrue(sevenDayForecast.contains("7"));

        // 测试无效的天数
        String invalidDays = weatherService.getWeatherForecast("北京", 0);
        assertTrue(invalidDays.contains("预报天数必须在1-7天之间"));

        String tooManyDays = weatherService.getWeatherForecast("北京", 8);
        assertTrue(tooManyDays.contains("预报天数必须在1-7天之间"));

        // 测试不支持的城市
        String unknownCityForecast = weatherService.getWeatherForecast("未知城市", 3);
        assertTrue(unknownCityForecast.contains("暂不支持"));
    }

    @Test
    void testGetAirQuality() {
        // 测试支持的城市
        String aqi = weatherService.getAirQuality("北京");
        assertTrue(aqi.contains("北京"));
        assertTrue(aqi.contains("AQI"));
        assertTrue(aqi.contains("等级"));
        assertTrue(aqi.contains("建议"));

        // 测试AQI范围和等级
        // AQI应该在合理范围内（30-150）
        String aqiContent = aqi.substring(aqi.indexOf("AQI") + 4);
        int aqiValue = Integer.parseInt(aqiContent.substring(0, aqiContent.indexOf("，")));
        assertTrue(aqiValue >= 30 && aqiValue <= 150);

        // 测试不支持的城市
        String unknownCityAqi = weatherService.getAirQuality("未知城市");
        assertTrue(unknownCityAqi.contains("暂不支持"));
    }

    @Test
    void testCompareWeather() {
        // 测试两个支持的城市
        String comparison = weatherService.compareWeather("北京", "上海");
        assertTrue(comparison.contains("北京"));
        assertTrue(comparison.contains("上海"));
        assertTrue(comparison.contains("°C"));
        assertTrue(comparison.contains("湿度"));

        // 测试其中一个城市不支持
        String partialUnknown = weatherService.compareWeather("北京", "未知城市");
        assertTrue(partialUnknown.contains("暂不支持"));
        assertTrue(partialUnknown.contains("未知城市"));

        // 测试两个城市都不支持
        String bothUnknown = weatherService.compareWeather("火星", "月球");
        assertTrue(bothUnknown.contains("暂不支持"));
    }

    @Test
    void testWeatherDataConsistency() {
        // 测试多次调用同一个城市的结果应该相似
        String weather1 = weatherService.getCurrentWeather("北京");
        String weather2 = weatherService.getCurrentWeather("北京");

        // 应该都包含基本信息
        assertTrue(weather1.contains("北京"));
        assertTrue(weather2.contains("北京"));
        assertTrue(weather1.contains("°C"));
        assertTrue(weather2.contains("°C"));

        // 但温度可能有小幅变化（模拟实时性）
        // 这里我们不做严格的相等性检查，因为温度有随机变化
    }

    @Test
    void testSupportedCities() {
        String[] supportedCities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "武汉", "成都", "重庆", "西安"};

        for (String city : supportedCities) {
            String weather = weatherService.getCurrentWeather(city);
            assertFalse(weather.contains("暂不支持"), "城市 " + city + " 应该被支持");
            assertTrue(weather.contains(city), "天气信息应该包含城市名称 " + city);
        }
    }
}


