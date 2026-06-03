plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.github.spadger.mvvmc"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // 公共 API 依赖 - 使用 api 暴露给使用者
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.datastore.preferences)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.navigation.ui.ktx)
    api(libs.kotlinx.coroutines.android)
    api(libs.gson)

    // 内部实现依赖 - 使用 implementation 隐藏
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp)
    api(libs.okhttp.logging.interceptor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.ZhangYuanYang-spadger"
                    artifactId = "AndroidMVVMCore"
                    version = "1.0.2"

                    pom {
                        name.set("MVVMCore")
                        description.set("A modern MVVM framework for Android development with BaseActivity, BaseFragment, BaseViewModel, BaseRepository and Network components")
                        url.set("https://github.com/ZhangYuanYang-spadger/AndroidMVVMCore")

                        licenses {
                            license {
                                name.set("MIT License")
                                url.set("https://opensource.org/licenses/MIT")
                            }
                        }

                        developers {
                            developer {
                                id.set("ZhangYuanYang-spadger")
                                name.set("ZhangYuanYang")
                                email.set("1293290662@qq.com")
                            }
                        }

                        scm {
                            connection.set("scm:git:https://github.com/ZhangYuanYang-spadger/AndroidMVVMCore.git")
                            developerConnection.set("scm:git:https://github.com/ZhangYuanYang-spadger/AndroidMVVMCore.git")
                            url.set("https://github.com/ZhangYuanYang-spadger/AndroidMVVMCore")
                        }
                }
            }
        }
    }
}