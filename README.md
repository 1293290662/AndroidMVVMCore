# AndroidMVVMCore

一个现代化的 Android MVVM 框架，提供完整的基础组件封装，帮助开发者快速构建高质量的 Android 应用。

---

## 📋 目录

1. [作者](#作者)
2. [功能特性](#功能特性)
3. [安装指南](#安装指南)
4. [快速开始](#快速开始)
5. [基类组件](#基类组件)
6. [网络请求](#网络请求)
7. [数据存储](#数据存储)
8. [工具类](#工具类)
9. [状态管理](#状态管理)
10. [导航管理](#导航管理)
11. [系统栏管理](#系统栏管理)
12. [表单验证](#表单验证)
13. [日期处理](#日期处理)
14. [项目结构](#项目结构)
15. [许可证](#许可证)

---

## 作者

**ZhangYuanYang**  
邮箱: 1293290662@qq.com  
GitHub: https://github.com/ZhangYuanYang-spadger

---

## 功能特性

- **BaseActivity**: Activity 基类，支持 ViewModel 和生命周期管理
- **BaseFragment**: Fragment 基类，支持 ViewModel 和懒加载
- **BaseBindingActivity**: 支持 View Binding 的 Activity
- **BaseBindingFragment**: 支持 View Binding 的 Fragment
- **BaseViewModel**: ViewModel 基类，提供协程和错误处理
- **BaseRepository**: Repository 基类，封装网络请求
- **网络组件**: Retrofit、OkHttp 配置和拦截器
- **结果处理**: 通用的 Result 和 NetworkResponse 封装
- **DataStore**: Jetpack DataStore 封装
- **UI 工具**: LogUtil、ToastUtil、DialogBuilder、StateLayout
- **导航管理**: Navigator 封装
- **系统栏**: Edge-to-Edge 支持和 Insets 处理
- **表单验证**: 常用验证工具

---

## 安装指南

### 步骤 1: 添加 JitPack 仓库

在 `settings.gradle.kts` 中添加：

```gradle
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 步骤 2: 添加依赖

```gradle
dependencies {
    implementation("com.github.ZhangYuanYang-spadger:AndroidMVVMCore:1.0.0")
}
```

---

## 快速开始

### 在 Application 中初始化

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 Retrofit
        RetrofitProvider.init("https://api.example.com/")
        
        // 初始化 DataStore
        DataStoreHolder.init(this)
        
        // 开启调试日志
        LogUtil.isDebug = BuildConfig.DEBUG
    }
}
```

---

## 基类组件

### BaseBindingActivity（推荐）

```kotlin
class MainActivity : BaseBindingActivity<MainViewModel, ActivityMainBinding>() {

    override fun createViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.tvTitle.text = "你好世界"
    }

    override fun createViewModel(): MainViewModel {
        return getViewModel()
    }

    override fun setupObservers() {
        viewModel.data.observe(this) { data ->
            // 处理数据
        }
    }

    override fun initData() {
        viewModel.loadData()
    }
}
```

### BaseViewModel

```kotlin
class MainViewModel : BaseViewModel() {
    
    val data = MutableLiveData<String>()
    
    fun loadData() {
        launchOnIO {
            val result = repository.fetchData()
            when (result) {
                is Result.Success -> data.value = result.data
                is Result.Error -> handleError(result.exception)
            }
        }
    }
}
```

### BaseRepository

```kotlin
class MainRepository : BaseRepository() {
    
    private val apiService = createApi<ApiService>()
    
    suspend fun fetchData(): Result<String> {
        return apiCall { apiService.getData() }
    }
}
```

---

## 网络请求

### 创建 API 服务

```kotlin
interface ApiService {
    @GET("users")
    suspend fun getUsers(): NetworkResponse<List<User>>
    
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): NetworkResponse<User>
}
```

### 处理响应

```kotlin
viewModel.state.observeResult(
    this,
    onSuccess = { user -> handleSuccess(user) },
    onError = { e -> handleError(e) },
    onLoading = { showLoading() }
)
```

---

## 数据存储

### DataStore 使用

```kotlin
// 写入数据
CoroutineScope(Dispatchers.IO).launch {
    DataStoreHolder.getInstance().putString("token", "abc123")
    DataStoreHolder.getInstance().putBoolean("isLoggedIn", true)
}

// 读取数据
val token = DataStoreHolder.getInstance().getString("token")

// 监听数据变化
DataStoreHolder.getInstance().getStringFlow("token")
    .collect { token ->
        // 处理 token 变化
    }
```

---

## 工具类

### LogUtil

```kotlin
LogUtil.d("调试信息")
LogUtil.i("信息日志")
LogUtil.w("警告日志")
LogUtil.e("错误日志", exception)
LogUtil.json(jsonString)
```

### ToastUtil

```kotlin
ToastUtil.showShort(context, "短提示")
ToastUtil.showLong(context, "长提示")
ToastUtil.showSuccess(context, "成功")
ToastUtil.showError(context, "错误")
ToastUtil.showWarning(context, "警告")
```

### DialogBuilder

```kotlin
DialogBuilder.showMessage(context, "消息内容")
DialogBuilder.showConfirm(context, "确认删除？", onConfirm, onCancel)
```

---

## 状态管理

### StateLayout

```xml
<com.github.spadger.mvvmc.widget.StateLayout
    android:id="@+id/stateLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- 内容布局 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
    
</com.github.spadger.mvvmc.widget.StateLayout>
```

```kotlin
stateLayout.showLoading()        // 显示加载中
stateLayout.showContent()        // 显示内容
stateLayout.showEmpty("暂无数据")  // 显示空状态
stateLayout.showError("加载失败")  // 显示错误
stateLayout.showNoNetwork()      // 显示无网络
stateLayout.setOnErrorRetryClickListener { loadData() }  // 设置重试点击
```

---

## 导航管理

### Navigator

```kotlin
Navigator.navigate(fragment, R.id.action_home_to_detail)  // 导航到详情页
Navigator.navigate(fragment, R.id.action_home_to_detail, args)  // 带参数导航
Navigator.popBackStack(fragment)  // 返回上一级
Navigator.popToRoot(fragment)     // 返回栈底
```

### 导航选项

```kotlin
NavOptionsFactory.slideInFromRight()   // 从右侧进入
NavOptionsFactory.slideInFromBottom()  // 从底部进入
NavOptionsFactory.fadeInFadeOut()      // 淡入淡出
NavOptionsFactory.clearBackStack()     // 清除返回栈
```

---

## 系统栏管理

### Edge-to-Edge

```kotlin
// 启用 Edge-to-Edge（默认启用）
enableEdgeToEdge()

// 透明状态栏
setTransparentStatusBar()

// 浅色状态栏图标
setLightStatusBar(true)

// 隐藏系统栏
hideSystemBars()
```

### View Insets 适配

```kotlin
binding.toolbar.applyStatusBarPadding()      // 状态栏 padding
binding.bottomNav.applyNavigationBarPadding() // 导航栏 padding
binding.content.applySystemBarPadding()      // 系统栏 padding
```

---

## 表单验证

### ValidatorUtil

```kotlin
ValidatorUtil.isPhoneValid("13800138000")        // 手机号验证
ValidatorUtil.isEmailValid("test@example.com")    // 邮箱验证
ValidatorUtil.isIdCardValid("110101199001011234") // 身份证验证
ValidatorUtil.isPasswordValid("Password123")      // 密码验证
ValidatorUtil.isUrlValid("https://example.com")   // URL验证
```

---

## 日期处理

### DateUtil

```kotlin
DateUtil.getCurrentDateTime()           // 获取当前日期时间
DateUtil.formatDate(timestamp, "yyyy-MM-dd")  // 格式化日期
DateUtil.parseDate("2024-01-01", "yyyy-MM-dd") // 解析日期
DateUtil.smartFormat(timestamp)         // 智能格式化（刚刚、5分钟前等）
DateUtil.isToday(timestamp)             // 判断是否是今天
DateUtil.formatDuration(3661)           // 格式化时长（1小时1分钟1秒）
```

---

## 项目结构

```
mvvmc/
├── BaseActivity.kt              # Activity 基类
├── BaseFragment.kt              # Fragment 基类
├── BaseBindingActivity.kt       # View Binding Activity
├── BaseBindingFragment.kt       # View Binding Fragment
├── BaseViewModel.kt             # ViewModel 基类
├── BaseRepository.kt            # Repository 基类
├── Result.kt                    # 结果封装
├── NetworkResponse.kt           # 网络响应封装
├── ErrorHandler.kt              # 错误处理
├── ext/
│   ├── LiveDataExt.kt           # LiveData 扩展
│   └── ViewInsetsExt.kt         # View Insets 扩展
├── model/
│   └── DataModels.kt            # 数据模型
├── nav/
│   ├── Navigator.kt             # 导航管理器
│   ├── NavOptionsFactory.kt     # 导航选项
│   └── NavParams.kt             # 导航参数
├── network/
│   ├── RetrofitProvider.kt      # Retrofit 配置
│   ├── OkHttpProvider.kt        # OkHttp 配置
│   └── HeaderInterceptor.kt     # 请求头拦截器
├── util/
│   ├── LogUtil.kt               # 日志工具
│   ├── ToastUtil.kt             # Toast 工具
│   ├── DialogBuilder.kt         # 对话框工具
│   ├── DataStoreManager.kt      # DataStore 封装
│   ├── PermissionHelper.kt      # 权限工具
│   ├── SystemBarHelper.kt       # 系统栏工具
│   ├── DateUtil.kt              # 日期工具
│   └── ValidatorUtil.kt         # 验证工具
├── vm/
│   └── StateViewModel.kt        # 状态管理 ViewModel
└── widget/
    └── StateLayout.kt           # 状态管理布局
```

---

## 许可证

MIT License

---

## 贡献

欢迎提交 issue 和改进建议！

---

*版本: 1.0.0*
