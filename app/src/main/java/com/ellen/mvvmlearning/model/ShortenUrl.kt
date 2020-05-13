package com.ellen.mvvmlearning.model

//응답 받는 데이터 클래스 생성
data class ShortenUrl(val hash: String,
                      val url: String,
                      val orgUrl: String)