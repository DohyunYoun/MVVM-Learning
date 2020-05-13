package com.ellen.mvvmlearning.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

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
open class DisposableViewModel: ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}