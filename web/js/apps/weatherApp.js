import { weatherService } from '../services/weatherService.js';
import { state } from '../state.js';

export class WeatherApp {
    render(container) {
        this.container = container;
        this.renderView();
    }

    renderView() {
        const w = state.weather;
        this.container.innerHTML = `
            <div class="app-header">
                <span>🌤️ Weather</span>
                <select id="citySelect" style="background: var(--bg-surface); color: var(--text-primary); border: 1px solid var(--border-color); border-radius: 8px; padding: 2px 6px; font-size: 12px;">
                    ${weatherService.popularCities.map((c, i) => `
                        <option value="${i}" ${c.name === w.cityName ? 'selected' : ''}>${c.name}</option>
                    `).join('')}
                </select>
            </div>
            <div class="app-content" style="padding: 16px;">
                <div style="text-align: center; margin-top: 10px; margin-bottom: 24px;">
                    <div style="font-size: 54px;">${weatherService.getWeatherIcon(w.weatherCode)}</div>
                    <div style="font-size: 42px; font-weight: 700; margin-top: 8px;">${Math.round(w.temperature)}°C</div>
                    <div style="font-size: 16px; font-weight: 600; color: var(--text-primary);">${w.cityName}</div>
                    <div style="font-size: 13px; color: var(--text-secondary);">${w.weatherDesc}</div>
                    <div style="font-size: 12px; color: var(--text-secondary); margin-top: 6px;">Humidity: ${w.humidity}% | Wind: ${w.windSpeed} km/h</div>
                </div>

                <h4 style="font-size: 14px; margin-bottom: 10px;">Hourly Forecast</h4>
                <div style="display: flex; gap: 12px; overflow-x: auto; padding-bottom: 12px; margin-bottom: 20px;">
                    ${(w.hourly || []).map(h => `
                        <div style="min-width: 64px; padding: 10px 8px; border-radius: 12px; background: var(--bg-surface); border: 1px solid var(--border-color); text-align: center; font-size: 12px;">
                            <div>${h.time}</div>
                            <div style="font-size: 20px; margin: 4px 0;">${weatherService.getWeatherIcon(h.weatherCode)}</div>
                            <div style="font-weight: 600;">${Math.round(h.temp)}°</div>
                        </div>
                    `).join('')}
                </div>

                <h4 style="font-size: 14px; margin-bottom: 10px;">Daily Forecast</h4>
                <div style="display: flex; flex-direction: column; gap: 8px;">
                    ${(w.daily || []).map(d => `
                        <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 14px; border-radius: 12px; background: var(--bg-surface); border: 1px solid var(--border-color); font-size: 13px;">
                            <span style="width: 50px; font-weight: 600;">${d.day}</span>
                            <span>${weatherService.getWeatherIcon(d.weatherCode)}</span>
                            <span>${Math.round(d.minTemp)}° / ${Math.round(d.maxTemp)}°C</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

        this.container.querySelector('#citySelect').addEventListener('change', (e) => {
            state.refreshWeather(parseInt(e.target.value));
            setTimeout(() => this.renderView(), 400);
        });
    }
}
