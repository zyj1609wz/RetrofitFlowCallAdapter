package com.zyj.retrofit.adapter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author : zhaoyanjun
 * @time : 2021/11/10
 * @desc : process
 */

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<Any>.callEnqueueFlow(call: Call<R>, isBody: Boolean) {
    call.enqueue(object : Callback<R> {
        override fun onResponse(call: Call<R>, response: Response<R>) {
            processing(response, isBody)
        }

        override fun onFailure(call: Call<R>, throwable: Throwable) {
            cancel(CancellationException(throwable.localizedMessage, throwable))
        }
    })
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<Any>.callExecuteFlow(call: Call<R>, isBody: Boolean) {
    try {
        processing(call.execute(), isBody)
    } catch (throwable: Throwable) {
        cancel(CancellationException(throwable.localizedMessage, throwable))
    }
}

@ExperimentalCoroutinesApi
internal fun <R> ProducerScope<Any>.processing(response: Response<R>, isBody: Boolean) {
    if (response.isSuccessful) {
        val body = response.body()
        if (body == null || response.code() == 204) {
            cancel(CancellationException("HTTP status code: ${response.code()}"))
        } else {
            var channelResult = if (isBody) trySendBlocking(body) else trySendBlocking(response)
            channelResult
                .onSuccess {
                    close()
                }
                .onClosed { throwable ->
                    cancel(
                        CancellationException(
                            throwable?.localizedMessage,
                            throwable
                        )
                    )
                }
                .onFailure { throwable ->
                    cancel(
                        CancellationException(
                            throwable?.localizedMessage,
                            throwable
                        )
                    )
                }
        }
    } else {
        val msg = response.errorBody()?.string()
        cancel(
            CancellationException(
                if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                } ?: "unknown error"
            )
        )
    }
}