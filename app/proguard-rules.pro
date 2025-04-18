###############################################
# ROOM DATABASE
###############################################
# Keep Room Database classes
-keep class androidx.room.** { *; }
-keepattributes *Annotation*

# Keep Room Entities
-keep class com.novandiramadhan.petster.data.local.room.entity.** { *; }

# Keep the database and DAO classes
-keep class com.novandiramadhan.petster.data.local.room.PetsterDatabase { *; }
-keep class com.novandiramadhan.petster.data.local.room.dao.** { *; }

# Keep entity field names (used by Room)
-keepclassmembers class com.novandiramadhan.petster.data.local.room.entity.** {
    <fields>;
}

# Keep constructors that Room uses for entity instantiation
-keepclassmembers class com.novandiramadhan.petster.data.local.room.entity.** {
    <init>(...);
}

# Keep getter and setter methods for Room entities
-keepclassmembers class com.novandiramadhan.petster.data.local.room.entity.** {
    public void set*(***);
    public *** get*();
    public boolean is*();
}

# Preserve Room's generated code
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.* class *

###############################################
# RETROFIT & NETWORKING
###############################################
# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

# Keep API models
-keep class com.novandiramadhan.petster.data.remote.response.** { *; }
-keep class com.novandiramadhan.petster.data.remote.request.** { *; }
-keep class com.novandiramadhan.petster.data.remote.api.** { *; }

###############################################
# MATERIAL ICONS
###############################################
# Keep Material Design components
-keep class androidx.compose.material.icons.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep vector drawables and resources used by Material Icons
-keep class androidx.compose.ui.graphics.vector.** { *; }
-keepclassmembers class androidx.compose.material.icons.** {
    public static ** get*();
}

# Keep icon factory methods
-keepclassmembers class * extends androidx.compose.ui.graphics.vector.ImageVector {
    public static ** get*();
}

###############################################
# HILT DEPENDENCY INJECTION
###############################################
-keep class javax.inject.** { *; }
-keep class dagger.hilt.** { *; }
-keep class androidx.hilt.** { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

###############################################
# FIREBASE
###############################################
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-keepattributes Signature
-keepattributes *Annotation*

###############################################
# DATASTORE
###############################################
-keep class androidx.datastore.** { *; }
-keepclassmembers class * implements androidx.datastore.preferences.core.Preferences$Key { *; }

###############################################
# PAGING
###############################################
-keep class androidx.paging.** { *; }

###############################################
# COIL IMAGE LOADING
###############################################
-keep class io.coil.** { *; }
-keep class coil3.** { *; }
-dontwarn io.coil.**

###############################################
# EASYCROP
###############################################
-keep class io.github.mr0xf00.easycrop.** { *; }

###############################################
# GOOGLE GENERATIVE AI
###############################################
-keep class com.google.ai.client.generativeai.** { *; }

###############################################
# NAVIGATION COMPOSE
###############################################
-keep class androidx.navigation.** { *; }

###############################################
# KOTLINX SERIALIZATION
###############################################
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep class kotlinx.serialization.json.** { *; }

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

###############################################
# COMPOSE MARKDOWN
###############################################
-keep class com.github.jeziellago.compose.markdown.** { *; }
-keepclassmembers class com.github.jeziellago.compose.markdown.** { *; }

###############################################
# GENERAL ANDROID & KOTLIN
###############################################
# Keep BuildConfig
-keep class com.novandiramadhan.petster.BuildConfig { *; }

# Keep kotlin metadata
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepattributes AnnotationDefault

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}