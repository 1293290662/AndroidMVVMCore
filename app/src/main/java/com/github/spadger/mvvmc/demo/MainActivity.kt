package com.github.spadger.mvvmc.demo

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.spadger.mvvmc.BaseBindingActivity
import com.github.spadger.mvvmc.demo.adapter.ContractListAdapter
import com.github.spadger.mvvmc.demo.databinding.ActivityMainBinding
import com.github.spadger.mvvmc.demo.model.ContractResponse
import com.github.spadger.mvvmc.demo.model.LoginResponse
import com.github.spadger.mvvmc.demo.vm.DemoViewModel
import com.github.spadger.mvvmc.ext.observeResult
import com.github.spadger.mvvmc.pager.PagingLoadState
import com.github.spadger.mvvmc.util.DataStoreHolder
import com.github.spadger.mvvmc.util.DataStoreKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

class MainActivity : BaseBindingActivity<DemoViewModel, ActivityMainBinding>() {

    private lateinit var adapter: ContractListAdapter

    override fun createViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        adapter = ContractListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    override fun setupObservers() {
        viewModel.contractPager.dataList.observe(this) { contracts ->
            adapter.submitList(contracts)
        }

        viewModel.contractPager.loadState.observe(this) { state ->
            when (state) {
                is PagingLoadState.LoadingFirst -> showLoading("加载中...")
                is PagingLoadState.LoadingMore -> adapter.showLoading(true, binding.recyclerView)
                is PagingLoadState.Refreshing -> showLoading("刷新中...")
                is PagingLoadState.Success -> {
                    hideLoading()
                    adapter.showLoading(false, binding.recyclerView)
                    binding.swipeRefreshLayout.isRefreshing = false
                    updateListInfo(state.page, state.totalCount, state.hasMore)
                }
                is PagingLoadState.Error -> {
                    hideLoading()
                    adapter.showLoading(false, binding.recyclerView)
                    binding.swipeRefreshLayout.isRefreshing = false
                    showError(state.message ?: "加载失败")
                }
                is PagingLoadState.Idle -> {}
            }
        }

        viewModel.loginBodyResult.observeResult(
            this,
            onSuccess = { handleLoginSuccess(it) },
            onError = { showError("登录失败: ${it.message}") },
            onLoading = { showLoading("登录中...") }
        )
    }

    override fun initListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshContractList()
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (username.isNotBlank() && password.isNotBlank()) {
                viewModel.login(username, getMD5Str(password))
            } else {
                showWarning("请输入用户名和密码")
            }
        }

        binding.btnLoadData.setOnClickListener {
            viewModel.loadContractList()
        }

        binding.btnDataStore.setOnClickListener {
            initDataStore()
        }

        adapter.setOnItemClickListener { contract ->
            showInfo("合同号: ${contract.Contract_Num}\n线路: ${contract.Line_Name}")
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == adapter.itemCount - 1 && viewModel.contractPager.hasMoreData) {
                    viewModel.loadMoreContracts()
                }
            }
        })
    }

    override fun initData() {
        viewModel.loadContractList()
    }

    private fun updateListInfo(page: Int, count: Int, hasMore: Boolean) {
        binding.tvListInfo.text = "共 $count 条数据，第 $page 页${if (hasMore) "，可加载更多" else "，已加载全部"}"
    }

    private fun handleLoginSuccess(response: LoginResponse) {
        showSuccess("登录成功！欢迎 ${response.User_Name}")
    }

    private fun initDataStore() {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreHolder.getInstance().putString(DataStoreKeys.USER_TOKEN, "test_token_123")
            DataStoreHolder.getInstance().putBoolean(DataStoreKeys.IS_LOGGED_IN, true)
            runOnUiThread { showSuccess("DataStore 测试完成") }
        }
    }
    override fun createViewModel(): DemoViewModel {
        return obtainViewModel()
    }

    fun getMD5Str(str: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(str.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }

            // Return upper case hex string
            return hexString.toString().uppercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}