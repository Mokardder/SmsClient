package vip.mokardder.smsclient.models


data class SendDataPayload(
    val searchValue: String,
    val fcmToken: String,
    val mobileName: String,
    val type: String,
)

data class SendQoutaUpdate(
    val searchValue: String,
    val qouta: String,
    val type: String,
)

data class dacPayload(
    val dac: String = "",        // Default value for dac
    val cashmemo: String = "",   // Default value for cashmemo
    val timeStamp: String = ""    // Default value for timeStamp
)