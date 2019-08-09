# CardStack
An iOS Passbook-like highly customisable stack of cards implementation for Android.

[![Build Status](https://travis-ci.org/mutualmobile/CardStackUI.svg?branch=master)](https://travis-ci.org/mutualmobile/CardStackUI) [![Version](https://api.bintray.com/packages/mutualmobile/Android/card-stack-ui/images/download.svg)](https://bintray.com/mutualmobile/Android/card-stack-ui)
 [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CardStackUI-green.svg?style=true)](https://android-arsenal.com/details/1/3096)

<table>
  <tr>
    <td><h4>Init Animation</h4></td>
    <td><h4>Card Click</h4></td>
  </tr>
  <tr>
    <td><img src="https://github.com/Vivek65/CardStackUI/blob/master/images/showcase/InitAnimation0.0.6.gif"></td>
    <td><img src="https://github.com/Vivek65/CardStackUI/blob/master/images/showcase/CardClick0.0.6.gif"></td>
  </tr>
</table>

Complete Demo - https://www.youtube.com/watch?v=8VjEa_u3LkM

Sample app APK - https://github.com/mutualmobile/CardStackUI/releases

### Gradle
```
dependencies {
    ...
    compile 'com.mutualmobile.android:cardstack:0.6.0'
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

Known Issues
-------

```parallax_enabled``` and ```parallax_scale``` are not working currently. Also the drag to move cards also is not working.

License
-------

    Copyright 2015 - 2019 Mutual Mobile

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
