package com.example.fastscroll.custom_view.coroutines

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

object Android : AbstractCoroutineContextElement(ContinuationInterceptor) {
}

class ContinuationInterceptor1 : ContinuationInterceptor {
    override val key: CoroutineContext.Key<*>
        get() = TODO("Not yet implemented")

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return AndroidContinuation(continuation)
    }

}

private class AndroidContinuation<T>(val cont: Continuation<T>) : Continuation<T> {
    override val context: CoroutineContext
        get() = TODO("Not yet implemented")

    override fun resumeWith(result: Result<T>) {
        TODO("Not yet implemented")
    }

}