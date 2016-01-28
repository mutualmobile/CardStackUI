# CardStack
An iOS Healthbook-like highly customisable stack of cards implementation for Android.

<table>
  <tr>
    <td><h4>Init Animation</h4></td>
    <td><h4>Card Click</h4></td>
    <td><h4>Basic Drag</h4></td>
    <td><h4>Parallax Drag</h4></td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/mutualmobile/CardStackUI/development/images/showcase/InitAnimation.gif?token=ABCt5p-LPvIgv6lQmwPhcs01L7i6s1RNks5WsjhMwA%3D%3D"></td>
    <td><img src="https://raw.githubusercontent.com/mutualmobile/CardStackUI/development/images/showcase/Card%20Click.gif?token=ABCt5vutFKLG17UfTjwuNP7Wch6_e0a5ks5Wsi1HwA%3D%3D"></td>
    <td><img src="https://raw.githubusercontent.com/mutualmobile/CardStackUI/development/images/showcase/BasicDrag.gif?token=ABCt5uiVA9iKifP5YX9ta6nI_t0KsxVMks5Wsi0GwA%3D%3D"></td>
    <td><img src="https://raw.githubusercontent.com/mutualmobile/CardStackUI/development/images/showcase/PrallaxDrag.gif?token=ABCt5t4-uywQ20Kw489R1_5AQ7GXdx07ks5Wsi2QwA%3D%3D"></td>
  </tr>
</table>

Complete Demo - https://www.youtube.com/watch?v=8VjEa_u3LkM

### Gradle
```
dependencies {
    ...
    compile 'com.mutualmobile:cardstackui:0.1'
}
```

Usage
-----
```xml
<com.mutualmobile.cardstack.CardStackLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:card_stack="http://schemas.android.com/apk/res-auto"
    android:id="@+id/cardStack"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    card_stack:card_gap="60dp"
    card_stack:card_gap_bottom="5dp"
    card_stack:parallax_enabled="true"
    card_stack:parallax_scale="-6"
    card_stack:showInitAnimation="true" />
```

License
-------

    Copyright 2015 - 2016 Mutual Mobile

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
