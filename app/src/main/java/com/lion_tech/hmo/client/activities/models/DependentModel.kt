package com.lion_tech.hmo.client.activities.models

data class DependentModel(
    var clientId:Int,
    var dependentId_code:String,
    var name: String,
    var lastName: String,
    var relation: String,
    var otherName: String,
    var clientProfile: String,
    var hospitalId: Int,
    var hospitalName: String,
    var hospitalLocation: String,
    var planName: String,
    var dependentId: Int,
    var dob: String

)