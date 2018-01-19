package com.spacitron.reposlistapp.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

fun View.expand(animationListener: AnimationEndedListener? = null) {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    expand(animationListener, measuredHeight)
}

fun View.expand(animationListener: AnimationEndedListener?, targetHeight: Int) {
    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    visibility = View.VISIBLE
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            layoutParams.height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            animationListener?.animationStatus(AnimationEndedListener.AnimationStatus.REPEAT)
        }

        override fun onAnimationEnd(animation: Animation?) {
            animationListener?.animationStatus(AnimationEndedListener.AnimationStatus.COMPLETED)
        }

        override fun onAnimationStart(animation: Animation?) {
            animationListener?.animationStatus(AnimationEndedListener.AnimationStatus.START)
        }

    })

    // 1dp/ms
    a.duration = (targetHeight / context.resources.displayMetrics.density).toInt().toLong()
    startAnimation(a)
}


fun View.collapse() {

    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                visibility = View.GONE
            } else {
                layoutParams.height = measuredHeight - (measuredHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms
    a.duration = (measuredHeight / context.resources.displayMetrics.density).toInt().toLong()
    startAnimation(a)

}


interface AnimationEndedListener {
    enum class AnimationStatus {
        COMPLETED, REPEAT, START
    }

    fun animationStatus(status: AnimationStatus);
}
