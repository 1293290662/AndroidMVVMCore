# AndroidMVVMCore

A modern MVVM framework for Android development with BaseActivity, BaseFragment, BaseViewModel, BaseRepository and Network components.

## Features

- **BaseActivity**: Base class for activities with ViewModel support
- **BaseFragment**: Base class for fragments with ViewModel support
- **BaseViewModel**: Base ViewModel class with common utilities
- **BaseRepository**: Base repository class for data operations
- **Network Components**: Retrofit, OkHttp configuration with interceptors
- **Result Handling**: Generic Result and NetworkResponse wrappers

## Installation

### Step 1: Add JitPack repository

Add it in your root build.gradle at the end of repositories:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

```gradle
dependencies {
    implementation 'com.github.spadger:AndroidMVVMCore:1.0.0'
}
```

## Usage

### BaseActivity

```kotlin
class MainActivity : BaseActivity<MainViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun createViewModel(): MainViewModel {
        return getViewModel()
    }

    override fun observeViewModel() {
        viewModel.data.observe(this) { data ->
            // Handle data
        }
    }
}
```

### BaseViewModel

```kotlin
class MainViewModel : BaseViewModel() {
    val data = MutableLiveData<String>()
    
    fun fetchData() {
        launch {
            val result = repository.getData()
            result.onSuccess { data.value = it }
            result.onFailure { showError(it.message) }
        }
    }
}
```

### BaseRepository

```kotlin
class MainRepository : BaseRepository() {
    suspend fun getData(): Result<String> {
        return safeApiCall { apiService.getData() }
    }
}
```

### Network Configuration

```kotlin
// Create Retrofit service
val apiService = RetrofitProvider.create<ApiService>()

// Custom OkHttp client
val client = OkHttpProvider.create {
    addInterceptor(HeaderInterceptor())
}
```

## License

MIT License

## Contributing

Feel free to submit issues and enhancement requests.

## Credits

Developed by Spadger Developer
