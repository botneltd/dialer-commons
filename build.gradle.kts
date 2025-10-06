plugins {
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.parcelize).apply(false)
    alias(libs.plugins.library).apply(false)
    alias(libs.plugins.kotlinSerialization).apply(false)
    alias(libs.plugins.detekt).apply(false)
}

subprojects {
    plugins.withId("io.gitlab.arturbosch.detekt") {
        configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            buildUponDefaultConfig = true
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            baseline = file("$rootDir/config/detekt/detekt-baseline.xml")
            ignoreFailures = true

            reports {
                html.required.set(true)
                html.outputLocation.set(file("$buildDir/reports/detekt.html"))
                xml.required.set(false)
                txt.required.set(false)
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete {
        layout.buildDirectory.asFile
    }
}
