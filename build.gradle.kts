import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependencymanagement)
}


group = "sve2"

setJavaVersion()
setReleaseVersion()
configureRepositories()
configureSpringDependencyManagement()
configureJavaTest()

fun setJavaVersion() = configure(allprojects) {
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

fun setReleaseVersion() {
    val releaseVersion = System.getenv("RELEASE_VERSION") ?: return
    configure(allprojects) {
        version = releaseVersion
    }
}

fun configureRepositories() = configure(allprojects) {
    repositories {
        mavenCentral()
    }
}

fun configureSpringDependencyManagement() {
    allprojects {
        apply(plugin = "io.spring.dependency-management")

        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
        }
    }
}

fun configureJavaTest() {
    subprojects.forEach { project ->
        project.afterEvaluate {
            if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
                return@afterEvaluate
            }
            project.tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}


