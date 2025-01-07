package vip.mokardder.smsclient.models

data class sms_list_payload(
    val sent_to: String,
    val sms_content: String,
    val time: String,
)