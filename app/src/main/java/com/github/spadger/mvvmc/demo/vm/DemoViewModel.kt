/**
 * @author ZhangYuanYang
 * @email 1293290662@qq.com
 * @date 2024/01/01
 *
 * 演示 ViewModel
 */
package com.github.spadger.mvvmc.demo.vm

import androidx.lifecycle.MutableLiveData
import com.github.spadger.mvvmc.vm.BaseViewModel
import com.github.spadger.mvvmc.demo.model.ContractResponse
import com.github.spadger.mvvmc.demo.model.LoginRequest
import com.github.spadger.mvvmc.demo.model.LoginResponse
import com.github.spadger.mvvmc.demo.repository.DemoRepository
import com.github.spadger.mvvmc.util.LogUtil
import com.github.spadger.mvvmc.vm.launchVmRequest
import com.github.spadger.mvvmc.pager.Pager
import com.github.spadger.mvvmc.pager.createPager
import com.github.spadger.mvvmc.ext.VmLiveData

class DemoViewModel : BaseViewModel() {

    private val repository = DemoRepository()

    val contractPager: Pager<ContractResponse> = createPager(pageSize = 10) { page, size ->
        repository.getContractList(page, size)
    }

    var loginBodyResult: VmLiveData<LoginResponse> = MutableLiveData()

    fun login(username: String, password: String) {
        launchVmRequest({ repository.login(LoginRequest(username, password)) }, loginBodyResult)
    }

    fun loadContractList() {
        LogUtil.d("DemoViewModel", "loadContractList called")
        contractPager.loadData()
    }

    fun loadMoreContracts() {
        contractPager.loadMore()
    }

    fun refreshContractList() {
        contractPager.refresh()
    }
}