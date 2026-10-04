export class CalculatorApp {
    render(container) {
        container.innerHTML = `
            <div class="app-header">
                <span>🧮 Calculator</span>
            </div>
            <div class="app-content" style="padding: 16px; display: flex; flex-direction: column; justify-content: space-between;">
                <div style="text-align: right; margin-top: 20px; padding: 16px; background: var(--bg-surface); border-radius: 16px; border: 1px solid var(--border-color);">
                    <div id="calcExpr" style="font-size: 14px; color: var(--text-secondary); min-height: 20px;"></div>
                    <div id="calcDisplay" style="font-size: 36px; font-weight: 700; color: var(--text-primary); min-height: 44px; word-break: break-all;">0</div>
                </div>

                <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-top: 20px;">
                    ${['C', '(', ')', '/', '7', '8', '9', '*', '4', '5', '6', '-', '1', '2', '3', '+', '0', '.', '⌫', '='].map(btn => `
                        <button class="calc-btn" data-val="${btn}" style="padding: 16px; font-size: 20px; font-weight: 600; border-radius: 14px; border: 1px solid var(--border-color); background: ${['/', '*', '-', '+', '='].includes(btn) ? 'var(--accent)' : 'var(--bg-surface)'}; color: ${['/', '*', '-', '+', '='].includes(btn) ? 'white' : 'var(--text-primary)'}; cursor: pointer;">
                            ${btn}
                        </button>
                    `).join('')}
                </div>
            </div>
        `;

        let expression = "";
        const display = container.querySelector('#calcDisplay');
        const exprEl = container.querySelector('#calcExpr');

        container.querySelectorAll('.calc-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const val = btn.getAttribute('data-val');
                if (val === 'C') {
                    expression = "";
                    display.textContent = "0";
                    exprEl.textContent = "";
                } else if (val === '⌫') {
                    expression = expression.slice(0, -1);
                    display.textContent = expression || "0";
                } else if (val === '=') {
                    try {
                        const safeExpr = expression.replace(/[^0-9+\-*/().]/g, '');
                        const res = Function(`"use strict"; return (${safeExpr})`)();
                        exprEl.textContent = expression + " =";
                        display.textContent = res;
                        expression = res.toString();
                    } catch (e) {
                        display.textContent = "Error";
                        expression = "";
                    }
                } else {
                    expression += val;
                    display.textContent = expression;
                }
            });
        });
    }
}
