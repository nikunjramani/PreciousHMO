package com.lion_tech.hmo.client.activities.models

data class ComplaintModel(
    var complaintId:Int,
    var complaintSubject:String,
    var complaintDescription:String,
    var complaintCode:String,
    var ticketDate:String,
    var ticketStatus:String
)