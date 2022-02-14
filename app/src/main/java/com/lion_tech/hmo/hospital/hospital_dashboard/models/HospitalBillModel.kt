package com.lion_tech.hmo.hospital.hospital_dashboard.models

data class HospitalBillModel(
    var requestId:Int,
    var clientId:Int,
    var requestCode:String,
    var requestStatus:Int,
    var enrolleeName:String,
    var lastName:String,
    var otherName:String,
    var rejectReason:String,
    var totalBill:Float,
    var date:String,
    var diagnose:String,
    var procedure:String,
    var medical:String,
    var investigation:String
)