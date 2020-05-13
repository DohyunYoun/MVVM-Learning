package com.ellen.mvvmlearning.api

import com.ellen.mvvmlearning.model.ShortenUrl


data class MyResponse(val message: String,
                      val result: ShortenUrl,
                      val code: String)