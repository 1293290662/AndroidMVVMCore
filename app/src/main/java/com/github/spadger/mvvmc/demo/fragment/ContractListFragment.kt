package com.github.spadger.mvvmc.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.spadger.mvvmc.BaseBindingFragment
import com.github.spadger.mvvmc.demo.adapter.ContractListAdapter
import com.github.spadger.mvvmc.demo.databinding.FragmentContractListBinding
import com.github.spadger.mvvmc.demo.model.ContractResponse
import com.github.spadger.mvvmc.demo.vm.DemoViewModel
import com.github.spadger.mvvmc.pager.PagingLoadState

class ContractListFragment : BaseBindingFragment<DemoViewModel, FragmentContractListBinding>() {

    private lateinit var adapter: ContractListAdapter

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentContractListBinding {
        return FragmentContractListBinding.inflate(inflater, container, false)
    }

    override fun createViewModel(): DemoViewModel {
        return obtainViewModel()
    }

    override fun initView() {
        logD("initView called")
        
        // 初始化 RecyclerView
        adapter = ContractListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        // 设置下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.contractPager.refresh()
        }
    }

    override fun setupObservers() {
        logD("setupObservers called")

        // 观察数据变化
        viewModel.contractPager.dataList.observe(viewLifecycleOwner) { contracts ->
            adapter.submitList(contracts)
        }

        // 观察加载状态
        viewModel.contractPager.loadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PagingLoadState.LoadingFirst -> showLoading("加载中...")
                is PagingLoadState.LoadingMore -> adapter.showLoading(true, binding.recyclerView)
                is PagingLoadState.Refreshing -> binding.swipeRefreshLayout.isRefreshing = true
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
    }

    override fun initListener() {
        logD("initListener called")

        // 列表项点击事件
        adapter.setOnItemClickListener { contract ->
            showToast("点击了合同: ${contract.Contract_Num}")
            // 可以跳转到详情页
            // navigate(R.id.action_to_contract_detail, bundleOf("contract" to contract))
        }

        // 滚动加载更多
        binding.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                
                if (dy > 0 && lastVisibleItemPosition >= totalItemCount - 2 && viewModel.contractPager.canLoadMore()) {
                    viewModel.contractPager.loadMore()
                }
            }
        })
    }

    override fun initData() {
        logD("initData called")
        // 首次加载数据
        viewModel.contractPager.loadData()
    }

    fun showLoading(message: String) {
        // 显示加载对话框或进度条
        showToast(message)
    }

    override fun hideLoading() {
        // 隐藏加载对话框或进度条
    }

    private fun updateListInfo(page: Int, totalCount: Int, hasMore: Boolean) {
        binding.tvPageInfo.text = "第 $page 页，共 $totalCount 条数据"
        binding.tvHasMore.text = if (hasMore) "还有更多数据" else "已加载全部"
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        logD("Fragment resumed")
    }

    override fun onFragmentPause() {
        super.onFragmentPause()
        logD("Fragment paused")
    }

    companion object {
        fun newInstance(): ContractListFragment {
            return ContractListFragment().apply {
                arguments = Bundle().apply {
                    // 可以传递参数
                }
            }
        }
    }
}