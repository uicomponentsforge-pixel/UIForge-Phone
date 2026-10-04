package com.example.data

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.example.model.ContactItem

class RealContactsManager(private val context: Context) {

    fun fetchContacts(): List<ContactItem> {
        val contactsList = mutableListOf<ContactItem>()
        val resolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        var cursor: Cursor? = null
        try {
            cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.let {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val seenNumbers = mutableSetOf<String>()
                while (it.moveToNext()) {
                    val id = if (idIndex != -1) it.getString(idIndex) else ""
                    val name = if (nameIndex != -1) it.getString(nameIndex) ?: "Unknown" else "Unknown"
                    val number = if (numberIndex != -1) it.getString(numberIndex) ?: "" else ""

                    val cleanNum = number.replace("[^0-9+]".toRegex(), "")
                    if (cleanNum.isNotEmpty() && !seenNumbers.contains(cleanNum)) {
                        seenNumbers.add(cleanNum)
                        contactsList.add(ContactItem(id = id, name = name, phone = number))
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.w("RealContactsManager", "Permission not granted: ${e.message}")
        } catch (e: Exception) {
            Log.e("RealContactsManager", "Error querying contacts: ${e.message}")
        } finally {
            cursor?.close()
        }

        return contactsList
    }

    fun createAddContactIntent(name: String = "", phone: String = ""): Intent {
        return Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
            if (name.isNotEmpty()) {
                putExtra(ContactsContract.Intents.Insert.NAME, name)
            }
            if (phone.isNotEmpty()) {
                putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            }
        }
    }
}
