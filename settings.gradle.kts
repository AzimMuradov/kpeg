rootProject.name = "kpeg"


// Library

include("lib")


// Examples

include(
    "examples:simple-calc",
    "examples:json",
)


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")