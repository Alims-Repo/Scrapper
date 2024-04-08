# Keep all interfaces
-keep interface com.nelu.scrapper.data.repo.base.** { *; }

# Keep all data classes
-keep class com.nelu.scrapper.data.model.** {
    public protected private *;
}