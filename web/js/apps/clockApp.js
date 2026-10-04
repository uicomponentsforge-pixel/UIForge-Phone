export class ClockApp {
    constructor() {
        this.stopwatchTime = 0;
        this.stopwatchTimer = null;
        this.timerSeconds = 60;
        this.timerInterval = null;
    }

    render(container) {
        this.container = container;
        container.innerHTML = `
            <div class="app-header">
                <span>⏰ Clock & Timers</span>
            </div>
            <div class="app-content" style="padding: 16px;">
                <div style="display: flex; justify-content: space-around; margin-bottom: 16px; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">
                    <button class="tab-btn active" id="tabWorld" style="background: transparent; border: none; color: var(--accent); font-weight: 600; cursor: pointer;">World Clock</button>
                    <button class="tab-btn" id="tabStopwatch" style="background: transparent; border: none; color: var(--text-secondary); font-weight: 600; cursor: pointer;">Stopwatch</button>
                    <button class="tab-btn" id="tabTimer" style="background: transparent; border: none; color: var(--text-secondary); font-weight: 600; cursor: pointer;">Timer</button>
                </div>

                <div id="clockTabContent"></div>
            </div>
        `;

        this.renderWorldClock();

        container.querySelector('#tabWorld').addEventListener('click', (e) => {
            this.setActiveTab(e.target);
            this.renderWorldClock();
        });

        container.querySelector('#tabStopwatch').addEventListener('click', (e) => {
            this.setActiveTab(e.target);
            this.renderStopwatch();
        });

        container.querySelector('#tabTimer').addEventListener('click', (e) => {
            this.setActiveTab(e.target);
            this.renderTimer();
        });
    }

    setActiveTab(activeBtn) {
        this.container.querySelectorAll('.tab-btn').forEach(b => {
            b.style.color = 'var(--text-secondary)';
        });
        activeBtn.style.color = 'var(--accent)';
    }

    renderWorldClock() {
        const content = this.container.querySelector('#clockTabContent');
        const cities = [
            { name: "San Francisco", tz: "America/Los_Angeles" },
            { name: "New York", tz: "America/New_York" },
            { name: "London", tz: "Europe/London" },
            { name: "Tokyo", tz: "Asia/Tokyo" }
        ];

        const updateTimes = () => {
            if (!content) return;
            content.innerHTML = cities.map(c => {
                const now = new Date();
                const timeStr = now.toLocaleTimeString('en-US', { timeZone: c.tz, hour: '2-digit', minute: '2-digit', second: '2-digit' });
                return `
                    <div style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px; margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-weight: 600;">${c.name}</span>
                        <span style="font-size: 18px; font-weight: 700; color: var(--accent);">${timeStr}</span>
                    </div>
                `;
            }).join('');
        };
        updateTimes();
    }

    renderStopwatch() {
        const content = this.container.querySelector('#clockTabContent');
        content.innerHTML = `
            <div style="text-align: center; margin-top: 30px;">
                <div id="swDisplay" style="font-size: 48px; font-weight: 700; margin-bottom: 24px;">0.0s</div>
                <div style="display: flex; justify-content: center; gap: 16px;">
                    <button id="swStartBtn" class="btn-primary">Start</button>
                    <button id="swResetBtn" class="btn-secondary">Reset</button>
                </div>
            </div>
        `;

        const display = content.querySelector('#swDisplay');
        const startBtn = content.querySelector('#swStartBtn');
        const resetBtn = content.querySelector('#swResetBtn');

        startBtn.addEventListener('click', () => {
            if (this.stopwatchTimer) {
                clearInterval(this.stopwatchTimer);
                this.stopwatchTimer = null;
                startBtn.textContent = 'Start';
            } else {
                startBtn.textContent = 'Pause';
                const startTime = Date.now() - this.stopwatchTime;
                this.stopwatchTimer = setInterval(() => {
                    this.stopwatchTime = Date.now() - startTime;
                    display.textContent = (this.stopwatchTime / 1000).toFixed(1) + 's';
                }, 100);
            }
        });

        resetBtn.addEventListener('click', () => {
            if (this.stopwatchTimer) {
                clearInterval(this.stopwatchTimer);
                this.stopwatchTimer = null;
            }
            this.stopwatchTime = 0;
            display.textContent = '0.0s';
            startBtn.textContent = 'Start';
        });
    }

    renderTimer() {
        const content = this.container.querySelector('#clockTabContent');
        content.innerHTML = `
            <div style="text-align: center; margin-top: 30px;">
                <div id="tmDisplay" style="font-size: 48px; font-weight: 700; margin-bottom: 24px;">${this.timerSeconds}s</div>
                <div style="display: flex; justify-content: center; gap: 16px;">
                    <button id="tmStartBtn" class="btn-primary">Start</button>
                    <button id="tmSetBtn" class="btn-secondary">Set 60s</button>
                </div>
            </div>
        `;

        const display = content.querySelector('#tmDisplay');
        const startBtn = content.querySelector('#tmStartBtn');

        startBtn.addEventListener('click', () => {
            if (this.timerInterval) {
                clearInterval(this.timerInterval);
                this.timerInterval = null;
                startBtn.textContent = 'Start';
            } else {
                startBtn.textContent = 'Pause';
                this.timerInterval = setInterval(() => {
                    if (this.timerSeconds > 0) {
                        this.timerSeconds--;
                        display.textContent = `${this.timerSeconds}s`;
                    } else {
                        clearInterval(this.timerInterval);
                        this.timerInterval = null;
                        alert("Timer Complete!");
                        startBtn.textContent = 'Start';
                    }
                }, 1000);
            }
        });
    }
}
