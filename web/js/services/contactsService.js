import { storage } from '../storage/storage.js';

const INITIAL_CONTACTS = [
    { id: "1", name: "Alice Smith", phone: "+1 (555) 019-2831", initials: "AS" },
    { id: "2", name: "Bob Jones", phone: "+1 (555) 014-9920", initials: "BJ" },
    { id: "3", name: "Charlie Brown", phone: "+1 (555) 018-3344", initials: "CB" },
    { id: "4", name: "Diana Prince", phone: "+1 (555) 012-7788", initials: "DP" },
    { id: "5", name: "Ethan Hunt", phone: "+1 (555) 016-5511", initials: "EH" }
];

export class ContactsService {
    constructor() {
        this.contacts = storage.getItem('uiforge_contacts', INITIAL_CONTACTS);
    }

    getContacts() {
        return [...this.contacts];
    }

    addContact(name, phone) {
        const initials = name.trim().split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || "C";
        const newContact = {
            id: Date.now().toString(),
            name,
            phone,
            initials
        };
        this.contacts.unshift(newContact);
        this.save();
        return newContact;
    }

    deleteContact(id) {
        this.contacts = this.contacts.filter(c => c.id !== id);
        this.save();
    }

    save() {
        storage.setItem('uiforge_contacts', this.contacts);
    }
}

export const contactsService = new ContactsService();
