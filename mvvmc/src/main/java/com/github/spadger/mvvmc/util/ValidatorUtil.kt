/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 表单验证工具类
 */
package com.github.spadger.mvvmc.util

import java.util.regex.Pattern

object ValidatorUtil {

    /**
     * 验证结果
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String = ""
    )

    // ==================== 通用验证 ====================

    /**
     * 检查是否为空
     */
    fun isEmpty(value: String?): Boolean {
        return value.isNullOrEmpty()
    }

    /**
     * 检查是否为空（包含空白字符）
     */
    fun isBlank(value: String?): Boolean {
        return value.isNullOrBlank()
    }

    /**
     * 检查长度范围
     */
    fun checkLength(value: String, minLength: Int, maxLength: Int): ValidationResult {
        return when {
            isEmpty(value) -> ValidationResult(false, "内容不能为空")
            value.length < minLength -> ValidationResult(false, "内容长度不能少于$minLength个字符")
            value.length > maxLength -> ValidationResult(false, "内容长度不能超过$maxLength个字符")
            else -> ValidationResult(true)
        }
    }

    // ==================== 邮箱验证 ====================

    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )

    /**
     * 验证邮箱格式
     */
    fun isEmailValid(email: String): ValidationResult {
        return when {
            isEmpty(email) -> ValidationResult(false, "邮箱地址不能为空")
            !EMAIL_PATTERN.matcher(email).matches() -> ValidationResult(false, "请输入有效的邮箱地址")
            else -> ValidationResult(true)
        }
    }

    /**
     * 验证邮箱格式（仅检查格式，不检查空）
     */
    fun isEmailFormatValid(email: String): Boolean {
        return !isEmpty(email) && EMAIL_PATTERN.matcher(email).matches()
    }

    // ==================== 手机号验证 ====================

    private val PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    )

    /**
     * 验证手机号格式
     */
    fun isPhoneValid(phone: String): ValidationResult {
        return when {
            isEmpty(phone) -> ValidationResult(false, "手机号码不能为空")
            !PHONE_PATTERN.matcher(phone).matches() -> ValidationResult(false, "请输入有效的手机号码")
            else -> ValidationResult(true)
        }
    }

    /**
     * 验证手机号格式（仅检查格式，不检查空）
     */
    fun isPhoneFormatValid(phone: String): Boolean {
        return !isEmpty(phone) && PHONE_PATTERN.matcher(phone).matches()
    }

    // ==================== 身份证验证 ====================

    private val ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$"
    )

    /**
     * 验证身份证号码格式
     */
    fun isIdCardValid(idCard: String): ValidationResult {
        return when {
            isEmpty(idCard) -> ValidationResult(false, "身份证号码不能为空")
            !ID_CARD_PATTERN.matcher(idCard).matches() -> ValidationResult(false, "请输入有效的身份证号码")
            !validateIdCardChecksum(idCard) -> ValidationResult(false, "身份证号码校验码错误")
            else -> ValidationResult(true)
        }
    }

    /**
     * 验证身份证校验码
     */
    private fun validateIdCardChecksum(idCard: String): Boolean {
        if (idCard.length != 18) return false

        val coefficients = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
        val checkCodes = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')

        var sum = 0
        for (i in 0..16) {
            sum += (idCard[i] - '0') * coefficients[i]
        }

        val remainder = sum % 11
        return idCard[17].uppercaseChar() == checkCodes[remainder]
    }

    // ==================== 密码验证 ====================

    /**
     * 验证密码强度（至少8位，包含字母和数字）
     */
    fun isPasswordValid(password: String): ValidationResult {
        return when {
            isEmpty(password) -> ValidationResult(false, "密码不能为空")
            password.length < 8 -> ValidationResult(false, "密码长度不能少于8位")
            !containsLetter(password) -> ValidationResult(false, "密码必须包含字母")
            !containsDigit(password) -> ValidationResult(false, "密码必须包含数字")
            else -> ValidationResult(true)
        }
    }

    /**
     * 验证密码强度（至少8位，包含大小写字母和数字）
     */
    fun isStrongPasswordValid(password: String): ValidationResult {
        return when {
            isEmpty(password) -> ValidationResult(false, "密码不能为空")
            password.length < 8 -> ValidationResult(false, "密码长度不能少于8位")
            !containsLowerCase(password) -> ValidationResult(false, "密码必须包含小写字母")
            !containsUpperCase(password) -> ValidationResult(false, "密码必须包含大写字母")
            !containsDigit(password) -> ValidationResult(false, "密码必须包含数字")
            else -> ValidationResult(true)
        }
    }

    /**
     * 检查两个密码是否一致
     */
    fun isPasswordMatch(password: String, confirmPassword: String): ValidationResult {
        return when {
            isEmpty(password) -> ValidationResult(false, "密码不能为空")
            isEmpty(confirmPassword) -> ValidationResult(false, "确认密码不能为空")
            password != confirmPassword -> ValidationResult(false, "两次输入的密码不一致")
            else -> ValidationResult(true)
        }
    }

    // ==================== URL验证 ====================

    private val URL_PATTERN = Pattern.compile(
        "^(https?://)?[a-zA-Z0-9][-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$"
    )

    /**
     * 验证URL格式
     */
    fun isUrlValid(url: String): ValidationResult {
        return when {
            isEmpty(url) -> ValidationResult(false, "URL不能为空")
            !URL_PATTERN.matcher(url).matches() -> ValidationResult(false, "请输入有效的URL")
            else -> ValidationResult(true)
        }
    }

    // ==================== IP地址验证 ====================

    private val IP_V4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    )

    /**
     * 验证IPv4地址格式
     */
    fun isIpV4Valid(ip: String): ValidationResult {
        return when {
            isEmpty(ip) -> ValidationResult(false, "IP地址不能为空")
            !IP_V4_PATTERN.matcher(ip).matches() -> ValidationResult(false, "请输入有效的IPv4地址")
            else -> ValidationResult(true)
        }
    }

    // ==================== 银行卡号验证 ====================

    /**
     * 使用Luhn算法验证银行卡号
     */
    fun isBankCardValid(cardNumber: String): ValidationResult {
        val cleanNumber = cardNumber.replace("\\s+".toRegex(), "")

        return when {
            isEmpty(cleanNumber) -> ValidationResult(false, "银行卡号不能为空")
            cleanNumber.length < 16 || cleanNumber.length > 19 -> ValidationResult(false, "请输入有效的银行卡号")
            !cleanNumber.all { it.isDigit() } -> ValidationResult(false, "银行卡号只能包含数字")
            !luhnCheck(cleanNumber) -> ValidationResult(false, "银行卡号校验失败")
            else -> ValidationResult(true)
        }
    }

    /**
     * Luhn算法校验
     */
    private fun luhnCheck(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false

        for (i in cardNumber.length - 1 downTo 0) {
            var digit = cardNumber[i] - '0'

            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            sum += digit
            alternate = !alternate
        }

        return sum % 10 == 0
    }

    // ==================== 车牌号验证 ====================

    private val LICENSE_PLATE_PATTERN = Pattern.compile(
        "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{5}$"
    )

    /**
     * 验证车牌号格式
     */
    fun isLicensePlateValid(plateNumber: String): ValidationResult {
        return when {
            isEmpty(plateNumber) -> ValidationResult(false, "车牌号不能为空")
            !LICENSE_PLATE_PATTERN.matcher(plateNumber).matches() -> ValidationResult(false, "请输入有效的车牌号")
            else -> ValidationResult(true)
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 是否包含字母
     */
    fun containsLetter(value: String): Boolean {
        return value.any { it.isLetter() }
    }

    /**
     * 是否包含小写字母
     */
    fun containsLowerCase(value: String): Boolean {
        return value.any { it.isLowerCase() }
    }

    /**
     * 是否包含大写字母
     */
    fun containsUpperCase(value: String): Boolean {
        return value.any { it.isUpperCase() }
    }

    /**
     * 是否包含数字
     */
    fun containsDigit(value: String): Boolean {
        return value.any { it.isDigit() }
    }

    /**
     * 是否只包含字母和数字
     */
    fun isAlphanumeric(value: String): Boolean {
        return value.all { it.isLetterOrDigit() }
    }

    /**
     * 是否只包含中文
     */
    fun isChinese(value: String): Boolean {
        return value.all { it.code in 0x4e00..0x9fa5 }
    }

    /**
     * 是否包含特殊字符
     */
    fun containsSpecialCharacter(value: String): Boolean {
        val specialChars = "!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`"
        return value.any { it in specialChars }
    }
}