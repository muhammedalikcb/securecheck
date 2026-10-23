package com.muhammedalikocabey.securecheck.data.system

import android.os.Debug
import javax.inject.Inject
import javax.inject.Singleton

fun interface ClassLookup {
    fun exists(className: String): Boolean
}

fun interface DebuggerProbe {
    fun isAttached(): Boolean
}

@Singleton
class RuntimeClassLookup @Inject constructor() : ClassLookup {
    override fun exists(className: String): Boolean = try {
        Class.forName(className, false, javaClass.classLoader)
        true
    } catch (_: ClassNotFoundException) {
        false
    } catch (_: LinkageError) {
        false
    }
}

@Singleton
class AndroidDebuggerProbe @Inject constructor() : DebuggerProbe {
    override fun isAttached(): Boolean = Debug.isDebuggerConnected()
}
