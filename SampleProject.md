# MVVM 패턴을 적용한 토이프로젝트
[참고 1](https://terry-some.tistory.com/28)

[참고 2](https://github.com/iam1492/mvvmsample)
### Sample Application Architecture
- Koin - 의존성 주입
- Retrofit - Naver api를 호출하기 위한 Rest Api
- RxJava - Model에서 데이터를 노출하고, 이벤트를 발생시키기 위해 사용.(Model에 observe하는 주체는 viewModel)
- LiveData - ViewModel에서 View에 데이터를 노출하고 이벤트를 발생시키기 위해 사용(ViewModel에 observe하는 주체는 view)
- Databinding - ViewModel과 xml layout간의 데이터를 바인딩 하기 위해 사용
- Naver API(Shorten url_)
- Snackbar


1. app의 build.gradle 설정
```
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"

    //androidx
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.core:core-ktx:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    //lifecycle-extensions 지원중단
//    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "com.google.android.material:material:1.2.0-alpha06"

    //rxjava
    implementation 'io.reactivex.rxjava2:rxjava:2.1.9'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'

    //koin
    implementation 'org.koin:koin-android:1.0.2'

    //retrofit
    implementation "com.squareup.retrofit2:retrofit:2.3.0"
    implementation "com.squareup.retrofit2:converter-gson:2.3.0"
    implementation "com.squareup.retrofit2:adapter-rxjava2:2.3.0"
    implementation 'com.squareup.okhttp3:logging-interceptor:3.9.0'
 ```

2. API통신을 위한 준비

   1. 응답 받는 데이터 클래스 생성
   ```kotlin
   data class MyResponse(val message: String,
                         val result: ShortenUrl,
                         val code: String)
   ```

    ```kotlin
    data class ShortenUrl(val hash: String,
                          val url: String,
                          val orgUrl: String)
     ```

   2. 응답 객체를 활용한 API Interface 생성
   ```kotlin
    /**
     * Class Single (from Rx)
     * - Observable의 특수한 형태
     * - Observable 클래스는 데이터를 무한하게 발행할 수 있지만, Single 클래스는 오직 1개의 데이터만 발행
     *      (결과가 유일한 서버 API를 호출할 때 유용하게 사용)
     * - 데이터 하나 발행과 동시에 종료
     * 
     * ViewModel에서 ShortenUrl을 observe 가능하게 만들어 단방향 디펜던시를 갖게 하기 위해 RxJava의 Single사용
     */
    @GET("v1/util/shorturl")
    fun shorturl(@Query("url") url: String): Single<MyResponse>
    ```
   3. 서비스 생성
   ```kotlin
        class NetworkRepositoryImpl(private val api: Api): Repository {
            override fun getShortenUrl(url: String): Single<ShortenUrl> {
                return api.shorturl(url)
                    .map { shortenUrlResponse ->
                        shortenUrlResponse.result
                    }
            }
        }
   ```


3. 의존성 주입(Koin)
   1. Retrofit 모듈 구현
    ```kotlin
    val apiModules : Module = module {
        single{
            Retrofit.Builder()
                .client(OkHttpClient.Builder()
                    .addInterceptor(get(headerInterceptor))
                    .addInterceptor(get(loggingInterceptor))
                    .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(Api::class.java)
        }
        ...
        }
        val appModules = listOf(apiModules, shortenUrlModules)
    ```

    2. 베이스 모듈 구현
    ```kotlin
    val shortenUrlModules: Module = module {
        //API 통신
        factory {
            NetworkRepositoryImpl(get()) as Repository
        }
    
        //viewModel
        factory {
            ShortenUrlViewModelFactory(get())
        }
    }
    ```
   3. 의존성 주입
   ```kotlin
   class MyApplication:Application(){
        override fun onCreate() {
            super.onCreate()
            startKoin(this, appModules)
        }
    }
   ```
4. ViewModel 구현
```kotlin

open class BaseViewModel: ViewModel() {
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
}
```

```kotlin
class ShortenUrlViewModel(private val repository: Repository) : BaseViewModel() {

    /**
     * ViewModel내부에서는 Mutable한 데이터를 외부에서는 Immutable하게 사용하도록 제약을 주기위해 다음과 같이 LiveData프로퍼티를 노출
     * (=View에서 ViewModel의 데이터 상태를 변경하지 못함)
     */
    private val _shortenUrl = MutableLiveData<String>()
    //mutableLiveData를 immutable 하게 노출
    val shortenUrl: LiveData<String> get() = _shortenUrl
    
    /**
     * SingleLiveEvent
     * MutableLiveData를 상속받은 클래스로, 일회성 이벤트를 처리하기 위해 구현
     * 클릭 이벤트의 경우 lifecycle과 상관없이 사용자의 액션에 반응하고 그 이외의 경우에는 반응하면 안되기 때문에 사용.
     * (그냥 livedata는 화면이 전환되면 데이터를 보전하기 위해 view에 다시 notification을 줌)
     */
    private val _clickCopyToClipboard = SingleLiveEvent<String>()


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
    
}
```


5. View 구현
> 코드를 참고하되 특이사항으로 ViewModelProviders.of deprecated됨.
> ```kotlin
> //변경 전
> val shortenUrlViewModel = ViewModelProviders.of(this, shortenUrlViewModelFactory).get(
>        ShortenUrlViewModel::class.java)
> //변경 후
>  val shortenUrlViewModel =
>            ViewModelProvider(this, shortenUrlViewModelFactory).get(ShortenUrlViewModel::class.java)
> ```

