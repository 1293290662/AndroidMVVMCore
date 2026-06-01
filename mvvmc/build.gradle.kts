plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.github.spadger.mvvmc"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.viewmodel.ktx)
    implementation(libs.androidx.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.gitee.zhangyuanyangW"
                artifactId = "android-mvvmcore"
                version = "1.0.0"

                pom {
                    name.set("MVVMCore")
                    description.set("A modern MVVM framework for Android development with BaseActivity, BaseFragment, BaseViewModel, BaseRepository and Network components")
                    url.set("https://gitee.com/zhangyuanyangW/android-mvvmcore")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("zhangyuanyangW")
                            name.set("Zhang Yuanyang")
                            email.set("zhangyuanyangW@gitee.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://gitee.com/zhangyuanyangW/android-mvvmcore.git")
                        developerConnection.set("scm:git:https://gitee.com/zhangyuanyangW/android-mvvmcore.git")
                        url.set("https://gitee.com/zhangyuanyangW/android-mvvmcore")
                    }
                }
            }
        }
    }
}