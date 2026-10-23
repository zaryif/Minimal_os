package com.zaryif.minimalos.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.zaryif.minimalos.domain.model.Contact

class ContactRepository(private val context: Context) {

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun searchContacts(query: String = ""): List<Contact> {
        if (!hasPermission()) return emptyList()

        val cleanQuery = query.trim()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        val (selection, selectionArgs) = if (cleanQuery.isNotEmpty()) {
            val sel = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} LIKE ?"
            val args = arrayOf("%$cleanQuery%", "%$cleanQuery%")
            sel to args
        } else {
            null to null
        }
        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        data class ScoredCandidate(
            val contact: Contact,
            val score: Int
        )

        val scoredResults = mutableListOf<ScoredCandidate>()

        try {
            val cursor = context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val primaryIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY)
                val typeIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)

                val q = cleanQuery.lowercase()
                val qDigits = cleanQuery.filter { c -> c.isDigit() }

                while (it.moveToNext() && scoredResults.size < 60) {
                    val id = if (idIdx >= 0) it.getString(idIdx) ?: "" else ""
                    val name = if (nameIdx >= 0) it.getString(nameIdx) ?: "" else ""
                    val number = if (numberIdx >= 0) it.getString(numberIdx) ?: "" else ""
                    val isPrimary = if (primaryIdx >= 0) it.getInt(primaryIdx) == 1 else false
                    val type = if (typeIdx >= 0) it.getInt(typeIdx) else 0

                    if (name.isNotBlank() && number.isNotBlank()) {
                        val trimmedName = name.trim()
                        val trimmedNumber = number.trim()
                        val nameLower = trimmedName.lowercase()
                        val numberDigits = trimmedNumber.filter { c -> c.isDigit() }

                        var score = 0
                        if (q.isNotEmpty()) {
                            // 1. Exact Name match (case-insensitive) -> Highest Priority
                            if (nameLower == q) {
                                score += 1000
                            } else if (nameLower.startsWith(q)) {
                                // 2. Prefix match (e.g. "John" for "John Doe")
                                score += 600
                            } else {
                                // 3. Word-level exact or prefix match (e.g. "Doe" in "John Doe")
                                val words = nameLower.split(Regex("\\s+"))
                                if (words.any { w -> w == q }) {
                                    score += 400
                                } else if (words.any { w -> w.startsWith(q) }) {
                                    score += 250
                                } else if (nameLower.contains(q)) {
                                    score += 100
                                }
                            }

                            // 4. Phone number exact or suffix match
                            if (qDigits.isNotEmpty()) {
                                if (numberDigits == qDigits) {
                                    score += 800
                                } else if (numberDigits.endsWith(qDigits)) {
                                    score += 450
                                }
                            }

                            // 5. Prefer primary/default and mobile numbers
                            if (isPrimary) score += 60
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) score += 30
                        }

                        scoredResults.add(
                            ScoredCandidate(
                                contact = Contact(id = id, name = trimmedName, phoneNumber = trimmedNumber),
                                score = score
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {}

        if (cleanQuery.isEmpty()) {
            return scoredResults.map { it.contact }.distinctBy { it.phoneNumber }.take(25)
        }

        // Sort by relevance score descending, then shorter names closer to query, then alphabetical
        return scoredResults
            .sortedWith(
                compareByDescending<ScoredCandidate> { it.score }
                    .thenBy { it.contact.name.length }
                    .thenBy { it.contact.name }
            )
            .map { it.contact }
            .distinctBy { it.phoneNumber }
            .take(25)
    }
}
