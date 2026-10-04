import { storage } from '../storage/storage.js';

export class CalendarApp {
    constructor() {
        this.events = storage.getItem('uiforge_calendar_events', [
            { date: new Date().toISOString().slice(0, 10), title: "UIForge Web Release" }
        ]);
        this.selectedDate = new Date();
    }

    render(container) {
        this.container = container;
        this.renderCalendar();
    }

    renderCalendar() {
        const year = this.selectedDate.getFullYear();
        const month = this.selectedDate.getMonth();
        const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        this.container.innerHTML = `
            <div class="app-header">
                <span>📅 Calendar</span>
                <button id="addEventBtn" class="btn-primary" style="padding: 4px 10px; font-size: 12px;">+ Event</button>
            </div>
            <div class="app-content" style="padding: 12px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                    <button id="prevMonthBtn" class="btn-secondary" style="padding: 4px 10px;">◀</button>
                    <span style="font-weight: 700; font-size: 16px;">${monthNames[month]} ${year}</span>
                    <button id="nextMonthBtn" class="btn-secondary" style="padding: 4px 10px;">▶</button>
                </div>

                <div style="display: grid; grid-template-columns: repeat(7, 1fr); gap: 4px; text-align: center; font-weight: 600; font-size: 12px; margin-bottom: 8px;">
                    <span>Su</span><span>Mo</span><span>Tu</span><span>We</span><span>Th</span><span>Fr</span><span>Sa</span>
                </div>

                <div style="display: grid; grid-template-columns: repeat(7, 1fr); gap: 4px;" id="calendarDaysGrid">
                    ${Array(firstDay).fill('').map(() => `<div></div>`).join('')}
                    ${Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => {
                        const dateStr = `${year}-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
                        const hasEvent = this.events.some(e => e.date === dateStr);
                        const isToday = day === new Date().getDate() && month === new Date().getMonth() && year === new Date().getFullYear();
                        return `
                            <div class="cal-day" data-date="${dateStr}" style="padding: 10px 4px; text-align: center; border-radius: 8px; border: 1px solid var(--border-color); background: ${isToday ? 'var(--accent)' : 'var(--bg-surface)'}; color: ${isToday ? 'white' : 'var(--text-primary)'}; position: relative; cursor: pointer; font-size: 13px;">
                                ${day}
                                ${hasEvent ? `<div style="width: 4px; height: 4px; background: #EF4444; border-radius: 50%; position: absolute; bottom: 3px; left: 50%; transform: translateX(-50%);"></div>` : ''}
                            </div>
                        `;
                    }).join('')}
                </div>

                <h4 style="margin-top: 16px; margin-bottom: 8px; font-size: 14px;">Events</h4>
                <div id="eventsList" style="display: flex; flex-direction: column; gap: 6px;">
                    ${this.events.map(e => `
                        <div style="padding: 10px; border-radius: 8px; background: var(--bg-surface); border: 1px solid var(--border-color); font-size: 13px; display: flex; justify-content: space-between;">
                            <span>${e.title}</span>
                            <span style="color: var(--text-secondary);">${e.date}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

        this.container.querySelector('#prevMonthBtn').addEventListener('click', () => {
            this.selectedDate.setMonth(this.selectedDate.getMonth() - 1);
            this.renderCalendar();
        });

        this.container.querySelector('#nextMonthBtn').addEventListener('click', () => {
            this.selectedDate.setMonth(this.selectedDate.getMonth() + 1);
            this.renderCalendar();
        });

        this.container.querySelector('#addEventBtn').addEventListener('click', () => {
            const title = prompt("Enter Event Title:");
            if (title) {
                const dateStr = prompt("Enter Date (YYYY-MM-DD):", new Date().toISOString().slice(0, 10));
                if (dateStr) {
                    this.events.push({ title, date: dateStr });
                    storage.setItem('uiforge_calendar_events', this.events);
                    this.renderCalendar();
                }
            }
        });
    }
}
