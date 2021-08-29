package com.amirhosseinemadi.appstore.common

import android.content.Context
import dagger.Component

@Component(modules = [Module::class])
interface Component {

    fun context() : Context

}