package com.y2thez.samples.kotlin2

import Example1.*
import Example2.*
import android.app.Activity
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class MainActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
    }

    fun testLambda1Tap(v: View) {
        testLambda1()
    }

    fun testLambda2Tap(v: View) {
        testLambda2()
    }

    fun testCoroutine1Tap(v: View) {
        testCoroutine1()
    }

    fun testCoroutine2Tap(v: View) {
        testCoroutine2()
    }

    fun testCoroutine3Tap(v: View) {
        launch(CommonPool) {testCoroutine3() }
    }

    fun testCoroutine4Tap(v: View) {
        launch(CommonPool) {testCoroutine4() }
    }

    fun testCoroutine5Tap(v: View) {
        launch(CommonPool) {testCoroutine5() }
    }

    fun testCoroutine6Tap(v: View) {
        launch(CommonPool) {testCoroutine6() }
    }

    fun testCoroutine7Tap(v: View) {
        launch(CommonPool) {testCoroutine7() }
    }

    fun testCoroutine8Tap(v: View) {
        launch(CommonPool) {testCoroutine8() }
    }

    fun testCoroutine9Tap(v: View) {
        launch(CommonPool) {testCoroutine9() }
    }

    fun testCoroutine10Tap(v: View) {
        launch(CommonPool) {testCoroutine10() }
    }

    fun testCoroutineContextTap(v: View) {
        testContextDispatcher()
    }

    fun testCoroutineJumpTap(v: View) {
        jumpBetweenContexts()
    }

    fun testCoroutine11Tap(v: View) {
        launch(CommonPool) {testCoroutine11() }
    }

    fun testCoroutine12Tap(v: View) {
        launch(CommonPool) {testCoroutine12() }
    }

    fun testCoroutine13Tap(v: View) {
        launch(CommonPool) {testCoroutine13() }
    }

    fun testCoroutine14Tap(v: View) {
        launch(CommonPool) {testCoroutine14() }
    }
}
