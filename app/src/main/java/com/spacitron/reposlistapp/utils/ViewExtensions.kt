package com.spacitron.reposlistapp.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation

enum class AnimationStatus {
    COMPLETED, REPEAT, START
}

fun View.expand(animationListener: ((status: AnimationStatus)->Unit)? = null) {
    measure(layoutParams.width, layoutParams.height)
    expand(measuredHeight, animationListener)
}


fun View.expand(targetHeight: Int, animationListener: ((status: AnimationStatus)->Unit)?) {
    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 0
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

    animationListener?.let { handleAnimationListener(a, it) }

    // 1dp/ms
    a.duration = (targetHeight / context.resources.displayMetrics.density).toInt().toLong()
    startAnimation(a)
}


fun View.collapse(animationListener: ((status: AnimationStatus)->Unit)? = null) {

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
    animationListener?.let { handleAnimationListener(a, it) }

    // 1dp/ms
    a.duration = (measuredHeight / context.resources.displayMetrics.density).toInt().toLong()
    startAnimation(a)
}


private fun handleAnimationListener(a: Animation, animationListener: ((status: AnimationStatus)->Unit)){
    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            animationListener(AnimationStatus.REPEAT)
        }

        override fun onAnimationEnd(animation: Animation?) {
            animationListener(AnimationStatus.COMPLETED)
        }

        override fun onAnimationStart(animation: Animation?) {
            animationListener(AnimationStatus.START)
        }
    })
}
