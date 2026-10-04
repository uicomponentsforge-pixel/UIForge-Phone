import { storage } from '../storage/storage.js';

const INITIAL_THREADS = [
    {
        id: "1",
        contactName: "Alice Smith",
        phone: "+1 (555) 019-2831",
        messages: [
            { text: "Hey! Are we meeting today?", sender: "them", time: "10:30 AM" },
            { text: "Yes, 2 PM works great for me!", sender: "me", time: "10:32 AM" }
        ]
    },
    {
        id: "2",
        contactName: "Bob Jones",
        phone: "+1 (555) 014-9920",
        messages: [
            { text: "Sent you the UIForge updates.", sender: "them", time: "Yesterday" }
        ]
    }
];

export class MessagesApp {
    constructor() {
        this.threads = storage.getItem('uiforge_messages', INITIAL_THREADS);
        this.activeThreadId = null;
    }

    render(container) {
        this.container = container;
        this.renderView();
    }

    renderView() {
        if (this.activeThreadId) {
            this.renderChatThread();
        } else {
            this.renderThreadList();
        }
    }

    renderThreadList() {
        this.container.innerHTML = `
            <div class="app-header">
                <span>💬 Messages</span>
                <span style="font-size: 11px; opacity: 0.7;">Web Local Messaging</span>
            </div>
            <div class="app-content" style="padding: 12px;">
                <div style="font-size: 11px; color: var(--text-secondary); background: rgba(59, 130, 246, 0.1); padding: 8px 12px; border-radius: 8px; margin-bottom: 12px;">
                    ℹ️ Browsers cannot send SMS directly. Messages are saved in local browser storage.
                </div>
                <div id="threadList" style="display: flex; flex-direction: column; gap: 8px;">
                    ${this.threads.map(t => {
                        const lastMsg = t.messages[t.messages.length - 1] || { text: 'No messages', time: '' };
                        return `
                            <div class="thread-item" data-id="${t.id}" style="background: var(--bg-surface); padding: 12px; border-radius: 12px; border: 1px solid var(--border-color); cursor: pointer;">
                                <div style="display: flex; justify-content: space-between; font-weight: 600; font-size: 14px;">
                                    <span>${t.contactName}</span>
                                    <span style="font-size: 11px; color: var(--text-secondary);">${lastMsg.time}</span>
                                </div>
                                <div style="font-size: 13px; color: var(--text-secondary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-top: 4px;">
                                    ${lastMsg.text}
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;

        this.container.querySelectorAll('.thread-item').forEach(el => {
            el.addEventListener('click', () => {
                this.activeThreadId = el.getAttribute('data-id');
                this.renderView();
            });
        });
    }

    renderChatThread() {
        const thread = this.threads.find(t => t.id === this.activeThreadId);
        if (!thread) {
            this.activeThreadId = null;
            this.renderView();
            return;
        }

        this.container.innerHTML = `
            <div class="app-header">
                <div style="display: flex; align-items: center; gap: 8px;">
                    <button id="backToThreadsBtn" style="background: transparent; border: none; color: var(--accent); font-size: 18px; cursor: pointer;">◀</button>
                    <span>${thread.contactName}</span>
                </div>
                <a href="sms:${thread.phone}" style="color: var(--accent); font-size: 12px; text-decoration: none;">SMS Link</a>
            </div>
            <div class="app-content" style="padding: 12px; display: flex; flex-direction: column; justify-content: space-between;">
                <div id="messagesList" style="flex: 1; overflow-y: auto; display: flex; flex-direction: column; gap: 8px; padding-bottom: 12px;">
                    ${thread.messages.map(m => `
                        <div style="max-width: 75%; padding: 10px 14px; border-radius: 16px; font-size: 13px; ${m.sender === 'me' ? 'align-self: flex-end; background: var(--accent); color: white;' : 'align-self: flex-start; background: var(--bg-surface); color: var(--text-primary); border: 1px solid var(--border-color);'}">
                            <div>${m.text}</div>
                            <div style="font-size: 10px; opacity: 0.7; text-align: right; margin-top: 2px;">${m.time}</div>
                        </div>
                    `).join('')}
                </div>
                <div style="display: flex; gap: 8px;">
                    <input type="text" id="msgInput" class="input-field" placeholder="Type a message..." style="flex: 1;">
                    <button id="sendMsgBtn" class="btn-primary">Send</button>
                </div>
            </div>
        `;

        this.container.querySelector('#backToThreadsBtn').addEventListener('click', () => {
            this.activeThreadId = null;
            this.renderView();
        });

        const sendMsg = () => {
            const input = this.container.querySelector('#msgInput');
            const text = input.value.trim();
            if (!text) return;

            const now = new Date();
            const timeStr = `${now.getHours() % 12 || 12}:${now.getMinutes().toString().padStart(2, '0')} ${now.getHours() >= 12 ? 'PM' : 'AM'}`;

            thread.messages.push({
                text,
                sender: 'me',
                time: timeStr
            });

            storage.setItem('uiforge_messages', this.threads);
            this.renderView();
        };

        this.container.querySelector('#sendMsgBtn').addEventListener('click', sendMsg);
        this.container.querySelector('#msgInput').addEventListener('keydown', (e) => {
            if (e.key === 'Enter') sendMsg();
        });
    }
}
