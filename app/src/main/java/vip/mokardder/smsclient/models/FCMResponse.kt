package vip.mokardder.smsclient.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


data class FCMResponse(
    val action: String,
    @SerializedName("smsData") private val smsDataString: String
) {
    val smsData: List<SmsData>
        get() = Gson().fromJson(smsDataString, object : TypeToken<List<SmsData>>() {}.type)
}

data class SmsData(
    val number: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("smsMessage") val smsMessage: String
)