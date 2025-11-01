package com.banew.cw2025_client.data;

import androidx.lifecycle.MutableLiveData;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    public void resolveData(MutableLiveData<T> success, MutableLiveData<Exception> failure) {
        if (isSuccess()) {
            success.postValue(asSuccess().getData());
        }
        else {
            failure.postValue(asError().getError());
        }
    }

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isError() {
        return this instanceof Error;
    }

    public Success<T> asSuccess() {
        return (Success<T>) this;
    }

    public Error<T> asError() {
        return (Error<T>) this;
    }

    // Success sub-class
    public final static class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error<T> extends Result<T> {
        private final Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}