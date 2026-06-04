/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 演示数据仓库
 */
package com.github.spadger.mvvmc.demo.repository

import com.github.spadger.mvvmc.BaseRepository
import com.github.spadger.mvvmc.demo.model.ContractResponse
import com.github.spadger.mvvmc.demo.model.LoginRequest
import com.github.spadger.mvvmc.demo.model.LoginResponse
import com.github.spadger.mvvmc.model.BasePageResponse
import com.github.spadger.mvvmc.model.BaseResponse
import com.github.spadger.mvvmc.pager.PagedData
import com.github.spadger.mvvmc.util.LogUtil
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

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

    suspend fun getContractList(page: Int, pageSize: Int): PagedData<ContractResponse> {
        LogUtil.d("DemoRepository", "getContractList called: page=$page, pageSize=$pageSize")
        return try {
            val response = api.getContractList(
                start = ((page - 1) * pageSize).toLong(),
                limit = pageSize.toLong(),
                pageindex = page.toLong(),
                where = "{}"
            )
            val rows = response.rows ?: emptyList()
            val total = response.results
            val hasMore = (page * pageSize) < total
            
            PagedData(
                data = rows,
                page = page,
                totalPage = (total + pageSize - 1) / pageSize,
                totalCount = total,
                hasMore = hasMore
            )
        } catch (e: Exception) {
            LogUtil.e("DemoRepository", "getContractList error: ${e.message}", e)
            PagedData(emptyList(), page, 0, 0, false)
        }
    }
}

/**
 * 用户 API 接口
 * 定义用户相关的网络请求
 */
interface DemoApi {
    @POST("api/User/LoginDriver")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>

    @POST("api/Contract/List")
    suspend fun getContractList(
        @Query("start") start: Long,
        @Query("limit") limit: Long,
        @Query("pageindex") pageindex: Long,
        @Query("where") where: String
    ): BasePageResponse<ContractResponse>
}