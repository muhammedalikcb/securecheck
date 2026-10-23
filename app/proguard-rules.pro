# Library entry points that are loaded reflectively.
-keep class com.google.android.play.core.integrity.** { *; }

# Report models are serialized with kotlinx.serialization.
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class com.muhammedalikocabey.securecheck.**$$serializer { *; }
-keepclassmembers class com.muhammedalikocabey.securecheck.** {
    *** Companion;
}
-keepclasseswithmembers class com.muhammedalikocabey.securecheck.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ObfuscationProbe is deliberately not kept: the code-shrinking check reads its renamed class name at runtime.
