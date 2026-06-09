package com.github.spadger.mvvmc.demo.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.github.spadger.mvvmc.BaseBindingFragment
import com.github.spadger.mvvmc.demo.adapter.ContractListAdapter
import com.github.spadger.mvvmc.demo.databinding.FragmentHomeBinding
import com.github.spadger.mvvmc.demo.model.LoginResponse
import com.github.spadger.mvvmc.demo.vm.DemoViewModel
import com.github.spadger.mvvmc.ext.observeResult
import com.github.spadger.mvvmc.pager.observeData
import com.github.spadger.mvvmc.pager.observeState
import com.github.spadger.mvvmc.pager.setupRecyclerViewPaging
import com.github.spadger.mvvmc.util.DataStoreHolder
import com.github.spadger.mvvmc.util.DataStoreKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, DemoViewModel>() {

    private lateinit var adapter: ContractListAdapter

    override fun initView() {
        adapter = ContractListAdapter()
        mBinding.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mBinding.recyclerView.adapter = adapter
    }

    override fun initObserve() {
        mVM.contractPager.observeData(requireActivity()) { adapter.submitList(it) }

        mVM.contractPager.observeState(
            owner = requireActivity(),
            onLoading = { showLoading("加载中...") },
            onLoadingMore = { adapter.showLoading(true, mBinding.recyclerView) },
            onRefreshing = { showLoading("刷新中...") },
            onSuccess = { page, totalCount, hasMore ->
                hideLoading()
                adapter.showLoading(false, mBinding.recyclerView)
                mBinding.swipeRefreshLayout.isRefreshing = false
                updateListInfo(page, totalCount, hasMore)
            },
            onError = { message ->
                hideLoading()
                adapter.showLoading(false, mBinding.recyclerView)
                mBinding.swipeRefreshLayout.isRefreshing = false
                showError(message ?: "加载失败")
            }
        )

        mVM.loginBodyResult.observeResult(
            this,
            onSuccess = { handleLoginSuccess(it)
                        hideLoading()},
            onError = { showError("登录失败: ${it.message}") },
            onLoading = { showLoading("登录中...") }
        )
    }

    override fun initData() {
        mVM.loadContractList()
    }

    override fun initListener() {
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mVM.refreshContractList()
        }

        mBinding.btnLogin.setOnClickListener {
            val username = mBinding.etUsername.text.toString()
            val password = mBinding.etPassword.text.toString()
            if (username.isNotBlank() && password.isNotBlank()) {
                mVM.login(username, getMD5Str(password))
            } else {
                showWarning("请输入用户名和密码")
            }
        }

        mBinding.btnLoadData.setOnClickListener {
            mVM.loadContractList()
        }

        mBinding.btnDataStore.setOnClickListener {
            initDataStore()
        }

        adapter.setOnItemClickListener { contract ->
            showInfo("合同号: ${contract.Contract_Num}\n线路: ${contract.Line_Name}")
        }

        mVM.contractPager.setupRecyclerViewPaging(mBinding.recyclerView)
    }

    private fun updateListInfo(page: Int, count: Int, hasMore: Boolean) {
        mBinding.tvListInfo.text = "共 $count 条数据，第 $page 页${if (hasMore) "，可加载更多" else "，已加载全部"}"
    }

    private fun handleLoginSuccess(response: LoginResponse) {
        showSuccess("登录成功！欢迎 ${response.User_Name}")
    }

    private fun initDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreHolder.getInstance().putString(DataStoreKeys.USER_TOKEN, "test_token_123")
            DataStoreHolder.getInstance().putBoolean(DataStoreKeys.IS_LOGGED_IN, true)
            requireActivity().runOnUiThread { showSuccess("DataStore 测试完成") }
        }
    }

    private fun getMD5Str(str: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(str.toByteArray())
            val messageDigest = digest.digest()

            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }

            return hexString.toString().uppercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}
