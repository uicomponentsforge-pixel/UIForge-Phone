import { state } from '../state.js';
import { contactsService } from '../services/contactsService.js';

export class PhoneApp {
    render(container) {
        container.innerHTML = `
            <div class="app-header">
                <span>📞 Phone</span>
                <span style="font-size: 12px; opacity: 0.7;">web tel: link ready</span>
            </div>
            <div class="app-content" style="padding: 16px; display: flex; flex-direction: column; justify-content: space-between;">
                <div style="text-align: center; margin-top: 20px;">
                    <input type="text" id="phoneDisplay" style="font-size: 32px; font-weight: 700; text-align: center; border: none; background: transparent; color: var(--text-primary); outline: none; width: 100%;" readonly value="" placeholder="Enter Number">
                </div>

                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; max-width: 280px; margin: 0 auto; width: 100%;">
                    ${['1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '0', '#'].map(key => `
                        <button class="keypad-btn" data-key="${key}" style="width: 64px; height: 64px; border-radius: 50%; border: 1px solid var(--border-color); background: var(--bg-surface); color: var(--text-primary); font-size: 24px; font-weight: 600; cursor: pointer; display: flex; justify-content: center; align-items: center; margin: 0 auto;">
                            ${key}
                        </button>
                    `).join('')}
                </div>

                <div style="display: flex; justify-content: center; gap: 20px; align-items: center; margin-bottom: 20px;">
                    <button id="clearPhoneBtn" class="btn-secondary" style="border-radius: 50%; width: 56px; height: 56px; padding: 0;">⌫</button>
                    <a id="callTelLink" href="tel:" style="text-decoration: none;">
                        <button id="callPhoneBtn" style="background: #10B981; color: white; border: none; border-radius: 50%; width: 64px; height: 64px; font-size: 24px; cursor: pointer; display: flex; justify-content: center; align-items: center;">📞</button>
                    </a>
                </div>
            </div>
        `;

        let currentNum = "";
        const display = container.querySelector('#phoneDisplay');
        const telLink = container.querySelector('#callTelLink');

        container.querySelectorAll('.keypad-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                currentNum += btn.getAttribute('data-key');
                display.value = currentNum;
                telLink.href = `tel:${currentNum}`;
            });
        });

        container.querySelector('#clearPhoneBtn').addEventListener('click', () => {
            currentNum = currentNum.slice(0, -1);
            display.value = currentNum;
            telLink.href = currentNum ? `tel:${currentNum}` : 'tel:';
        });
    }
}
