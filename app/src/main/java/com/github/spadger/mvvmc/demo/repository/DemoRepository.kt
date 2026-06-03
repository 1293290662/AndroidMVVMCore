/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 演示数据仓库
 */
package com.github.spadger.mvvmc.demo.repository

import com.github.spadger.mvvmc.BaseRepository
import com.github.spadger.mvvmc.demo.model.LoginRequest
import com.github.spadger.mvvmc.demo.model.LoginResponse
import com.github.spadger.mvvmc.demo.model.UserDetail
import com.github.spadger.mvvmc.model.BaseResponse
import com.github.spadger.mvvmc.pager.PagedData
import kotlinx.coroutines.delay

class DemoRepository : BaseRepository() {

    private val api: DemoApi by lazy {
        createApi()
    }

    suspend fun login(request: LoginRequest): BaseResponse<LoginResponse> {
        return try {
            api.login(request)
        } catch (e: Exception) {
            BaseResponse(active = false, code = -1, message = e.message, data = null)
        }
    }

    suspend fun getUserList(page: Int, pageSize: Int): PagedData<UserDetail> {
        delay(1000)

        val totalCount = 100
        val totalPage = (totalCount + pageSize - 1) / pageSize
        val startIndex = (page - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, totalCount)

        val users = mutableListOf<UserDetail>()
        for (i in startIndex until endIndex) {
            users.add(UserDetail(
                id = "${i + 1}",
                name = "用户${i + 1}",
                email = "user${i + 1}@example.com"
            ))
        }

        return PagedData(
            data = users,
            page = page,
            totalPage = totalPage,
            totalCount = totalCount,
            hasMore = page < totalPage
        )
    }
}

/**
 * 用户 API 接口
 * 定义用户相关的网络请求
 */
interface DemoApi {
    @retrofit2.http.POST("api/User/LoginDriver")
    suspend fun login(@retrofit2.http.Body request: LoginRequest): BaseResponse<LoginResponse>
}