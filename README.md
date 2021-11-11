# RetrofitFlowCallAdapter

Retrofit + Flow 最佳实践

- 支持异步、同步

- 支持body、response

# 使用

添加 FlowCallAdapterFactory 异步方式
```java
val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addCallAdapterFactory(FlowCallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
```

添加 FlowCallAdapterFactory 同步方式

```java
val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
```

定义接口

```java
interface ApiService {

    @GET("users/{user}/repos")
    fun listReposWithBody(@Path("user") user: String): Flow<List<Repo>>

    @GET("users/{user}/repos")
    fun listReposWithResponse(@Path("user") user: String): Flow<Response<List<Repo>>>
}
```

发起请求

```java
lifecycleScope.launch {
     service.listReposWithResponse("zyj1609wz")
            .flowOn(Dispatchers.IO)
             .catch {
                 it.printStackTrace()
                 Log.d("my--", "error:${it.message} ${Thread.currentThread().name}")
             }
             .collect {
                 val list = it.body()
                 val item = list?.get(0)
                 item?.let {
                     Log.d("my--", "onResponse:id:${item.id} name:${item.name}")
               }
           }
        }
```

# Downlaod

**Step 1.** Add the JitPack repository to your build file:

```Gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency:

```java
implementation 'com.github.zyj1609wz:RetrofitFlowCallAdapter:1.0.0'
```
