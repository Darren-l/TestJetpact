# TestJetpact
 用于培训演示jetpact使用
####总结

##1. MVVM
   dataBangding：
    1. 布局必须要用layout。
    2. 使用databanding进行加载，返回的类是apt根据加载的xml名字自动生成的。

   java两种方式让数据对象拥有观察者功能：
    1. 类继承BaseObservable+注解让当前类具备观察者，注解用于控制通知方式。
    2. 类成员申明ObservableField让类成员具备观察者，而非类本身。
    一旦对象具备观察者，那么可以使用代码的方式addOnPropertyChangedCallback对其修改进行监听。

   Xml中data申明：
    apt会自动生成相应的监听data代码，data改变则改变xml中指定的值。


##2. liveData
   绑定lifecycler，自动控制内存释放。
   和BaseObservable（BO）类似，目的是让包裹的对象拥有观察者功能，抽象数据修改后的操作。
   

##3. viewModel
   绑定声明周期，自动控制内存释放。
   数据持有者，一般用于存储liveData，可自动存储activity的临时状态。

##区别
   这里需要注意liveData和BaseObservable(BO)区别，两者都具备观察者功能。
   BO对象可以根据标注的注解及notify接口，观测到BO对象本身成员的修改。而liveData仅仅能监听到绑定的对象是否有更换
   （对应postValue和value接口），无法监听到绑定对象内部成员的修改。

   我们通常可以将请求数据后返回的整个对象存进liveData中，而liveData本身交由viewModel持有。对象中具体关系到Ui的
   刷新的子成员，继承BaseObservable进行绑定。

   流程就变成了：数据请求成功后，postValue更新liveData绑定对象，在liveData观察者回调中绑定BO，在BO的观察者回调
   中刷新Ui。后续不管是刷新数据对象本身还是更换liveData数据源，相应绑定的Ui都会自动刷新。