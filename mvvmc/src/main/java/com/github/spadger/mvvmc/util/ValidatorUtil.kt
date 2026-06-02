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

    data class ValidationResult(
        val isValid: Boolean,
        val message: String = ""
    )

    fun isEmpty(value: String?): Boolean {
        return value.isNullOrEmpty()
    }

    fun isBlank(value: String?): Boolean {
        return value.isNullOrBlank()
    }

    fun checkLength(value: String, minLength: Int, maxLength: Int): ValidationResult {
        return when {
            isEmpty(value) -> ValidationResult(false, "内容不能为空")
            value.length < minLength -> ValidationResult(false, "内容长度不能少于${minLength}个字符")
            value.length > maxLength -> ValidationResult(false, "内容长度不能超过${maxLength}个字符")
            else -> ValidationResult(true)
        }
    }

    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )

    fun isEmailValid(email: String): ValidationResult {
        return when {
            isBlank(email) -> ValidationResult(false, "邮箱不能为空")
            !EMAIL_PATTERN.matcher(email).matches() -> ValidationResult(false, "请输入有效的邮箱地址")
            else -> ValidationResult(true)
        }
    }

    private val PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    )

    fun isPhoneValid(phone: String): ValidationResult {
        return when {
            isBlank(phone) -> ValidationResult(false, "手机号不能为空")
            !PHONE_PATTERN.matcher(phone).matches() -> ValidationResult(false, "请输入有效的手机号码")
            else -> ValidationResult(true)
        }
    }

    private val ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$"
    )

    fun isIdCardValid(idCard: String): ValidationResult {
        return when {
            isBlank(idCard) -> ValidationResult(false, "身份证号不能为空")
            !ID_CARD_PATTERN.matcher(idCard).matches() -> ValidationResult(false, "请输入有效的身份证号")
            else -> ValidationResult(true)
        }
    }

    fun isPasswordValid(password: String): ValidationResult {
        return when {
            isBlank(password) -> ValidationResult(false, "密码不能为空")
            password.length < 6 -> ValidationResult(false, "密码长度不能少于6个字符")
            password.length > 20 -> ValidationResult(false, "密码长度不能超过20个字符")
            !password.matches(".*[a-zA-Z].*".toRegex()) -> ValidationResult(false, "密码必须包含字母")
            !password.matches(".*\\d.*".toRegex()) -> ValidationResult(false, "密码必须包含数字")
            else -> ValidationResult(true)
        }
    }

    fun isUrlValid(url: String): ValidationResult {
        return when {
            isBlank(url) -> ValidationResult(false, "URL不能为空")
            !url.startsWith("http://") && !url.startsWith("https://") -> ValidationResult(false, "URL必须以http://或https://开头")
            else -> ValidationResult(true)
        }
    }

    fun isNumberValid(number: String): ValidationResult {
        return when {
            isBlank(number) -> ValidationResult(false, "数字不能为空")
            !number.matches("-?\\d+".toRegex()) -> ValidationResult(false, "请输入有效的整数")
            else -> ValidationResult(true)
        }
    }

    fun isDecimalValid(decimal: String): ValidationResult {
        return when {
            isBlank(decimal) -> ValidationResult(false, "数字不能为空")
            !decimal.matches("-?\\d+\\.?\\d*".toRegex()) -> ValidationResult(false, "请输入有效的数字")
            else -> ValidationResult(true)
        }
    }

    fun isChineseValid(chinese: String): ValidationResult {
        return when {
            isBlank(chinese) -> ValidationResult(false, "内容不能为空")
            !chinese.matches("[\\u4e00-\\u9fa5]+".toRegex()) -> ValidationResult(false, "请输入中文")
            else -> ValidationResult(true)
        }
    }

    fun isUsernameValid(username: String): ValidationResult {
        return when {
            isBlank(username) -> ValidationResult(false, "用户名不能为空")
            username.length < 3 -> ValidationResult(false, "用户名长度不能少于3个字符")
            username.length > 20 -> ValidationResult(false, "用户名长度不能超过20个字符")
            !username.matches("^[a-zA-Z][a-zA-Z0-9_]*$".toRegex()) -> ValidationResult(false, "用户名只能以字母开头，包含字母、数字和下划线")
            else -> ValidationResult(true)
        }
    }

    fun validateAll(vararg results: ValidationResult): ValidationResult {
        results.forEach {
            if (!it.isValid) {
                return it
            }
        }
        return ValidationResult(true)
    }
}
