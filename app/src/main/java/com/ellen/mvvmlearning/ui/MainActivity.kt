package com.ellen.mvvmlearning.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ellen.mvvmlearning.R
import com.ellen.mvvmlearning.base.BaseActivity
import com.ellen.mvvmlearning.databinding.ActivityMainBinding
import com.ellen.mvvmlearning.viewmodel.ShortenUrlViewModel
import com.ellen.mvvmlearning.viewmodel.ShortenUrlViewModelFactory
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity<ActivityMainBinding, ShortenUrlViewModel>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    /**
     * 기본 :
     * ViewModelProvider를 통해 viewModel 클래스 생성
     * ShortenUrlViewModel이 생성자로 Repository를 받고 있기 때문에, 별도 팩토리 클래스 생성
     *  ViewModelProviders.of deprecated
        private val shortenUrlViewModelFactory: ShortenUrlViewModelFactory by inject()
        val shortenUrlViewModel = ViewModelProviders.of(this, shortenUrlViewModelFactory).get(
        ShortenUrlViewModel::class.java)

        val shortenUrlViewModel =
             ViewModelProvider(this, shortenUrlViewModelFactory).get(ShortenUrlViewModel::class.java)

     * koin의 viewModel ext 사용
     */

    override val viewModel: ShortenUrlViewModel by viewModel()


    //LiveData 에 콜백 등록
    // 첫번째 인자: UI. 해당 인자로 어떤 UI Thread를 사용할지 결정한다.
    // 두번째 인자: Observe 콜백.
//        viewModel.exampleData.observe(this, Observer {
//            Log.v(TAG, ">>> ${it.value}")
//        })



    override fun init() {
//        super.onCreate(savedInstanceState)
        /**
         * 각각 clickConvert, error등은 observe 하고 있으나 clickConvert내부에 getShortenUrl은 observe 하고있지 않다.
         */

        viewModel.clickConvert.observe(this, Observer {
            viewModel.getShortenUrl(viewDataBinding.urlEditText.text.toString())
        })

        viewModel.error.observe(this, Observer<String> { t ->
            Toast.makeText(this@MainActivity, t, Toast.LENGTH_LONG).show()
        })

        viewModel.clickCopyToClipboard.observe(this, Observer { clipUrl ->
//            copyToClipBoard(clipUrl) {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.clipboard_copied, clipUrl),
                Toast.LENGTH_LONG
            ).show()
//            }
        })

        viewModel.clickOpenWeb.observe(this, Observer { clipUrl ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(clipUrl)))
        })

//        viewDataBinding.urlEditText.addValidator(shortenUrlViewModel.getUrlValidator(getString(R.string.error_validate_email)))

        /**
         * Android Databinding을 통해 xml layout에 viewModel을 바인딩 해주고 있다.
         * setLifeCycleOwner로 현재 뷰(this)를 설정해 주고 있어
         * viewModel에서 value를 변경하는 순간 UI가 업데이트 되기 때문에 getShortenUrl을 observe할 필요가 없다.
         */
        viewDataBinding.shortenUrlViewModel = viewModel
        viewDataBinding.setLifecycleOwner(this)
    }
}
