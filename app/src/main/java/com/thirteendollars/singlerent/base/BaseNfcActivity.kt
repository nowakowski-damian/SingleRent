package com.thirteendollars.singlerent.base

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.databinding.ViewDataBinding
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.PersistableBundle
import com.thirteendollars.singlerent.bus.EventBus
import com.thirteendollars.singlerent.bus.Events
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Damian Nowakowski on 24/04/2018.
 * mail: thirteendollars.com@gmail.com
 */
 abstract class
BaseNfcActivity<BINDING: ViewDataBinding>: BaseActivity<BINDING>() {

    @Inject
    protected lateinit var bus: EventBus

    private var nfcAdapter: NfcAdapter? = null
    private var nfcAvailable = false

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initializeNfc()
        handleIntent(intent)
    }

    private fun initializeNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if( nfcAdapter==null || nfcAdapter?.isEnabled==false ) {
            nfcAvailable = false
            onNfcUnavailable()
        }
        else {
            nfcAvailable = true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if ( !nfcAvailable ) {
            initializeNfc()
        }
        if(nfcAvailable) {
            setupForegroundDispatch()
        }
    }

    override fun onPause() {
        nfcAdapter?.disableForegroundDispatch(this)
        super.onPause()
    }

    private fun setupForegroundDispatch() {
        val intent = Intent(applicationContext, this@BaseNfcActivity.javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val intentFilter = IntentFilter().apply {
            addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            addCategory(Intent.CATEGORY_DEFAULT)
            addDataType("text/plain")
        }
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, arrayOf(intentFilter), arrayOf())
    }


    private fun handleIntent(intent: Intent?) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {
            if (intent.type=="text/plain") {
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                val ndef = Ndef.get(tag).cachedNdefMessage.records.first().payload
                val result = String(ndef,Charsets.US_ASCII)
                this@BaseNfcActivity.bus.post(Events.NfcCollected(result))
            } else {
                Timber.e("Wrong mime type: " + intent.type!!)
            }
        }
    }

    abstract fun onNfcUnavailable()

}