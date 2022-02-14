package com.lion_tech.hmo.hospital.hospital_dashboard.models

data class HospitalRequestModel(
    var requestId:Int,
    var clientId:Int,
    var requestCode:String,
    var requestStatus:String,
    var requestBillStatus:String,
    var enrolleeName:String,
    var lastName:String,
    var otherName:String,
    var rejectReason:String,
    var totalBill:Float,
    var date:String,
    var diagnose:String,
    var procedure:String,
    var medical:String,
    var investigation:String,
    var codeRejectReason:String,
    var billRejectReason:String,
    var capitationStatus:String,
    var userType:String,
    var dob:String
)