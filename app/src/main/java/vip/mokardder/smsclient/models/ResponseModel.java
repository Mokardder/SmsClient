package vip.mokardder.smsclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {

    private int status;
    private List<userData> userData;
    public dataReg dataReg;



    public String timeStamp;


    public DataModified dataModified;

    public int getStatus() {
        return status;
    }


    public List<ResponseModel.userData> getUserData() {
        return userData;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


    public static class userData {
        @SerializedName("appendedRowId")
        private String appendedRowId;
        @SerializedName("email")
        private String email;
        @SerializedName("userid")
        private String userid;

        public String getAppendedRowId() {
            return appendedRowId;
        }

        public String getEmail() {
            return email;
        }

        public String getUserid() {
            return userid;
        }
    }

    public static class dataReg {
        private String rowId;
        private String mobileName;
        private String deviceInfo;
        private String userID;
        private String email;

        public String getRowId() {
            return rowId;
        }

        public String getMobileName() {
            return mobileName;
        }

        public String getDeviceInfo() {
            return deviceInfo;
        }

        public String getUserID() {
            return userID;
        }

        public String getEmail() {
            return email;
        }
    }
    public static class DataModified {
        private String fcmToken;
        private String lastActive;
        private String mobileName;
        private String email;
        private String deviceInfo;
        private String batteryInfo;
        private String isScreenOn;
        private String version;
        private String userID;

        public String getFcmToken() {
            return fcmToken;
        }

        public String getLastActive() {
            return lastActive;
        }

        public String getMobileName() {
            return mobileName;
        }

        public String getEmail() {
            return email;
        }

        public String getDeviceInfo() {
            return deviceInfo;
        }

        public String getBatteryInfo() {
            return batteryInfo;
        }

        public String getIsScreenOn() {
            return isScreenOn;
        }

        public String getVersion() {
            return version;
        }

        public String getUserID() {
            return userID;
        }
        // Constructors, getters, and setters
    }
    public static class Registered {

        private String appendedRowId;
        private String email;
        private String userid;

        public String getAppendedRowId() {
            return appendedRowId;
        }

        public String getEmail() {
            return email;
        }

        public String getUserid() {
            return userid;
        }
    }
}
