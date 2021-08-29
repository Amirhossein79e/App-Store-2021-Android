package com.amirhosseinemadi.appstore.common

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class Module(private val context: Context) {

    @Provides
    public fun context() : Context
    {
        return context
    }

}