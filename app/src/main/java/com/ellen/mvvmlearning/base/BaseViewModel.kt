package com.ellen.mvvmlearning.com.ellen.mvvmlearning.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.ellen.mvvmlearning.utils.SnackbarMessage
import com.ellen.mvvmlearning.utils.SnackbarMessageString
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BaseViewModel : ViewModel() {

    // 일회성 이벤트를 만들어 내는 라이브 이벤트
    // 뷰는 이러한 이벤트를 바인딩하고 있다가, 적절한 상황이 되면 액티비티를 호출하거나 스낵바를 만듬
    private val snackbarMessage = SnackbarMessage()
    private val snackbarMessageString = SnackbarMessageString()

    /**
     * RxJava 의 observing을 위한 부분.
     * addDisposable을 이용하여 추가하기만 하면 된다
     */
    /**
     * AAC의 ViewModel의 구현체
     * ShortenUrlViewModel의 상위클래스
     *
     * ViewModel은 Model(Repository)이 제공하는 데이터에 subscribe를 하게 되는데,
     * ViewModel이 클리어 되는 시점에 RxJava의 disposable또한 함게 클리어되는 것을 보장해 주기 위한 베이스 클래스
     *
     * (RxJava는 LiveData와 다르게, LifeCycle을 알지 못하기 떄문에
     * ViewModel의 onCleared()가 호출되면 명시적으로 dispose 해주어야 메모리릭을 방지할 수 있다.)
     */
    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    /**
     * 스낵바를 보여주고 싶으면 viewModel 에서 이 함수를 호출
     */
    fun showSnackbar(stringResourceId:Int) {
        snackbarMessage.value = stringResourceId
    }
    fun showSnackbar(str:String){
        snackbarMessageString.value = str
    }

    /**
     * BaseActivity 에서 쓰는 함수
     */
    fun observeSnackbarMessage(lifeCycleOwner: LifecycleOwner, ob:(Int) -> Unit){
        snackbarMessage.observe(lifeCycleOwner, ob)
    }
    fun observeSnackbarMessageStr(lifeCycleOwner: LifecycleOwner, ob:(String) -> Unit) {
        snackbarMessageString.observe(lifeCycleOwner, ob)
    }

}