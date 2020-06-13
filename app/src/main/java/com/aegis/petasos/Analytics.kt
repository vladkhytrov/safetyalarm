package com.aegis.petasos

class Analytics {

    class Event {

        companion object {
            const val SMS_CREATED = "sms_created"
            const val SMS_CANCELED = "sms_canceled"
            const val SMS_CHANGED = "sms_changed"
            const val SMS_SENT = "sms_sent"
        }

    }

    class Param {

        companion object {
            const val SENDING_TIME = "sending_time"
            const val SENT_AT = "sent_at"
        }

    }

}