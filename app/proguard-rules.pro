# Gerekli tutulan SDK sınıfları
-keep class androidx.security.crypto.** { *; }
-keep class com.google.android.gms.safetynet.** { *; }
-keep class com.google.android.play.** { *; }
-keep class com.muhammedalikocabey.securecheck.domain.model.** { *; }
-keep class java.security.cert.** { *; }
-keep class javax.net.ssl.** { *; }
-keep class com.muhammedalikocabey.securecheck.core.network.PinningTrustManager { *; }

# WebView JS interface güvenliği için
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# SAFETYNET API KEY'i korumak için
-keepclassmembers class **.BuildConfig {
    public static final *** SAFETYNET_API_KEY;
}

# intentionally obfuscate edilen sınıf → *tutulmasın*
# Bu sınıf silinirse obfuscation yapılmış sayacağız
# Bu yüzden *kasti olarak* -keep verilmez!
# -dontwarn verilebilir.
-dontwarn com.muhammedalikocabey.securecheck.domain.fake.IntentionallyMissingClass
