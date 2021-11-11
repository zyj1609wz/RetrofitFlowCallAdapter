package com.zyj.retrofit.adapter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : zhaoyanjun
 * @time : 2021/11/10
 * @desc :FlowCallAdapter
 */
internal class FlowCallAdapter<R>(
    private val responseType: Type,
    private val isAsync: Boolean,
    private val isBody: Boolean,
) : CallAdapter<R, Flow<Any?>> {

    override fun responseType() = responseType

    @ExperimentalCoroutinesApi
    override fun adapt(call: Call<R>): Flow<Any?> {
        return callFlow(call, isAsync)
    }

    @ExperimentalCoroutinesApi
    private fun callFlow(call: Call<R>, isAsync: Boolean): Flow<Any> {
        val started = AtomicBoolean(false)
        return callbackFlow {
            if (started.compareAndSet(false, true)) {
                if (isAsync) callEnqueueFlow(call, isBody) else callExecuteFlow(call, isBody)
                awaitClose { call.cancel() }
            }
        }
    }
}