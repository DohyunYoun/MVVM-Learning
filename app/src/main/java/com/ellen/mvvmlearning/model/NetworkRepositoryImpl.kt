package com.ellen.mvvmlearning.model

import com.ellen.mvvmlearning.api.Api
import io.reactivex.Single

//서비스 생성
class NetworkRepositoryImpl(private val api: Api): Repository {
    override fun getShortenUrl(url: String): Single<ShortenUrl> {
        return api.shorturl(url)
            .map { shortenUrlResponse ->
                shortenUrlResponse.result
            }
    }
}