package com.github.spadger.mvvmc.demo.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.spadger.mvvmc.BaseBindingFragment
import com.github.spadger.mvvmc.demo.adapter.ContractListAdapter
import com.github.spadger.mvvmc.demo.databinding.FragmentContractListBinding
import com.github.spadger.mvvmc.demo.vm.DemoViewModel
import com.github.spadger.mvvmc.pager.observeData
import com.github.spadger.mvvmc.pager.observeState
import com.github.spadger.mvvmc.pager.setupRecyclerViewPaging

class ContractListFragment : BaseBindingFragment<DemoViewModel, FragmentContractListBinding>() {

    private lateinit var adapter: ContractListAdapter

    override fun initView() {
        adapter = ContractListAdapter()
        mBinding.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mBinding.recyclerView.adapter = adapter

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mVM.contractPager.refresh()
        }
    }

    override fun initObserve() {
        mVM.contractPager.observeData(viewLifecycleOwner) { adapter.submitList(it) }

        mVM.contractPager.observeState(
            owner = viewLifecycleOwner,
            onLoading = { showLoading("加载中...") },
            onLoadingMore = { adapter.showLoading(true, mBinding.recyclerView) },
            onRefreshing = { mBinding.swipeRefreshLayout.isRefreshing = true },
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
    }

    override fun initData() {
        mVM.contractPager.loadData()
    }

    override fun initListener() {
        adapter.setOnItemClickListener { contract ->
            showToast("点击了合同: ${contract.Contract_Num}")
        }

        mVM.contractPager.setupRecyclerViewPaging(mBinding.recyclerView)
    }

    override fun hideLoading() {
    }

    private fun updateListInfo(page: Int, totalCount: Int, hasMore: Boolean) {
        mBinding.tvPageInfo.text = "第 $page 页，共 $totalCount 条数据"
        mBinding.tvHasMore.text = if (hasMore) "还有更多数据" else "已加载全部"
    }

    companion object {
        fun newInstance(): ContractListFragment {
            return ContractListFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}