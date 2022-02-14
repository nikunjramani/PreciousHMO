package com.lion_tech.hmo.server_urls

class ServerUrls {
    companion object {
        private const val baseUrl = "https://liontechhmo.liontech.com.ng/app/index.php/api"
        const val loginUr = "$baseUrl/auth/login/client"
            const val resetPasswordUrl = "$baseUrl/client/resetpassword"

            const val HOSPITAL_LOGIN = "$baseUrl/auth/login/hospital"
        const val GET_ENROLLEE_DATA = "$baseUrl/client/detail"
//        const val GET_ALL_PROVIDERS = "$baseUrl/client/provider"
        const val GET_ALL_PROVIDERS = "$baseUrl/client/list_hospital"
        const val GET_CLIENT_PROVIDERS = "$baseUrl/client/getClientHospitalList"
        const val GET_SUBSCRIPTOIN = "$baseUrl/client/subscription/detail"
//        const val ADD_NEW_DEPENDANT = "$baseUrl/client/new/dependent"
        const val ADD_NEW_DEPENDANT = "$baseUrl/client/new_dependant"
//        const val GET_CLIENT_DEPENDANT = "$baseUrl/client/dependent"
        const val GET_CLIENT_DEPENDANT = "$baseUrl/client/client_dependant"
        const val CHANGE_CLIENT_PROVIDER = "$baseUrl/client/request/hospital"
        const val ADD_CLIENT_COMPLAINT = "$baseUrl/client/create_new_tickets"
        const val ADD_HOSPITAL_COMPLAINT = "$baseUrl/hospital/create_new_tickets"
        const val GET_CLIENT_COMPLAINT = "$baseUrl/client/tickets"
        const val GET_HOSPITAL_COMPLAINT = "$baseUrl/hospital/get_tickets"
        const val LOG_OUT = "$baseUrl/auth/logout"
        const val GET_STATE_LIST = "$baseUrl/client/location"
        const val GET_CLIENT_SUBSCRIPTION = "$baseUrl/client/get_xin_subscription_detail"
        const val GET_CLIENT_HOSPITAL_DATA = "$baseUrl/client/getClientHospitalDetail"
        const val GET_CLIENT_SUBSCRIPTION_LIST = "$baseUrl/client/getAllSubscription"
        const val GET_CLIENT_DASHBOARD_COUNT = "$baseUrl/client/getNoForClientDashboard"
        const val GET_HOSPITAL_DASHBOARD_COUNT = "$baseUrl/hospital/getHospitalDashboardData"
        const val UPDATE_CLIENT_DATA = "$baseUrl/client/updateClientData"

        const val CLIENT_PASSWORD = "$baseUrl/client/update_password"
        const val HOSPITAL_PASSWORD = "$baseUrl/hospital/updateHospitalPassword"
        const val HOSPITAL_DETAIL = "$baseUrl/hospital/detail"
        const val GET_HOSPITAL_REQUEST_DATA = "$baseUrl/hospital/getDataForRequestPage"
        const val SEND_REQUEST_FOR_BILL = "$baseUrl/hospital/updateBillStatus"
        const val SEND_REQUEST_FOR_CODE = "$baseUrl/hospital/updateRequestStatus"
        const val ADD_HOSPITAL_REQUEST_DATA = "$baseUrl/hospital/addHospitalRequest"
        const val UPDATE_HOSPITAL_REQUEST_DATA = "$baseUrl/hospital/updateHospitalRequest"
        const val GET_CLIENT_DIAGNOSE_DRUGS_SERVICES = "$baseUrl/hospital/getClientDiagnoseServiceAndDrugs"
        const val GET_HOSPITAL_TOTAL_REQUEST = "$baseUrl/hospital/getHospitalTotalRequest"
        const val GET_HOSPITAL_APPROVED_BILLS = "$baseUrl/hospital/getHospitalApprovedBills"
        const val GET_HOSPITAL_AUTH_BILLS = "$baseUrl/hospital/getHospitalAuthBills"
        const val GET_ELECTRONICS_DATA = "$baseUrl/client/getElectronicId"

    }

}