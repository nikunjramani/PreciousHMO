package com.lion_tech.hmo.hospital.hospital_dashboard.models

data class EnrolleeModel(
    var enrolleeId:Int,
    var enrolleeName:String,
    var lastName:String,
    var otherName:String,
    var isCapitation:Boolean,
    var capitationId:String,
    var createdAt:String,
    var capitationType:String,
    var relation:String,
    var dependentClientId:Int
)