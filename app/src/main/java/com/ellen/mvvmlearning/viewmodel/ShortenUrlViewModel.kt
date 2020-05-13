package com.ellen.mvvmlearning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellen.mvvmlearning.model.Repository
import com.ellen.mvvmlearning.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ShortenUrlViewModel(private val repository: Repository) : DisposableViewModel() {

    /**
     * ViewModel내부에서는 Mutable한 데이터를 외부에서는 Immutable하게 사용하도록 제약을 주기위해 다음과 같이 LiveData프로퍼티를 노출
     * (=View에서 ViewModel의 데이터 상태를 변경하지 못함)
     */

    /**
     * SingleLiveEvent
     * MutableLiveData를 상속받은 클래스로, 일회성 이벤트를 처리하기 위해 구현
     * 클릭 이벤트의 경우 lifecycle과 상관없이 사용자의 액션에 반응하고 그 이외의 경우에는 반응하면 안되기 때문에 사용.
     * (그냥 livedata는 화면이 전환되면 데이터를 보전하기 위해 view에 다시 notification을 줌)
     */
    private val _shortenUrl = MutableLiveData<String>()
    private val _error = MutableLiveData<String>()
    private val _clickCopyToClipboard = SingleLiveEvent<String>()
    private val _clickOpenWeb = SingleLiveEvent<String>()
    private val _clickConvert = SingleLiveEvent<Any>()

    val showResult = MutableLiveData<Boolean>()

    //mutableLiveData를 immutable 하게 노출
    val shortenUrl: LiveData<String> get() = _shortenUrl
    val error: LiveData<String> get() = _error
    val clickCopyToClipboard: LiveData<String> get() = _clickCopyToClipboard
    val clickOpenWeb: LiveData<String> get() = _clickOpenWeb
    val clickConvert: LiveData<Any> get() = _clickConvert

//    fun getUrlValidator(errorMessage: String): METValidator {
//        return RegexpValidator(errorMessage, Patterns.WEB_URL.pattern())
//    }

    fun getShortenUrl(url: String) {
        addDisposable(repository.getShortenUrl(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                showResult.value = true
                _shortenUrl.value = it.url
            }, {
                _error.value = it.message
            }))
    }

    fun clickCopyToClipboard(){
        _clickCopyToClipboard.value = _shortenUrl.value
    }

    fun clickConvert() {
        _clickConvert.call()
    }



    fun clickOpenWeb() {
        _clickOpenWeb.value = _shortenUrl.value
    }
}