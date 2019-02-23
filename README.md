# DrawingView
This is an implementation of a view which you can use to draw anything you want on it. 

<a href="https://github.com/golovin47/gis-drawing-view">
<img align="center" src="https://github.com/golovin47/gis-drawing-view/blob/master/DrawingView.gif" width="50%" height="50%"/></a>

## How to install 
You can download it as an AAR [here](http://central.maven.org/maven2/com/github/golovin47/drawingview/drawingview/1.0.0/drawingview-1.0.0.aar).

You can use it as dependency in your gradle file:
```groovy
implementation 'com.github.golovin47.drawingview:drawingview:1.0.0'
```

If you're using maven:
```xml
<dependency>
  <groupId>com.github.golovin47.drawingview</groupId>
  <artifactId>drawingview</artifactId>
  <version>1.0.0</version>
  <type>aar</type>
</dependency>
```
## How to use
This is just a simple view, so you can add it to your layout.xml or create in code.

You can change paint color in code:
```kotlin
setPaintColor(R.color.black)
```
and
```kotlin
setPaintColor("#FF000000")
```
or add attribute in layout.xml:
```xml
app:paintColor="@android:color/black"
```

You can change line thickness in code:
```kotlin
setLineThickness(10f)
```
or add attribute in layout.xml:
```xml
app:lineThickness="10"
```

You can set background color in code:
```kotlin
setCanvasBackgroundColor(R.color.white)
```
and
```kotlin
setCanvasBackgroundColor("#FF000000")
```
or add attribute in layout.xml:
```xml
app:backgroundColor="@android:color/white"
```

You can undo your previously drawn path:
```kotlin
undo()
```

You can easily get bitmap of everything, DrawingView contains:
```kotlin
getBitmap()
```

## License
DrawingView is released under the MIT license. Visit https://opensource.org/licenses/MIT for details.
