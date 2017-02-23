package net.pside.androidthings.example

import android.app.Application

import net.pside.androidthings.example.util.ExtDebugTree

import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(ExtDebugTree())
    }
}
