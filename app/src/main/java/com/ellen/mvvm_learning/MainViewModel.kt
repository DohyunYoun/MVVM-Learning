package com.ellen.mvvm_learning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ellen.mvvm_learning.model.ExampleData

class MainViewModel : ViewModel() {
    val TAG = this.javaClass.simpleName

    //변경할 수 있는 LiveData
    private val _exampleLiveData: MutableLiveData<ExampleData> = MutableLiveData()

    //Observe 를 이용한 데이터 수신을 위한 LiveData
    //viewModel에서만 _exampleLiveData를 변경할 수 있기 때문에 보안에 용이
    val exampleData: LiveData<ExampleData>
        get() = _exampleLiveData

    /**
     * LiveData 콜백 실행 방법
     * UI Thread = Main Thread 에서 실행됨
     * post.setValue(post)
     *
     * Background Thread 에서 처리됨.
     * post.postValue(post)
     */
    fun requestData() {
        //송신 데이터 생성
        val exampleData = ExampleData().apply {
            value = 10
        }

        _exampleLiveData.value = exampleData
    }

    //ViewModel를 가지고 있는 Activity 혹은 Fragment 가 종료될 경우 ViewModel 이 정리된다.
    override fun onCleared() {
        super.onCleared()
        Log.v(TAG, ">>> onCleared")
    }
}