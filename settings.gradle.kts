rootProject.name = "kpeg"


// Library

include("lib")

// Examples

include(
    "examples:simple-calc",
    "examples:json",
)

// Docs

include("docs")


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")