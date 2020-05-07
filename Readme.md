# MVVM 패턴에 대한 이해 및 적용

### Model
**View 에 표시할 데이터**
###
### View
**UI**

어디까지 view에서 처리해야할까?(유저 인터렉션과 라이프사키을을 생각하면 어렵다.)
```kotlin
override fun onResume() {
  super.onResume()
  val call = myApp.getApi().getTodoList(currentDate)
  call.enqueue(object : Callback<TodoResponse>) {
    override fun onResponse(call: Call<TodoResponse>, response: Response<TodoResponse>) {
      updateView(response.body())
    }
    override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
        // TODO: Handle error.
    }
  }
}
```

서버와의 통신은 ViewModel에서 처리하는게 좋겠지만 View의 라이프사이클 상태를 알 수 없다.
그렇다면?

```kotlin
override fun onResume() {
  super.onResume()
  //viewmodel에 알려준다
  viewModel.onResume()
}
```
###
### ViewModel
**View와 관련된 모든 비즈니스 로직 & Model을 View에 뿌리기 좋게 바꿔줌**

* 중요 한점은 ViewModel은 View에 명령하면 안된다.
    > 라이브러리가 아닌 DataBinding 패턴을 이용하여 해결한다.

`DataBinding은 어떠한 subject를 두고 다른 객체들이 이를 구독하게 한다. subject가 업데이트되면, 다른 객체들이 자동으로 바뀌게 되는 것이다.`
> 구현방법
> 1. Observer 패턴을 사용하여 직접 구현
> 2. 안드로이드에서 제공하는 ViewModel 사용
> 3. RxJava2 사용
>```kotlin
> class TodoViewModel {
>  fun getTodoList(): Single<List<TodoItem>> {
>    return myApp.getApi().getTodoList()
>      .subsribeOn(Schedulers.io())
>  }
>```

###
### LiveData Model
(안드로이드에서 제공하는 ViewModel)

<img src="http://labs.brandi.co.kr///assets/20200217/02.png"/>

* LiveData?
  * LifeCycle을 알고 있는 DataType
  * 데이터의 변경이 일어날 때 Observer 패턴으로 콜백을 받지만, LifeCycle을 알기 때문에 필요하지 않을땐, 콜백을 수행하지 않는다.
* MutableLiveData?
     * 변경할 수 있는 LiveData
     * 일반적으로 LiveData는 오로지 데이터의 변경값만을 소비하는데 반해
     * MutableLiveData는 데이터를 UI Thread와 Background Thread에서 선택적으로 바꿀 수 있다.

###
1. View가 ViewModel을 구독: 서버에서 받아온 데이터로 뷰를 업데이트 하는 경우
2. View가 ViewModel을 트리거: 사용자의 입력 이벤트를 ViewModel에 전달하는 경우
3. ViewModel이 View를 트리거: ViewModel이 처리한 결과로 View가 업데이트 되어야 하는 경우
    > 로그인 버튼을 눌러 인증 성공하면 화면 이동

*https://poqw.github.io/about_mvvm/ 하단부분인데 아직 이해못함..*

##
참고(https://poqw.github.io/about_mvvm/)

참고(https://medium.com/harrythegreat/jetpack-android-livedata-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0-ed49a6f17de3)
