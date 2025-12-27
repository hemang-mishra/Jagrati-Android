package com.hexagraph.jagrati_android.model

import com.hexagraph.jagrati_android.util.Resource

/** These exceptions need to be handled in view-model, shown via UI and not just thrown.
 * @param genericToast Generic message for the error.
 * @param actualResponse Actual response given by the server.*/

enum class ResponseError(val genericToast: String, var actualResponse: String? = null) {

    DOES_NOT_EXIST(genericToast = "Error! Object not found."),

    BAD_REQUEST(genericToast = "Illegal action."),

    /** Also means the user is not logged in.*/
    AUTH_HEADER_NOT_FOUND(genericToast = "Please login in order to perform this operation."),    // "You need to provide an Authorization header.

    RATE_LIMIT_EXCEEDED(genericToast = "Rate limit exceeded."),

    UNAUTHORISED(genericToast = "You are not authorised to access the requested content."),

    INTERNAL_SERVER_ERROR(genericToast = "Internal server error."),

    BAD_GATEWAY(genericToast = "Error! Bad gateway."),

    REMOTE_PLAYER_ERROR(genericToast = "Error! Could not play the requested listen."),

    SERVICE_UNAVAILABLE(genericToast = "Server outage detected. Please try again later."),

    NETWORK_ERROR(genericToast = "Network issues detected. Make sure device is connected to internet."),

    FILE_NOT_FOUND(genericToast = "Selected file does not exist."),

    UNKNOWN(genericToast = "Some error has occurred.");


    /** Simple function that returns the most suitable message to show the user.*/
    val toast get() =  actualResponse ?: genericToast

    /** Wrap this [ResponseError] in [com.hexagraph.jagrati_android.util.Resource] with an optional parameter to add the actual response.*/
    fun <T> asResource(actualResponse: String? = null): Resource<T> =
        Resource.failure(error = this.apply { this@ResponseError.actualResponse = actualResponse })


}