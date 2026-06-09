# AndroidMVVMCore

一个现代化的 Android MVVM 框架，提供完整的基础组件封装，帮助开发者快速构建高质量的 Android 应用。

---

## 📋 目录

1. [作者](#作者)
2. [功能特性](#功能特性)
3. [安装指南](#安装指南)
4. [快速开始](#快速开始)
5. [基类组件](#基类组件)
6. [分页组件](#分页组件)
7. [网络请求](#网络请求)
8. [数据存储](#数据存储)
9. [工具类](#工具类)
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

- **BaseActivity**: Activity 基类，提供权限请求、页面跳转、Toast 等通用功能
- **BaseFragment**: Fragment 基类，支持懒加载
- **BaseBindingActivity**: 支持 View Binding 的 Activity（自动绑定，双泛型 `<VB, VM>`）
- **BaseBindingFragment**: 支持 View Binding 的 Fragment（自动绑定，双泛型 `<VB, VM>`）
- **BaseViewModel**: ViewModel 基类，提供协程和错误处理
- **BaseRepository**: Repository 基类，封装网络请求
- **Pager**: 可组合的分页管理器，支持多个分页数据源
- **PagerExt**: 分页组件扩展函数，简化观察和数据绑定
- **网络组件**: Retrofit、OkHttp 配置和拦截器
- **结果处理**: 通用的 Result 密封类封装（Success/Error/Loading/Idle）
- **DataStore**: Jetpack DataStore 封装（替代 SharedPreferences）
- **UI 工具**: LogUtil、ToastUtil、DialogBuilder、PermissionHelper
- **导航管理**: Navigator 封装 Navigation Component
- **系统栏**: SystemBarHelper 和 View Insets 处理
- **表单验证**: ValidatorUtil（手机号、邮箱、身份证等）
- **日期工具**: DateUtil 日期处理和格式化

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
    implementation("com.github.ZhangYuanYang-spadger:AndroidMVVMCore:1.0.4")
}
```

---

## 快速开始

### 在 Application 中初始化

```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 Retrofit
        RetrofitProvider.init("https://api.example.com/")
        
        // 初始化 DataStore
        DataStoreManager.getInstance().init(this)
        
        // 开启调试日志
        LogUtil.isDebug = BuildConfig.DEBUG
    }
}
```

---

## 基类组件

### BaseBindingActivity（推荐）

使用双泛型 `<VB, VM>`，通过反射自动绑定 ViewBinding：

```kotlin
class MainActivity : BaseBindingActivity<ActivityMainBinding, DemoViewModel>() {

    override fun initView() {
        mBinding.tvTitle.text = "你好世界"
    }

    override fun initObserve() {
        mVM.loginResult.observeWithLoading(this) { user ->
            handleLoginSuccess(user)
        }
    }

    override fun initData() {
        mVM.loadContractList()
    }

    override fun initListener() {
        mBinding.btnLogin.setOnClickListener {
            val username = mBinding.etUsername.text.toString()
            val password = mBinding.etPassword.text.toString()
            mVM.login(username, password)
        }
    }
}
```

### BaseBindingFragment

```kotlin
class HomeFragment : BaseBindingFragment<FragmentHomeBinding, DemoViewModel>() {

    override fun initView() {
        mBinding.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mBinding.recyclerView.adapter = contractAdapter
    }

    override fun initObserve() {
        mVM.contractPager.observeData(mActivity) { contractAdapter.submitList(it) }
        mVM.loginResult.observeWithLoading(this) { handleLoginSuccess(it) }
    }

    override fun initData() {
        mVM.loadContractList()
    }
}
```

### BaseViewModel

```kotlin
class DemoViewModel : BaseViewModel() {
    
    // 使用 VmLiveData 封装网络请求结果
    var loginResult: VmLiveData<LoginResponse> = MutableLiveData()
    
    fun login(username: String, password: String) {
        launchVmRequest({ repository.login(username, password) }, loginResult)
    }
    
    // 创建分页器
    val contractPager: Pager<ContractResponse> = createPager(pageSize = 10) { page, size ->
        repository.getContractList(page, size)
    }
    
    fun loadContractList() {
        contractPager.loadData()
    }
}
```

### BaseRepository

```kotlin
class DemoRepository : BaseRepository() {
    
    private val apiService = createApi<ApiService>()
    
    suspend fun login(username: String, password: String): BaseResponse<LoginResponse> {
        return try {
            apiService.login(LoginRequest(username, password))
        } catch (e: Exception) {
            BaseResponse(active = false, code = -1, message = e.message)
        }
    }
    
    suspend fun getContractList(page: Int, size: Int): PageResponse<ContractResponse> {
        return try {
            apiService.getContractList(page, size)
        } catch (e: Exception) {
            PageResponse(active = false, code = -1, message = e.message)
        }
    }
}
```

### 权限请求

```kotlin
requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 
    onGranted = { showToast("权限已授予") },
    onDenied = { showToast("权限被拒绝") }
)
```

---

## 分页组件

### Pager 分页器

在 ViewModel 中创建分页器：

```kotlin
class DemoViewModel : BaseViewModel() {
    
    val contractPager: Pager<ContractResponse> = createPager(pageSize = 10) { page, size ->
        repository.getContractList(page, size)
    }
    
    fun loadContractList() {
        contractPager.loadData()
    }
    
    fun loadMoreContracts() {
        contractPager.loadMore()
    }
    
    fun refreshContractList() {
        contractPager.refresh()
    }
}
```

### 使用 PagerExt 扩展函数

```kotlin
// 观察数据变化
mVM.contractPager.observeData(mActivity) { adapter.submitList(it) }

// 观察加载状态
mVM.contractPager.observeState(
    owner = mActivity,
    onLoading = { showLoadingDialog() },
    onLoadingMore = { adapter.showLoading(true) },
    onRefreshing = { mBinding.swipeRefreshLayout.isRefreshing = true },
    onSuccess = { page, totalCount, hasMore ->
        dismissLoadingDialog()
        adapter.showLoading(false)
        mBinding.swipeRefreshLayout.isRefreshing = false
    },
    onError = { message ->
        dismissLoadingDialog()
        showToast(message ?: "加载失败")
    }
)

// 自动设置 RecyclerView 滚动加载更多
mVM.contractPager.setupRecyclerViewPaging(mBinding.recyclerView)
```

---

## 网络请求

### 创建 API 服务

```kotlin
interface ApiService {
    @POST("api/User/LoginDriver")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>
    
    @GET("api/Contract/List")
    suspend fun getContractList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PageResponse<ContractResponse>
}
```

### 响应模型

```kotlin
// 通用响应模型
open class BaseResponse<T>(
    val active: Boolean,
    val code: Int = 0,
    override val message: String? = null,
    override val data: T? = null
) : BaseData<T> {
    override val isSuccess: Boolean get() = code == 0 || code == 200 || active
}

// 分页响应模型
open class PageResponse<T>(
    val active: Boolean,
    val code: Int = 0,
    override val message: String? = null,
    override val data: T? = null,
    val total: Int = 0,
    val pageSize: Int = 0
) : BaseData<T> {
    override val isSuccess: Boolean get() = code == 0 || code == 200 || active
}
```

---

## 数据存储

### DataStore 使用

```kotlin
// 写入数据（在协程中）
CoroutineScope(Dispatchers.IO).launch {
    DataStoreManager.getInstance().putString("token", "abc123")
    DataStoreManager.getInstance().putBoolean("isLoggedIn", true)
    DataStoreManager.getInstance().putInt("userId", 123)
}

// 读取数据（在协程中）
val token = DataStoreManager.getInstance().getString("token")

// 监听数据变化
DataStoreManager.getInstance().getStringFlow("token")
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
LogUtil.obj(someObject)  // 直接输出对象为 JSON
someObject.log()        // 扩展方法，直接输出
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

### SystemBarHelper

```kotlin
// 透明状态栏
setTransparentStatusBar()

// 浅色状态栏图标
setLightStatusBar(true)

// 启用 Edge-to-Edge
enableEdgeToEdge()
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
├── BaseActivity.kt              # Activity 基类（权限、跳转、Toast等）
├── BaseFragment.kt              # Fragment 基类（懒加载）
├── BaseBindingActivity.kt       # View Binding Activity（自动绑定）
├── BaseBindingFragment.kt       # View Binding Fragment（自动绑定）
├── BaseRepository.kt            # Repository 基类
├── Result.kt                    # 结果密封类（Success/Error/Loading/Idle）
├── NetworkResponse.kt           # 网络响应封装
├── ErrorHandler.kt              # 错误处理
├── ext/
│   ├── LiveDataExt.kt           # LiveData 扩展（observeWithLoading等）
│   ├── ViewInsetsExt.kt         # View Insets 扩展
│   └── VmStateExt.kt            # ViewModel 状态扩展
├── model/
│   ├── BaseData.kt              # 基础数据接口
│   └── DataModels.kt            # 数据模型（BaseResponse、PageResponse等）
├── nav/
│   ├── Navigator.kt              # 导航管理器
│   ├── NavOptionsFactory.kt     # 导航选项
│   └── NavParams.kt              # 导航参数
├── network/
│   ├── RetrofitProvider.kt       # Retrofit 配置
│   ├── OkHttpProvider.kt         # OkHttp 配置
│   └── HeaderInterceptor.kt      # 请求头拦截器
├── pager/
│   ├── Pager.kt                  # 分页管理器
│   └── PagerExt.kt               # 分页组件扩展函数
├── util/
│   ├── LogUtil.kt                # 日志工具
│   ├── ToastUtil.kt              # Toast 工具
│   ├── DialogBuilder.kt          # 对话框工具
│   ├── DataStoreManager.kt       # DataStore 封装
│   ├── PermissionHelper.kt       # 权限工具
│   ├── SystemBarHelper.kt         # 系统栏工具
│   ├── DateUtil.kt               # 日期工具
│   └── ValidatorUtil.kt          # 验证工具
└── vm/
    └── BaseViewModel.kt          # ViewModel 基类（协程、错误处理）
```

---

## 许可证

MIT License

---

## 贡献

欢迎提交 issue 和改进建议！

---

*版本: 1.0.4*
