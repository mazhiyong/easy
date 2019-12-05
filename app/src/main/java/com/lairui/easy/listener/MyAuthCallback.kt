package com.lairui.easy.listener

import android.annotation.TargetApi
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Handler

import com.lairui.easy.basic.MbsConstans

/**
 *
 */
@TargetApi(Build.VERSION_CODES.M)
class MyAuthCallback(handler: Handler) : FingerprintManager.AuthenticationCallback() {

    private var handler: Handler? = null

    init {

        this.handler = handler
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        super.onAuthenticationError(errMsgId, errString)

        handler?.obtainMessage(MbsConstans.FingerRecognize.MSG_AUTH_ERROR, errMsgId, 0)?.sendToTarget()
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        super.onAuthenticationHelp(helpMsgId, helpString)

        handler?.obtainMessage(MbsConstans.FingerRecognize.MSG_AUTH_HELP, helpMsgId, 0)?.sendToTarget()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)

        handler?.obtainMessage(MbsConstans.FingerRecognize.MSG_AUTH_SUCCESS)?.sendToTarget()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()

        handler?.obtainMessage(MbsConstans.FingerRecognize.MSG_AUTH_FAILED)?.sendToTarget()
    }
}
