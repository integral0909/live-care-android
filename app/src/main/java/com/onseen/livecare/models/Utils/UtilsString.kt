package com.onseen.livecare.models.Utils

import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class UtilsString {

    enum class ErrorCode(val value: String) {
        USER_LOGIN_INVALIDCREDENTIALS("INVALID_CREDENTIALS_ERROR")
    }

    companion object {
        // TODO: String Parsers
        fun parseString(value: Any?): String {
            var szResult: String = ""

            if(value == null) return szResult
            else if(value.equals("null")) return szResult

            szResult = value.toString()
            return szResult
        }

        fun parseString(value: Any?, defaultValue: String): String {
            var szResult: String = defaultValue

            if(value == null) return szResult
            else if(value.equals("null")) return szResult

            szResult = value.toString()
            return szResult
        }

        fun parseInt(value: Any?, defaultValue: Int?): Int {
            var defValue: Int = defaultValue ?: 0

            if(value == null) return defValue

            if(value is Int) return value

            defValue = value.toString().toInt()
            return defValue
        }

        fun parseDouble(value: Any?, defaultValue: Double?): Double {
            var defValue: Double = defaultValue ?: 0.0

            if(value == null || value.toString().isEmpty() || value.equals(".")) return defValue
            if(value is Double) return value

            defValue = value.toString().toDouble()
            return defValue
        }

        fun parseFloat(value: Any?, defaultValue: Float?): Float {
            var defValue: Float = defaultValue ?: 0f

            if(value == null) return defValue

            if(value is Float) return defValue

            defValue = value.toString().toFloat()
            return defValue
        }

        fun parseBool(value: Any?, defaultValue: Boolean?): Boolean {
            var defValue: Boolean = defaultValue ?: false

            if(value == null) return defValue

            if(value is Boolean) return value

            defValue = value.toString().toBoolean()
            return defValue
        }

        fun padLeadingZerosForTwoDigits(value: Int): String {
            if(value > 9) {
                return String.format("%d", value)
            }

            return String.format("0%d", value)
        }

        fun generateRandomString(length: Int): String {
            val letters: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

            return (0..length-1)
                .map { letters.random() }
                .joinToString("")
        }

        fun stripNonNumbericsFromString(text: String?): String {
            if(text == null) return ""

            return text.toCharArray()
                .filter { it.isLetterOrDigit() }
                .joinToString(separator = "")
        }

        fun beautifyPhoneNumber(phone: String?): String {
            var phoneNumber = stripNonNumbericsFromString(phone);

            val szPattern: String = "(xxx) xxx-xxxx"
            val nMaxLength: Int = szPattern.length;
            var szFormattedNumber: String = ""

            if(phoneNumber.length > 10) {
                phoneNumber = phoneNumber.substring(phoneNumber.length - 10)
            }

            var index: Int = 0
            for(i in 0..phoneNumber.length-1) {
                val r = szPattern.indexOf("x", index)
                if(r <= 0) break

                if(r != index) {
                    szFormattedNumber = szFormattedNumber + szPattern.substring(index, r)
                }
                szFormattedNumber = szFormattedNumber + phoneNumber.subSequence(i, i +1)
                index = r + 1
            }

            if(phoneNumber.length > 0 && (phoneNumber.length < szPattern.length)) {
                // Add extra non-numeric characters at the end
                val r = szPattern.indexOf("x", szFormattedNumber.length)
                if(r > 0) {
                    szFormattedNumber = szFormattedNumber + szPattern.substring(szFormattedNumber.length, r)
                } else {
                    szFormattedNumber = szFormattedNumber + szPattern.subSequence(szFormattedNumber.length,
                        szPattern.length)
                }
            }

            if(szFormattedNumber.length > nMaxLength) {
                szFormattedNumber = szFormattedNumber.substring(0, nMaxLength)
            }

            return szFormattedNumber
        }


        fun getStringForDictionary(data: JSONObject?): String {

            if(data == null) return "{}"

            val contents: ArrayList<String> = ArrayList()
            val keys: Iterator<String> = data.keys()

            while (keys.hasNext()) {
                val cKey: String = keys.next()
                if(data.has(cKey)) {
                    val value: Any? = data.get(cKey)
                    if(value == null) continue
                    val v: String? = getStringForValue(value)
                    contents.add("\"" + cKey + "\": " + v)
                }
            }

            if(contents.size == 0) return "{}"

            var result: String = contents.get(0)
            for(i in 1..contents.size-1) {
                val item: String = contents.get(i)
                result = result + ", " + item
            }

            return "{" + result + "}"
        }

        fun getStringForValue(value: Any?): String? {
            if(value == null) return null
            else if(value is Int) {
                return "" + value
            } else if(value is String) {
                var s: String = value.toString()
                s = s.replace("\n", "\\n")
                return "\"" + s + "\""
            } else if(value is Boolean) {
                if(value.toString().toBoolean() == true) {
                    return "true"
                } else {
                    return "false"
                }
            } else if(value is Float || value is Double) {
                return "" + value
            } else if(value is ArrayList<*>) {
                val arrayString: ArrayList<String?> = ArrayList()
                val arr = value as ArrayList<*>

                for(i in 0..arr.size-1) {
                    val obj: Any = arr.get(i)
                    arrayString.add(getStringForValue(obj))
                }

                if(arrayString.size == 0) return "[]"
                var result = arrayString.get(0)
                for(i in 1..arrayString.size) {
                    val item = arrayString.get(i)
                    result = result + ", " + item
                }
                return '[' + result!! + ']'
            } else if(value is JSONArray) {
                val arrayString: ArrayList<String?> = ArrayList()
                val arr: JSONArray = value
                for(i in 0..arr.length()-1) {
                    val obj: Any = arr.get(i)
                    arrayString.add(getStringForValue(obj))
                }

                if(arrayString.size == 0) return "[]"
                var result = arrayString.get(0)
                for(i in 1..arrayString.size-1) {
                    val item = arrayString.get(i)
                    result = result + ", " + item
                }

                return '[' + result!! + ']'
            } else if(value is JSONObject) {
                val dic = value
                return getStringForDictionary(dic)
            }

            return value.toString()
        }

    }
}