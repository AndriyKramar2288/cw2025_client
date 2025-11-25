package com.banew.cw2025_client.data

import com.banew.cw2025_client.ui.main.MainPageModel

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
open class Result<T>  // hide the private constructor to limit subclass types (Success, Error)
private constructor() {
    val isSuccess: Boolean
        get() = this is Success<*>

    val isError: Boolean
        get() = this is Error<*>

    fun asSuccess(callback: (res: Success<T>) -> Unit = {}): Result<T> {
        if (this is Success<*>) callback(this as Success<T>)
        return this
    }

    fun asSuccess(): Success<T> {
        return this as Success<T>
    }

    fun asError(): Error<T> {
        return this as Error<T>
    }

    fun asError(callback: (res: Error<T>) -> Unit = {}): Result<T> {
        if (this is Error<*>) callback(this as Error<T>)
        return this
    }

    fun asErrorNetEx(booleanFieldSetter: (Boolean) -> Unit): Result<T> {
        if (this is Error<*>)
            booleanFieldSetter(error.message.equals("Network error"))
        return this
    }

    fun asErrorNetEx(contextModel: MainPageModel): Result<T> {
        return asErrorNetEx {
            contextModel.updateConnectionError(contextModel.isConnectionError || it)
        }
    }

    // Success sub-class
    class Success<T>(val data: T) : Result<T>()

    // Error sub-class
    class Error<T>(val error: Exception) : Result<T>()
}