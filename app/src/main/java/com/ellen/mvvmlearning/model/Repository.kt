package com.ellen.mvvmlearning.model

import io.reactivex.Single

interface Repository {
    /**
     * ViewModel에서 ShortenUrl을 observe 가능하게 만들어 단방향 디펜던시를 갖게 하기 위해 RxJava의 Single사용
     */
    fun getShortenUrl(url: String): Single<ShortenUrl>
}