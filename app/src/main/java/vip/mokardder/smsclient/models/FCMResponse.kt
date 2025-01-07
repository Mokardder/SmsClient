package vip.mokardder.smsclient.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


data class FCMResponse(
    @SerializedName("smsData") val smsData: SmsData,
    @SerializedName("action") val action: String
)

data class SmsData(
    @SerializedName("smsMessage") val smsMessage: String,
    @SerializedName("number") val number: String
)
