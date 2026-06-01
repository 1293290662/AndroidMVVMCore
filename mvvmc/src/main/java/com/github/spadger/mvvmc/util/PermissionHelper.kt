/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 * 
 * Android 运行时权限封装类
 * 符合应用上架要求，支持 Android 13+ 新权限
 */
package com.github.spadger.mvvmc.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

/**
 * 权限请求回调
 */
interface PermissionCallback {
    fun onGranted()
    fun onDenied(deniedPermissions: List<String>, shouldShowRationale: Boolean)
}

/**
 * 权限请求结果
 */
data class PermissionResult(
    val granted: Boolean,
    val grantedPermissions: List<String>,
    val deniedPermissions: List<String>,
    val shouldShowRationale: Boolean
)

/**
 * 权限信息配置（用于权限说明）
 */
data class PermissionInfo(
    val permission: String,
    val title: String,
    val rationale: String,
    val settingsMessage: String
)

/**
 * 常用权限常量及说明（符合上架要求）
 */
object PermissionConstants {
    // ============ Android 6.0+ 危险权限 ============
    const val READ_CALENDAR = Manifest.permission.READ_CALENDAR
    const val WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR
    const val CAMERA = Manifest.permission.CAMERA
    const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
    const val WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS
    const val GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS
    const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
    const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
    const val CALL_PHONE = Manifest.permission.CALL_PHONE
    const val READ_CALL_LOG = Manifest.permission.READ_CALL_LOG
    const val WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG
    const val ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL
    const val USE_SIP = Manifest.permission.USE_SIP
    const val PROCESS_OUTGOING_CALLS = Manifest.permission.PROCESS_OUTGOING_CALLS
    const val BODY_SENSORS = Manifest.permission.BODY_SENSORS
    const val SEND_SMS = Manifest.permission.SEND_SMS
    const val RECEIVE_SMS = Manifest.permission.RECEIVE_SMS
    const val READ_SMS = Manifest.permission.READ_SMS
    const val RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH
    const val RECEIVE_MMS = Manifest.permission.RECEIVE_MMS
    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    
    // ============ Android 10+ 权限 ============
    const val ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    const val ACCESS_MEDIA_LOCATION = Manifest.permission.ACCESS_MEDIA_LOCATION
    
    // ============ Android 11+ 权限 ============
    const val MANAGE_EXTERNAL_STORAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE
    const val ACCESS_COARSE_LOCATION_11 = Manifest.permission.ACCESS_COARSE_LOCATION
    const val ACCESS_FINE_LOCATION_11 = Manifest.permission.ACCESS_FINE_LOCATION
    
    // ============ Android 13+ 权限 ============
    const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS
    const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO
    const val READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO
    const val NEARBY_WIFI_DEVICES = Manifest.permission.NEARBY_WIFI_DEVICES
    
    // ============ Android 14+ 权限 ============
    const val READ_MEDIA_VISUAL_USER_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    const val USE_BIOMETRIC_INTERNAL = Manifest.permission.USE_BIOMETRIC_INTERNAL
    
    // ============ 权限组 ============
    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf(READ_EXTERNAL_STORAGE)
    } else {
        arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    }
    
    val LOCATION_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    val BACKGROUND_LOCATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(ACCESS_BACKGROUND_LOCATION)
    } else {
        emptyArray()
    }
    
    val CONTACTS_PERMISSIONS = arrayOf(READ_CONTACTS, WRITE_CONTACTS)
    val CALL_LOG_PERMISSIONS = arrayOf(READ_CALL_LOG, WRITE_CALL_LOG)
    val SMS_PERMISSIONS = arrayOf(SEND_SMS, RECEIVE_SMS, READ_SMS)
    
    // ============ 权限说明配置（符合上架要求） ============
    val PERMISSION_INFO_MAP = mapOf(
        CAMERA to PermissionInfo(
            CAMERA,
            "相机权限",
            "为了能够拍摄照片和录制视频，需要获取相机权限。",
            "相机权限已被拒绝，请在应用设置中开启。"
        ),
        READ_EXTERNAL_STORAGE to PermissionInfo(
            READ_EXTERNAL_STORAGE,
            "存储权限",
            "为了能够读取和保存图片、视频等文件，需要获取存储权限。",
            "存储权限已被拒绝，请在应用设置中开启。"
        ),
        WRITE_EXTERNAL_STORAGE to PermissionInfo(
            WRITE_EXTERNAL_STORAGE,
            "存储权限",
            "为了能够保存下载的文件和编辑的内容，需要获取存储权限。",
            "存储权限已被拒绝，请在应用设置中开启。"
        ),
        READ_MEDIA_IMAGES to PermissionInfo(
            READ_MEDIA_IMAGES,
            "图片访问权限",
            "为了能够访问和选择相册中的图片，需要获取图片访问权限。",
            "图片访问权限已被拒绝，请在应用设置中开启。"
        ),
        READ_MEDIA_VIDEO to PermissionInfo(
            READ_MEDIA_VIDEO,
            "视频访问权限",
            "为了能够访问和选择相册中的视频，需要获取视频访问权限。",
            "视频访问权限已被拒绝，请在应用设置中开启。"
        ),
        READ_MEDIA_AUDIO to PermissionInfo(
            READ_MEDIA_AUDIO,
            "音频访问权限",
            "为了能够访问和选择设备中的音频文件，需要获取音频访问权限。",
            "音频访问权限已被拒绝，请在应用设置中开启。"
        ),
        ACCESS_FINE_LOCATION to PermissionInfo(
            ACCESS_FINE_LOCATION,
            "位置权限",
            "为了能够提供精准的定位服务，需要获取位置权限。",
            "位置权限已被拒绝，请在应用设置中开启。"
        ),
        ACCESS_COARSE_LOCATION to PermissionInfo(
            ACCESS_COARSE_LOCATION,
            "位置权限",
            "为了能够提供大致的位置服务，需要获取位置权限。",
            "位置权限已被拒绝，请在应用设置中开启。"
        ),
        ACCESS_BACKGROUND_LOCATION to PermissionInfo(
            ACCESS_BACKGROUND_LOCATION,
            "后台位置权限",
            "为了能够在应用后台时继续获取位置信息，需要获取后台位置权限。",
            "后台位置权限已被拒绝，请在应用设置中开启。"
        ),
        RECORD_AUDIO to PermissionInfo(
            RECORD_AUDIO,
            "麦克风权限",
            "为了能够录制音频和进行语音通话，需要获取麦克风权限。",
            "麦克风权限已被拒绝，请在应用设置中开启。"
        ),
        READ_CONTACTS to PermissionInfo(
            READ_CONTACTS,
            "联系人权限",
            "为了能够读取联系人信息，方便快速分享和邀请好友，需要获取联系人权限。",
            "联系人权限已被拒绝，请在应用设置中开启。"
        ),
        WRITE_CONTACTS to PermissionInfo(
            WRITE_CONTACTS,
            "联系人权限",
            "为了能够保存和更新联系人信息，需要获取联系人权限。",
            "联系人权限已被拒绝，请在应用设置中开启。"
        ),
        POST_NOTIFICATIONS to PermissionInfo(
            POST_NOTIFICATIONS,
            "通知权限",
            "为了能够接收应用的重要通知和消息提醒，需要获取通知权限。",
            "通知权限已被拒绝，请在应用设置中开启。"
        ),
        READ_PHONE_STATE to PermissionInfo(
            READ_PHONE_STATE,
            "电话状态权限",
            "为了能够获取设备信息和识别来电状态，需要获取电话状态权限。",
            "电话状态权限已被拒绝，请在应用设置中开启。"
        ),
        CALL_PHONE to PermissionInfo(
            CALL_PHONE,
            "拨打电话权限",
            "为了能够直接拨打电话，需要获取拨打电话权限。",
            "拨打电话权限已被拒绝，请在应用设置中开启。"
        ),
        SEND_SMS to PermissionInfo(
            SEND_SMS,
            "发送短信权限",
            "为了能够发送短信验证码和消息，需要获取发送短信权限。",
            "发送短信权限已被拒绝，请在应用设置中开启。"
        ),
        READ_SMS to PermissionInfo(
            READ_SMS,
            "读取短信权限",
            "为了能够自动读取短信验证码，需要获取读取短信权限。",
            "读取短信权限已被拒绝，请在应用设置中开启。"
        ),
        BODY_SENSORS to PermissionInfo(
            BODY_SENSORS,
            "传感器权限",
            "为了能够使用健康监测功能，需要获取传感器权限。",
            "传感器权限已被拒绝，请在应用设置中开启。"
        ),
        READ_CALENDAR to PermissionInfo(
            READ_CALENDAR,
            "日历权限",
            "为了能够读取日历事件和日程安排，需要获取日历权限。",
            "日历权限已被拒绝，请在应用设置中开启。"
        ),
        WRITE_CALENDAR to PermissionInfo(
            WRITE_CALENDAR,
            "日历权限",
            "为了能够创建和修改日历事件，需要获取日历权限。",
            "日历权限已被拒绝，请在应用设置中开启。"
        ),
        NEARBY_WIFI_DEVICES to PermissionInfo(
            NEARBY_WIFI_DEVICES,
            "附近设备权限",
            "为了能够发现和连接附近的设备，需要获取附近设备权限。",
            "附近设备权限已被拒绝，请在应用设置中开启。"
        )
    )
    
    /**
     * 获取权限说明信息
     */
    fun getPermissionInfo(permission: String): PermissionInfo? {
        return PERMISSION_INFO_MAP[permission]
    }
    
    /**
     * 获取权限组的组合说明
     */
    fun getPermissionsRationale(permissions: Array<String>): String {
        val infos = permissions.mapNotNull { PERMISSION_INFO_MAP[it] }
        return if (infos.isEmpty()) {
            "应用需要一些必要的权限来提供完整的功能。"
        } else if (infos.size == 1) {
            infos[0].rationale
        } else {
            val titles = infos.joinToString(", ") { it.title }
            "应用需要以下权限：$titles。这些权限用于提供更好的用户体验和完整的功能。"
        }
    }
}

/**
 * 权限管理帮助类（符合上架要求）
 */
class PermissionHelper(
    private val activity: AppCompatActivity,
    private val callback: PermissionCallback? = null,
    private val showRationaleDialog: Boolean = true,
    private val showSettingsDialog: Boolean = true
) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var pendingPermissions: Array<String> = emptyArray()
    
    init {
        initLauncher()
    }
    
    private fun initLauncher() {
        requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handleResult(permissions)
        }
    }
    
    /**
     * 请求权限（带说明引导）
     */
    fun requestPermission(permission: String) {
        requestPermissions(arrayOf(permission))
    }
    
    /**
     * 请求多个权限（带说明引导）
     */
    fun requestPermissions(permissions: Array<String>) {
        pendingPermissions = permissions
        
        val ungrantedPermissions = permissions.filter { !isPermissionGranted(it) }.toTypedArray()
        
        if (ungrantedPermissions.isEmpty()) {
            callback?.onGranted()
            return
        }
        
        // 检查是否需要显示权限说明
        val needRationale = ungrantedPermissions.any { shouldShowRationale(it) }
        
        if (showRationaleDialog && needRationale) {
            showPermissionRationaleDialog(ungrantedPermissions)
        } else {
            requestPermissionLauncher.launch(ungrantedPermissions)
        }
    }
    
    /**
     * 显示权限说明对话框
     */
    private fun showPermissionRationaleDialog(permissions: Array<String>) {
        val rationale = PermissionConstants.getPermissionsRationale(permissions)
        
        DialogBuilder(activity)
            .setTitle("需要权限")
            .setMessage(rationale)
            .setPositiveText("知道了")
            .setNegativeText("取消")
            .setPositiveListener {
                requestPermissionLauncher.launch(permissions)
            }
            .setNegativeListener {
                callback?.onDenied(permissions.toList(), false)
            }
            .show()
    }
    
    /**
     * 显示设置引导对话框（用户勾选"不再询问"后）
     */
    private fun showSettingsDialog(deniedPermissions: List<String>) {
        if (!showSettingsDialog) return
        
        val infos = deniedPermissions.mapNotNull { PermissionConstants.getPermissionInfo(it) }
        val message = if (infos.isEmpty()) {
            "部分权限已被拒绝且不再提示，请在应用设置中手动开启权限。"
        } else {
            infos.joinToString("\n") { it.settingsMessage }
        }
        
        DialogBuilder(activity)
            .setTitle("权限被拒绝")
            .setMessage(message)
            .setPositiveText("去设置")
            .setNegativeText("取消")
            .setPositiveListener {
                openAppSettings()
            }
            .setNegativeListener {
                callback?.onDenied(deniedPermissions, false)
            }
            .show()
    }
    
    /**
     * 处理权限请求结果
     */
    private fun handleResult(permissions: Map<String, Boolean>) {
        val grantedPermissions = permissions.filter { it.value }.keys.toList()
        val deniedPermissions = permissions.filter { !it.value }.keys.toList()
        
        if (deniedPermissions.isEmpty()) {
            callback?.onGranted()
        } else {
            val shouldShowRationale = deniedPermissions.any { shouldShowRationale(it) }
            
            if (!shouldShowRationale && showSettingsDialog) {
                showSettingsDialog(deniedPermissions)
            } else {
                callback?.onDenied(deniedPermissions, shouldShowRationale)
            }
        }
    }
    
    /**
     * 检查权限是否已授予
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 检查多个权限是否都已授予
     */
    fun areAllPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }
    
    /**
     * 获取未授予的权限列表
     */
    fun getDeniedPermissions(permissions: Array<String>): List<String> {
        return permissions.filter { !isPermissionGranted(it) }
    }
    
    /**
     * 是否需要显示权限说明
     */
    fun shouldShowRationale(permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    
    /**
     * 打开应用设置页面
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        activity.startActivity(intent)
    }
    
    /**
     * 请求权限并返回结果（挂起函数）
     */
    suspend fun requestPermissionsSuspend(permissions: Array<String>): PermissionResult {
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val tempCallback = object : PermissionCallback {
                override fun onGranted() {
                    continuation.resumeWith(
                        Result.success(
                            PermissionResult(
                                granted = true,
                                grantedPermissions = permissions.toList(),
                                deniedPermissions = emptyList(),
                                shouldShowRationale = false
                            )
                        )
                    )
                }
                
                override fun onDenied(deniedPermissions: List<String>, shouldShowRationale: Boolean) {
                    continuation.resumeWith(
                        Result.success(
                            PermissionResult(
                                granted = false,
                                grantedPermissions = permissions.filter { !deniedPermissions.contains(it) },
                                deniedPermissions = deniedPermissions,
                                shouldShowRationale = shouldShowRationale
                            )
                        )
                    )
                }
            }
            
            val helper = PermissionHelper(activity, tempCallback, showRationaleDialog, showSettingsDialog)
            helper.requestPermissions(permissions)
        }
    }
}

/**
 * Fragment 版本的权限管理帮助类
 */
class FragmentPermissionHelper(
    private val fragment: Fragment,
    private val callback: PermissionCallback? = null,
    private val showRationaleDialog: Boolean = true,
    private val showSettingsDialog: Boolean = true
) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var pendingPermissions: Array<String> = emptyArray()
    
    init {
        initLauncher()
    }
    
    private fun initLauncher() {
        requestPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handleResult(permissions)
        }
    }
    
    fun requestPermission(permission: String) {
        requestPermissions(arrayOf(permission))
    }
    
    fun requestPermissions(permissions: Array<String>) {
        pendingPermissions = permissions
        
        val ungrantedPermissions = permissions.filter { !isPermissionGranted(it) }.toTypedArray()
        
        if (ungrantedPermissions.isEmpty()) {
            callback?.onGranted()
            return
        }
        
        val needRationale = ungrantedPermissions.any { shouldShowRationale(it) }
        
        if (showRationaleDialog && needRationale) {
            showPermissionRationaleDialog(ungrantedPermissions)
        } else {
            requestPermissionLauncher.launch(ungrantedPermissions)
        }
    }
    
    private fun showPermissionRationaleDialog(permissions: Array<String>) {
        val rationale = PermissionConstants.getPermissionsRationale(permissions)
        
        DialogBuilder(fragment.requireContext())
            .setTitle("需要权限")
            .setMessage(rationale)
            .setPositiveText("知道了")
            .setNegativeText("取消")
            .setPositiveListener {
                requestPermissionLauncher.launch(permissions)
            }
            .setNegativeListener {
                callback?.onDenied(permissions.toList(), false)
            }
            .show()
    }
    
    private fun showSettingsDialog(deniedPermissions: List<String>) {
        if (!showSettingsDialog) return
        
        val infos = deniedPermissions.mapNotNull { PermissionConstants.getPermissionInfo(it) }
        val message = if (infos.isEmpty()) {
            "部分权限已被拒绝且不再提示，请在应用设置中手动开启权限。"
        } else {
            infos.joinToString("\n") { it.settingsMessage }
        }
        
        DialogBuilder(fragment.requireContext())
            .setTitle("权限被拒绝")
            .setMessage(message)
            .setPositiveText("去设置")
            .setNegativeText("取消")
            .setPositiveListener {
                openAppSettings()
            }
            .setNegativeListener {
                callback?.onDenied(deniedPermissions, false)
            }
            .show()
    }
    
    private fun handleResult(permissions: Map<String, Boolean>) {
        val grantedPermissions = permissions.filter { it.value }.keys.toList()
        val deniedPermissions = permissions.filter { !it.value }.keys.toList()
        
        if (deniedPermissions.isEmpty()) {
            callback?.onGranted()
        } else {
            val shouldShowRationale = deniedPermissions.any { shouldShowRationale(it) }
            
            if (!shouldShowRationale && showSettingsDialog) {
                showSettingsDialog(deniedPermissions)
            } else {
                callback?.onDenied(deniedPermissions, shouldShowRationale)
            }
        }
    }
    
    fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
    
    fun areAllPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }
    
    fun getDeniedPermissions(permissions: Array<String>): List<String> {
        return permissions.filter { !isPermissionGranted(it) }
    }
    
    fun shouldShowRationale(permission: String): Boolean {
        return fragment.shouldShowRequestPermissionRationale(permission)
    }
    
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", fragment.requireContext().packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        fragment.requireContext().startActivity(intent)
    }
}

/**
 * 权限工具类（静态方法）
 */
object PermissionUtil {
    
    /**
     * 检查单个权限是否已授予
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 检查多个权限是否都已授予
     */
    fun areAllPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }
    
    /**
     * 获取未授予的权限列表
     */
    fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { !isPermissionGranted(context, it) }
    }
    
    /**
     * 是否需要显示权限说明
     */
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    
    /**
     * 打开应用设置页面
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    
    /**
     * 检查是否是 Android 6.0+（需要运行时权限）
     */
    fun needRuntimePermissions(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
    
    /**
     * 获取权限友好名称
     */
    fun getPermissionName(permission: String): String {
        return PermissionConstants.getPermissionInfo(permission)?.title ?: permission
    }
    
    /**
     * 获取权限说明
     */
    fun getPermissionRationale(permission: String): String {
        return PermissionConstants.getPermissionInfo(permission)?.rationale ?: ""
    }
}