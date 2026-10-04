// Weather Service matching WeatherService.kt (Open-Meteo API & popular cities)

export class WeatherService {
    constructor() {
        this.popularCities = [
            { name: "San Francisco", lat: 37.7749, lon: -122.4194 },
            { name: "New York", lat: 40.7128, lon: -74.0060 },
            { name: "London", lat: 51.5074, lon: -0.1278 },
            { name: "Tokyo", lat: 35.6762, lon: 139.6503 },
            { name: "Sydney", lat: -33.8688, lon: 151.2093 }
        ];
    }

    getWeatherDesc(code) {
        switch (code) {
            case 0: return "Clear Sky";
            case 1: return "Mainly Clear";
            case 2: return "Partly Cloudy";
            case 3: return "Overcast";
            case 45: case 48: return "Foggy";
            case 51: case 53: case 55: return "Drizzle";
            case 61: case 63: case 65: return "Rainy";
            case 71: case 73: case 75: return "Snowy";
            case 80: case 81: case 82: return "Rain Showers";
            case 95: case 96: case 99: return "Thunderstorm";
            default: return "Partly Cloudy";
        }
    }

    getWeatherIcon(code) {
        switch (code) {
            case 0: return "☀️";
            case 1: case 2: return "⛅";
            case 3: return "☁️";
            case 45: case 48: return "🌫️";
            case 51: case 53: case 55: case 61: case 63: case 65: case 80: case 81: case 82: return "🌧️";
            case 71: case 73: case 75: return "❄️";
            case 95: case 96: case 99: return "⛈️";
            default: return "⛅";
        }
    }

    async fetchWeather(city = this.popularCities[0]) {
        const url = `https://api.open-meteo.com/v1/forecast?latitude=${city.lat}&longitude=${city.lon}&current_weather=true&hourly=temperature_2m,weathercode&daily=weathercode,temperature_2m_max,temperature_2m_min&timezone=auto`;
        try {
            const res = await fetch(url);
            if (!res.ok) throw new Error("Network error");
            const data = await res.json();
            const current = data.current_weather || {};
            const hourly = [];
            if (data.hourly && data.hourly.time) {
                for (let i = 0; i < Math.min(12, data.hourly.time.length); i++) {
                    const timeStr = data.hourly.time[i];
                    const hour = new Date(timeStr).getHours();
                    const ampm = hour >= 12 ? 'PM' : 'AM';
                    const h12 = hour % 12 || 12;
                    hourly.push({
                        time: `${h12} ${ampm}`,
                        temp: data.hourly.temperature_2m[i] || 20,
                        weatherCode: data.hourly.weathercode[i] || 0
                    });
                }
            }

            const daily = [];
            if (data.daily && data.daily.time) {
                const days = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
                for (let i = 0; i < Math.min(7, data.daily.time.length); i++) {
                    const dateObj = new Date(data.daily.time[i]);
                    daily.push({
                        day: days[dateObj.getUTCDay()],
                        minTemp: data.daily.temperature_2m_min[i] || 15,
                        maxTemp: data.daily.temperature_2m_max[i] || 22,
                        weatherCode: data.daily.weathercode[i] || 0
                    });
                }
            }

            return {
                cityName: city.name,
                temperature: current.temperature || 21.0,
                apparentTemp: (current.temperature || 21.0) - 0.5,
                humidity: 62,
                windSpeed: current.windspeed || 14.5,
                weatherCode: current.weathercode || 0,
                weatherDesc: this.getWeatherDesc(current.weathercode || 0),
                isDay: current.is_day !== undefined ? current.is_day === 1 : true,
                hourly,
                daily
            };
        } catch (e) {
            console.warn("Weather API fetch failed, returning fallback mock data:", e);
            return {
                cityName: city.name,
                temperature: 21.0,
                apparentTemp: 20.5,
                humidity: 62,
                windSpeed: 14.5,
                weatherCode: 0,
                weatherDesc: "Clear Sky",
                isDay: true,
                hourly: [
                    { time: "Now", temp: 21.0, weatherCode: 0 },
                    { time: "1 PM", temp: 22.0, weatherCode: 0 },
                    { time: "2 PM", temp: 23.0, weatherCode: 1 },
                    { time: "3 PM", temp: 22.5, weatherCode: 2 },
                    { time: "4 PM", temp: 21.0, weatherCode: 0 },
                    { time: "5 PM", temp: 19.5, weatherCode: 0 }
                ],
                daily: [
                    { day: "Today", minTemp: 14, maxTemp: 23, weatherCode: 0 },
                    { day: "Mon", minTemp: 15, maxTemp: 24, weatherCode: 1 },
                    { day: "Tue", minTemp: 13, maxTemp: 21, weatherCode: 3 },
                    { day: "Wed", minTemp: 14, maxTemp: 22, weatherCode: 61 },
                    { day: "Thu", minTemp: 16, maxTemp: 25, weatherCode: 0 }
                ]
            };
        }
    }
}

export const weatherService = new WeatherService();
