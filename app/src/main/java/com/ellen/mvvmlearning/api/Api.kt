package com.ellen.mvvmlearning.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    /**
     * Class Single (from Rx)
     * - Observable의 특수한 형태
     * - Observable 클래스는 데이터를 무한하게 발행할 수 있지만, Single 클래스는 오직 1개의 데이터만 발행
     *      (결과가 유일한 서버 API를 호출할 때 유용하게 사용)
     * - 데이터 하나 발행과 동시에 종료
     */
    @GET("v1/util/shorturl")
    fun shorturl(@Query("url") url: String): Single<MyResponse>
}