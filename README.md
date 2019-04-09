### Android原生项目集成Flutter

#### 创建Flutter module

假定存在的Android原生项目目录是`some/path/MyApp`，我们在MyApp的所在的目录下创建一个`flutter module`。
```
$ cd some/path/
$ flutter create -t module my_flutter
```
上面的命令会创建一个`flutter module`并且会在`my_flutter`目录下生成一个`./android`的隐藏子目录，
这个目录的作用就是将创建好的`flutter module`包装成一个 `android library`。

#### 宿主app的要求
在连接flutter module和宿主app之前，你需要确保在宿主app的`build.gradle`文件中声明如下

```
android {
    
    //...

    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
    
}
```
#### 让宿主app依赖flutter module

在宿主app的`settings.gradle`中把flutter module包含进来作为子项目
```
// MyApp/settings.gradle
include ':app'                                     // assumed existing content
setBinding(new Binding([gradle: this]))                                 // new
evaluate(new File(                                                      // new
  settingsDir.parentFile,                                               // new
  'my_flutter/.android/include_flutter.groovy'                          // new
))                                                                      // new
```

然后在`build.gradle`文件中声明如下
```
// MyApp/app/build.gradle

dependencies {
  implementation project(':flutter')
  //...
}
```
#### 使用 flutter module
使用flutter module的Java api 来添加 Flutter views 到宿主app中。
可以直接使用`Flutter.createView`实现。
```
// MyApp/app/src/main/java/some/package/MainActivity.java
fab.setOnClickListener(new View.OnClickListener() {
  @Override
  public void onClick(View view) {
    View flutterView = Flutter.createView(
      MainActivity.this,
      getLifecycle(),
      "route1"
    );
    FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(600, 800);
    layout.leftMargin = 100;
    layout.topMargin = 200;
    addContentView(flutterView, layout);
  }
});
```

也可以创建一个FlutterFragment来自己处理生命周期
```
// MyApp/app/src/main/java/some/package/SomeActivity.java
fab.setOnClickListener(new View.OnClickListener() {
  @Override
  public void onClick(View view) {
    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
    tx.replace(R.id.someContainer, Flutter.createFragment("route1"));
    tx.commit();
  }
});

```

flutter module的入口都是`lib/main.dart`。默认创建的widget是`MyApp`。
```
import 'package:flutter/material.dart';

///lib/main.dart

void main() => runApp(MyApp());

Widget _widgetForRoute(String route) {
  switch (route) {
    case 'route1':
      return Route1App();
    default:
      return Center(
        child: Text('Unknown route: $route', textDirection: TextDirection.ltr),
      );
  }
}

class MyApp extends StatelessWidget{
   ///...
}

```
在上面的代码中，我们传递了一个提供的路径字符串`route1`来告诉Dart code我们想要显示哪个widget。我们需要修改
flutter module的`lib/main.dart`文件，根据提供的路径字符串（可用作window.defaultRouteName），
来决定要创建哪个widget并传递给runApp。

举个简单的例子

1. 新建route1.dart

```
import 'package:flutter/material.dart';

///
/// Created by dumingwei on 2019/4/9.
/// Desc:  rout1
///

class Route1App extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Route1(),
    );
  }
}

class Route1 extends StatefulWidget {
  String text;

  Route1({Key key, this.text}) : super(key: key);

  @override
  State createState() {
    return Route1State();
  }
}

class Route1State extends State<Route1> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Center(
          child: Text(
            'Hello world ,i am route1',
          ),
        ),
      ),
    );
  }
}

```

2. 修改一下flutter module的`main.dart`

```
import 'dart:ui';

import 'package:flutter/material.dart';

import 'route1.dart';

void main() => runApp(_widgetForRoute(window.defaultRouteName));

Widget _widgetForRoute(String route) {
  switch (route) {
    case 'route1'://如果是route1，则返回Route1App
      return Route1App();
    default:
      return Center(
        child: Text('Unknown route: $route', textDirection: TextDirection.ltr),
      );
  }
}

```
重新运行如下

#### 热启动/调试Dart代码

完整的IDE集成以支持使用混合应用程序的Flutter / Dart代码的工作正在进行中。
flutter命令行和`tDart Observatory web user interface`已经提供了一些基本功能。

连接真机或者模拟器。然后使Flutter CLI工具监听您的应用程序。

切换到flutter module的目录下，在命令行输入下面的命令
```
$ cd some/path/my_flutter
$ flutter attach
Waiting for a connection from Flutter on LLD AL20...

```
然后使用debug模式启动宿主app，然后导航到使用flutter的地方。然后回到命令行，可以看到类似下面输出的信息。
```
Done.
Syncing files to device LLD AL20...                              1,752ms

🔥  To hot reload changes while running, press "r". To hot restart (and rebuild state), press "R".
An Observatory debugger and profiler on LLD AL20 is available at: http://127.0.0.1:54043/
For a more detailed help message, press "h". To detach, press "d"; to quit, press "q".

```

现在你可以修改flutter中的代码，然后在命令行里输入`r`来进行热更新。你也可以黏贴上面输出信息中的URL到浏览器中来使用
`Dart Observatory`来设置断点，分析内存保留以及其他调试任务。