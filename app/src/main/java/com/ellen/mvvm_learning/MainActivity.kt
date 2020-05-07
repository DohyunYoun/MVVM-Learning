package com.ellen.mvvm_learning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ellen.mvvm_learning.base.BaseActivity
import com.ellen.mvvm_learning.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    //layout의 binding 메소드를 사용하여 layout 파일을 바인딩한 객체
    private val binding by binding<ActivityMainBinding>(R.layout.activity_main)
    lateinit var viewModel: MainViewModel
    val TAG: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //AAC ViewModel 생성
        val androidViewModelFactory = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
        viewModel = ViewModelProvider(this, androidViewModelFactory)
            .get(MainViewModel::class.java)

        //LiveData 에 콜백 등록
        // 첫번째 인자: UI. 해당 인자로 어떤 UI Thread를 사용할지 결정한다.
        // 두번째 인자: Observe 콜백.
        viewModel.exampleData.observe(this, Observer {
            Log.v(TAG, ">>> ${it.value}")
        })

        viewModel.requestData()


    }
}
