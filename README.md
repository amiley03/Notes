# notes-app
An example of using MVVM Architecture, ViewDataBinding and Kotlin Coroutines


### Architecture
- LiveData used to communicate between View and ViewModel
- Kotlin Flow used in data repository layer for observing data changes
- Couroutines used with ViewModelScope to make async calls and handle clean up
- UseCase and Mapper used for separating business logic


#### Tests:
- Test coverage for view model, repo and data store layers
- Basic UI smoke test using espresso
