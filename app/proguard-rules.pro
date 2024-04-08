# Keep all interfaces
-keep interface com.yourlibrarypackage.** { *; }

# Keep all data classes
-keep class com.yourlibrarypackage.** {
    public protected private *;
}