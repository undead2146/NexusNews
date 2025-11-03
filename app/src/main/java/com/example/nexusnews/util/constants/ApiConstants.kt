package com.example.nexusnews.util.constants

/**
 * API-related constants for endpoints, parameters, and response codes.
 */
object ApiConstants {
    const val API_KEY_HEADER = "X-Api-Key"
    const val AUTHORIZATION_HEADER = "Authorization"
    const val CONTENT_TYPE_HEADER = "Content-Type"
    const val USER_AGENT_HEADER = "User-Agent"
    const val ACCEPT_HEADER = "Accept"
    const val APPLICATION_JSON = "application/json"

    // HTTP Status Codes
    const val HTTP_OK = 200
    const val HTTP_CREATED = 201
    const val HTTP_BAD_REQUEST = 400
    const val HTTP_UNAUTHORIZED = 401
    const val HTTP_FORBIDDEN = 403
    const val HTTP_NOT_FOUND = 404
    const val HTTP_TOO_MANY_REQUESTS = 429
    const val HTTP_INTERNAL_SERVER_ERROR = 500

    // API Response Status
    const val STATUS_OK = "ok"
    const val STATUS_ERROR = "error"
}