package com.zyj.retrofit.adapter

import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author : zhaoyanjun
 * @time : 2021/11/10
 * @desc : FlowCallAdapterFactory
 */
class FlowCallAdapterFactory private constructor(private var isAsync: Boolean) :
    CallAdapter.Factory() {

    companion object {
        /**
         * 同步
         */
        fun create(): FlowCallAdapterFactory =
            FlowCallAdapterFactory(false)

        /**
         * 异步
         */
        fun createAsync(): FlowCallAdapterFactory =
            FlowCallAdapterFactory(true)
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }
        var observableType = getParameterUpperBound(0, returnType as ParameterizedType)

        val rawObservableType = getRawType(observableType)
        var isBody = true

        if (rawObservableType == Response::class.java) {
            observableType = getParameterUpperBound(0, observableType as ParameterizedType)
            isBody = false
        }
        return FlowCallAdapter<Any>(observableType, isAsync, isBody)
    }

}