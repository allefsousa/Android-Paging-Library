package com.developer.allef.boilerplateapp.util

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import java.util.concurrent.Executor

/**
 * @author allef.santos on 12/05/20
 */
class MainThreadExecutor : Executor {
    private val mHandler = Handler(Looper.getMainLooper())
    override fun execute(@NonNull command: Runnable?) {
        mHandler.post(command)
    }
}