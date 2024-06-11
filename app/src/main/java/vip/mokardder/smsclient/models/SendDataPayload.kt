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