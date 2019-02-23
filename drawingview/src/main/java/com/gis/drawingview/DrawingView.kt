package com.gis.drawingview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent.*
import android.view.View
import android.view.View.BaseSavedState
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.android.parcel.Parcelize

class DrawingView : View {

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    applyXmlAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    applyXmlAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
      super(context, attrs, defStyleAttr, defStyleRes) {
    applyXmlAttrs(context, attrs)
  }

  private val DEFAULT_THICKNESS = 10f

  private var canvasBackgroundColor: Int = Color.WHITE
  private var drawnPaths: ArrayList<DrawnPath> = arrayListOf()
  private val drawingPaint = Paint().apply {
    style = Paint.Style.STROKE
    strokeJoin = Paint.Join.ROUND
    strokeCap = Paint.Cap.ROUND
    pathEffect = CornerPathEffect(100f)
    isDither = true
    isAntiAlias = true
  }

  init {
    initTouchListener()
  }

  private fun applyXmlAttrs(context: Context, attrs: AttributeSet?) {
    val attrsArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DrawingView, 0, 0)
    drawingPaint.color = attrsArray.getColor(R.styleable.DrawingView_paintColor, Color.BLACK)
    drawingPaint.strokeWidth = attrsArray.getFloat(R.styleable.DrawingView_lineThickness, DEFAULT_THICKNESS)
    canvasBackgroundColor = attrsArray.getColor(R.styleable.DrawingView_backgroundColor, Color.WHITE)
    attrsArray.recycle()
  }

  private fun initTouchListener() {
    setOnTouchListener { v, event ->
      when (event.action) {
        ACTION_DOWN -> {
          addPathToList(event.x, event.y, event.x, event.y)
        }

        ACTION_MOVE, ACTION_UP, ACTION_CANCEL -> {
          val previousPath = drawnPaths.last().fractionsList.last()
          changePathInList(previousPath.endX, previousPath.endY, event.x, event.y)
        }
      }
      return@setOnTouchListener true
    }
  }

  private fun addPathToList(startX: Float, startY: Float, endX: Float, endY: Float) {
    drawnPaths.add(
      DrawnPath(
        arrayListOf(
          DrawnPathFraction(startX, startY, endX, endY, drawingPaint.color, drawingPaint.strokeWidth)
        )
      )
    )
    invalidate()
  }

  private fun changePathInList(startX: Float, startY: Float, endX: Float, endY: Float) {
    drawnPaths.last().fractionsList.add(
      DrawnPathFraction(startX, startY, endX, endY, drawingPaint.color, drawingPaint.strokeWidth)
    )
    invalidate()
  }

  override fun onSaveInstanceState(): Parcelable? {
    val superState = super.onSaveInstanceState()
    if (superState == null) {
      return superState
    }

    val savedState = DrawingViewState(superState)
    savedState.drawnPaths = drawnPaths
    savedState.savedBackgroundColor = canvasBackgroundColor
    savedState.savedPaintColor = drawingPaint.color
    savedState.savedLineThickness = drawingPaint.strokeWidth
    return savedState
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    if (state is DrawingViewState) {
      super.onRestoreInstanceState(state.superState)
      drawnPaths = state.drawnPaths
      canvasBackgroundColor = state.savedBackgroundColor
      drawingPaint.color = state.savedPaintColor
      drawingPaint.strokeWidth = state.savedLineThickness
    } else
      super.onRestoreInstanceState(state)
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    drawBackgroundColor(canvas)
    drawPaths(canvas)
  }

  private fun drawBackgroundColor(canvas: Canvas?) {
    canvas?.drawColor(canvasBackgroundColor)
  }

  private fun drawPaths(canvas: Canvas?) {
    for (path in drawnPaths) {
      val pathToDraw = Path()
      pathToDraw.setLastPoint(path.fractionsList[0].startX, path.fractionsList[0].startY)
      for (fraction in path.fractionsList) {
        pathToDraw.lineTo(fraction.endX, fraction.endY)
      }

      drawingPaint.color = path.fractionsList[0].paintColor
      drawingPaint.strokeWidth = path.fractionsList[0].lineThickness
      canvas?.drawPath(pathToDraw, drawingPaint)
    }
  }

  /**
   *  Set drawing color using color resource id
   *  @param colorResId Resource ID of a color
   *
   *  Example: R.color.white
   */

  fun setPaintColor(@ColorRes colorResId: Int) {
    drawingPaint.color = ContextCompat.getColor(context, colorResId)
  }

  /**
   *  Set drawing color using color String
   *  @param colorString String representation of a color
   *
   *  Example: #FFFFFFFF
   */

  fun setPaintColor(colorString: String) {
    drawingPaint.color = Color.parseColor(colorString)
  }

  /**
   * Set the thickness of drawing lines.
   * Pass 0 if you want 1px thickness.
   *
   * @param thickness set the drawing lines thickness.
   */

  fun setLineThickness(thickness: Float) {
    drawingPaint.strokeWidth = thickness
  }

  /**
   *  Set background color for canvas using color resource id
   *  @param colorResId Resource ID of a color
   *
   *  Example: R.color.white
   */

  fun setCanvasBackgroundColor(@ColorRes colorResId: Int) {
    canvasBackgroundColor = ContextCompat.getColor(context, colorResId)
  }

  /**
   *  Set background color for canvas using color String
   *  @param colorString String representation of a color
   *
   *  Example: #FFFFFFFF
   */

  fun setCanvasBackgroundColor(colorString: String) {
    canvasBackgroundColor = Color.parseColor(colorString)
  }

  /**
   *  Cancel your previous drawn path
   */

  fun undo() {
    if (drawnPaths.size > 0) {
      drawnPaths.removeAt(drawnPaths.size - 1)
      invalidate()
    }
  }

  /**
   * Get bitmap of everything that was drawn on this view
   */

  fun getBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
  }
}


private class DrawingViewState : BaseSavedState {
  var drawnPaths: ArrayList<DrawnPath> = arrayListOf()
  var savedBackgroundColor: Int = 0
  var savedPaintColor: Int = 0
  var savedLineThickness: Float = 0f

  constructor(source: Parcel) : super(source)

  @RequiresApi(Build.VERSION_CODES.N)
  constructor(source: Parcel, loader: ClassLoader) : super(source, loader) {
    drawnPaths = source.readArrayList(loader) as ArrayList<DrawnPath>
    savedBackgroundColor = source.readInt()
    savedPaintColor = source.readInt()
    savedLineThickness = source.readFloat()
  }

  constructor(superState: Parcelable) : super(superState)

  companion object {
    @JvmField
    val CREATOR = object : Parcelable.Creator<DrawingViewState> {

      override fun createFromParcel(source: Parcel): DrawingViewState = DrawingViewState(source)

      override fun newArray(size: Int): Array<DrawingViewState?> = arrayOfNulls(size)
    }
  }

  override fun writeToParcel(out: Parcel, flags: Int) {
    super.writeToParcel(out, flags)
    out.writeList(drawnPaths)
    out.writeInt(savedBackgroundColor)
    out.writeInt(savedPaintColor)
    out.writeFloat(savedLineThickness)
  }
}


@Parcelize
private data class DrawnPathFraction(
  var startX: Float,
  var startY: Float,
  var endX: Float,
  var endY: Float,
  var paintColor: Int,
  var lineThickness: Float
) : Parcelable


@Parcelize
private data class DrawnPath(val fractionsList: MutableList<DrawnPathFraction>) : Parcelable