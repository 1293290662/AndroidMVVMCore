# AndroidMVVMCore

A modern MVVM framework for Android development with BaseActivity, BaseFragment, BaseViewModel, BaseRepository and Network components.

## Author

**ZhangYuanYang**  
Email: 1293290662@qq.com

## Features

- **BaseActivity**: Base class for activities with ViewModel support
- **BaseFragment**: Base class for fragments with ViewModel support and lazy loading
- **BaseViewModel**: Base ViewModel class with coroutine utilities and error handling
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
    implementation 'com.github.ZhangYuanYang-spadger:AndroidMVVMCore:1.0.0'
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
        launchOnIO {
            val result = repository.getData()
            result.onSuccess { data.value = it }
            result.onError { showError(it.message) }
        }
    }
}
```

### BaseRepository

```kotlin
class MainRepository : BaseRepository() {
    suspend fun getData(): Result<String> {
        return apiCall { apiService.getData() }
    }
}
```

### Network Configuration

```kotlin
// Initialize Retrofit
RetrofitProvider.init("https://api.example.com/")

// Create Retrofit service
val apiService = createApi<ApiService>()
```

## License

MIT License

## Contributing

Feel free to submit issues and enhancement requests.
