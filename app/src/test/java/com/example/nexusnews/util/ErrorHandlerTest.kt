package com.example.nexusnews.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorHandlerTest {
    @Test
    fun `getErrorMessage returns correct message for UnknownHostException`() {
        val exception = UnknownHostException()
        val message = ErrorHandler.getErrorMessage(exception)
        assertEquals("No internet connection. Please check your network.", message)
    }

    @Test
    fun `getErrorMessage returns correct message for SocketTimeoutException`() {
        val exception = SocketTimeoutException()
        val message = ErrorHandler.getErrorMessage(exception)
        assertEquals("Connection timeout. Please try again.", message)
    }

    @Test
    fun `getErrorMessage returns correct message for 404 HttpException`() {
        val exception = HttpException(Response.error<Any>(404, okhttp3.ResponseBody.create(null, "")))
        val message = ErrorHandler.getErrorMessage(exception)
        assertTrue(message.contains("not found"))
    }

    @Test
    fun `isRecoverable returns true for network errors`() {
        assertTrue(ErrorHandler.isRecoverable(UnknownHostException()))
        assertTrue(ErrorHandler.isRecoverable(SocketTimeoutException()))
        assertTrue(ErrorHandler.isRecoverable(IOException()))
    }

    @Test
    fun `isRecoverable returns false for non-recoverable errors`() {
        assertFalse(ErrorHandler.isRecoverable(IllegalArgumentException()))
        assertFalse(ErrorHandler.isRecoverable(NullPointerException()))
    }
}
