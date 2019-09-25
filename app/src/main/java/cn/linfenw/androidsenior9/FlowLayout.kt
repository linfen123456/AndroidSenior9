package cn.linfenw.androidsenior9

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class FlowLayout(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获得它的父容器为它设置的测量模式和大小
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        //当前父容器的padding值
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddintTop = paddingTop
        val paddingBottom = paddingBottom

        //用于记录单行的动态宽和高
        var lineWidth = 0
        var lineHeight = 0
        //根据内容决定的动态宽和高
        var wrapContentWidth = 0
        var wrapContentHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue//子view是隐藏的跳过
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)//子view的measure
            val childLayoutParams = child.layoutParams as ViewGroup.MarginLayoutParams
            //获得子View + 外边框的宽和高
            val childWidth = (childLayoutParams.leftMargin + child.measuredWidth
                    + childLayoutParams.rightMargin)
            val childHeight = (childLayoutParams.topMargin + child.measuredWidth
                    + childLayoutParams.bottomMargin)
            //换行情况

            if (lineWidth + childWidth > widthSize - paddingLeft - paddingRight) {
                //更新最终想要的结果wrapContentWidth和wrapContentHeight的值，高要累加，宽取最宽
                wrapContentWidth = Math.max(wrapContentWidth, lineWidth)
                wrapContentHeight += lineHeight
                //重置 singleLineWidth 和 singleLineHeight
                lineHeight = 0
                lineWidth = 0
            }
            //更新本行的宽和高
            lineWidth += childWidth
            lineHeight = Math.max(lineHeight, childHeight)

            //最后一个，跟换行情况一样处理
            if (i == childCount - 1) {
                wrapContentWidth = Math.max(wrapContentWidth, lineWidth)
                wrapContentHeight += lineHeight
            }
        }
        //最终结果加上padding值
        wrapContentWidth += wrapContentWidth + (paddingLeft + paddingRight)
        wrapContentHeight += wrapContentHeight + (paddintTop + paddingBottom)

        //最宽和最高不能大于可容纳的宽和高
        if (wrapContentWidth > widthSize) {
            wrapContentWidth = widthSize
        }
        if (wrapContentHeight > heightSize) {
            wrapContentHeight = heightSize
        }

        //设置当前ViewGroup的wrap_Content的值
        setMeasuredDimension(
            if (widthMode == View.MeasureSpec.EXACTLY) widthSize else wrapContentWidth,
            if (heightMode == View.MeasureSpec.EXACTLY) heightSize else wrapContentHeight
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //当前父容器的Padding值
        val paddingleft = paddingLeft
        val paddingRight = paddingRight
        val paddTop = paddingTop
        //开始的坐标
        var x = 0
        var y = 0
        //用于记录单行的高
        var lineHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            //获得子View + 外边距的宽和高
            val childLayoutParams = child.layoutParams as ViewGroup.MarginLayoutParams
            val childWidth = (childLayoutParams.leftMargin + child.measuredWidth
                    + childLayoutParams.rightMargin)
            val childHeight = (childLayoutParams.topMargin + child.measuredHeight
                    + childLayoutParams.bottomMargin)
            //换行情况
            if (x + childWidth > width - paddingleft - paddingRight) {
                x = 0
                y += lineHeight
                lineHeight = 0
            }
            //子View的layout
            val childLeft = paddingleft + childLayoutParams.leftMargin + x
            val childTop = paddTop + childLayoutParams.topMargin + y
            val childRight = childLeft + child.measuredWidth
            val childBottom = childTop + child.measuredHeight
            child.layout(childLeft, childTop, childRight, childBottom)
            //更新开始左坐标
            x += childWidth
            //更新行高
            lineHeight = Math.max(lineHeight, childHeight)
        }

    }
}
