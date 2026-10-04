import { contactsService } from '../services/contactsService.js';

export class ContactsApp {
    render(container) {
        this.container = container;
        this.renderList();
    }

    renderList() {
        const contacts = contactsService.getContacts();
        this.container.innerHTML = `
            <div class="app-header">
                <span>👤 Contacts</span>
                <button id="addContactBtn" class="btn-primary" style="padding: 4px 10px; font-size: 12px;">+ Add</button>
            </div>
            <div class="app-content" style="padding: 12px;">
                <input type="text" id="contactSearch" class="input-field" placeholder="Search contacts..." style="width: 100%; margin-bottom: 12px;">
                <div id="contactsList" style="display: flex; flex-direction: column; gap: 8px;">
                    ${contacts.map(c => `
                        <div class="contact-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px; display: flex; align-items: center; justify-content: space-between;">
                            <div style="display: flex; align-items: center; gap: 12px;">
                                <div style="width: 40px; height: 40px; border-radius: 50%; background: var(--accent); color: white; display: flex; justify-content: center; align-items: center; font-weight: 700;">
                                    ${c.initials}
                                </div>
                                <div>
                                    <div style="font-weight: 600; font-size: 14px;">${c.name}</div>
                                    <div style="font-size: 12px; color: var(--text-secondary);">${c.phone}</div>
                                </div>
                            </div>
                            <div style="display: flex; gap: 8px;">
                                <a href="tel:${c.phone}" style="text-decoration: none;"><button class="btn-secondary" style="padding: 6px 10px; font-size: 12px;">📞</button></a>
                                <button class="btn-secondary delete-contact-btn" data-id="${c.id}" style="padding: 6px 10px; font-size: 12px; color: #EF4444;">🗑️</button>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

        this.container.querySelector('#addContactBtn').addEventListener('click', () => this.renderAddForm());

        this.container.querySelectorAll('.delete-contact-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.getAttribute('data-id');
                contactsService.deleteContact(id);
                this.renderList();
            });
        });

        const searchInput = this.container.querySelector('#contactSearch');
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.toLowerCase();
            const filtered = contactsService.getContacts().filter(c => c.name.toLowerCase().includes(query) || c.phone.includes(query));
            const listEl = this.container.querySelector('#contactsList');
            listEl.innerHTML = filtered.map(c => `
                <div class="contact-card" style="background: var(--bg-surface); border: 1px solid var(--border-color); border-radius: 12px; padding: 12px; display: flex; align-items: center; justify-content: space-between;">
                    <div style="display: flex; align-items: center; gap: 12px;">
                        <div style="width: 40px; height: 40px; border-radius: 50%; background: var(--accent); color: white; display: flex; justify-content: center; align-items: center; font-weight: 700;">
                            ${c.initials}
                        </div>
                        <div>
                            <div style="font-weight: 600; font-size: 14px;">${c.name}</div>
                            <div style="font-size: 12px; color: var(--text-secondary);">${c.phone}</div>
                        </div>
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <a href="tel:${c.phone}" style="text-decoration: none;"><button class="btn-secondary" style="padding: 6px 10px; font-size: 12px;">📞</button></a>
                        <button class="btn-secondary delete-contact-btn" data-id="${c.id}" style="padding: 6px 10px; font-size: 12px; color: #EF4444;">🗑️</button>
                    </div>
                </div>
            `).join('');
        });
    }

    renderAddForm() {
        this.container.innerHTML = `
            <div class="app-header">
                <span>Add Contact</span>
                <button id="cancelAddBtn" class="btn-secondary" style="padding: 4px 10px; font-size: 12px;">Cancel</button>
            </div>
            <div class="app-content" style="padding: 16px; display: flex; flex-direction: column; gap: 12px;">
                <div>
                    <label style="font-size: 12px; color: var(--text-secondary);">Full Name</label>
                    <input type="text" id="newContactName" class="input-field" placeholder="e.g. John Doe" style="width: 100%; margin-top: 4px;">
                </div>
                <div>
                    <label style="font-size: 12px; color: var(--text-secondary);">Phone Number</label>
                    <input type="text" id="newContactPhone" class="input-field" placeholder="e.g. +1 (555) 012-3456" style="width: 100%; margin-top: 4px;">
                </div>
                <button id="saveContactBtn" class="btn-primary" style="margin-top: 12px;">Save Contact</button>
            </div>
        `;

        this.container.querySelector('#cancelAddBtn').addEventListener('click', () => this.renderList());
        this.container.querySelector('#saveContactBtn').addEventListener('click', () => {
            const name = this.container.querySelector('#newContactName').value.trim();
            const phone = this.container.querySelector('#newContactPhone').value.trim();
            if (name && phone) {
                contactsService.addContact(name, phone);
                this.renderList();
            }
        });
    }
}
