package vn.hitu.ntb.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import vn.hitu.ntb.constants.AppConstants


object AudioUtils {
    fun setProcessBarForAudio(
        set1: AnimatorSet,
        set2: AnimatorSet,
        set3: AnimatorSet,
        pbProgressbar1: ProgressBar,
        pbProgressbar2: ProgressBar,
        pbProgressbar3: ProgressBar
    ) {
        /**pbProgressbar1 && set1**/
        pbProgressbar1.progress = 75
        val progressAnimator1 = ObjectAnimator.ofInt(pbProgressbar1, AppConstants.PROGRESS, 75, 100)
        progressAnimator1.duration = 800
        progressAnimator1.interpolator = LinearInterpolator()
        progressAnimator1.repeatCount = ValueAnimator.INFINITE
        val progressAnimator2 = ObjectAnimator.ofInt(pbProgressbar1, AppConstants.PROGRESS, 100, 25)
        progressAnimator2.duration = 800
        progressAnimator2.interpolator = LinearInterpolator()
        progressAnimator2.repeatCount = ValueAnimator.INFINITE
        val progressAnimator3 = ObjectAnimator.ofInt(pbProgressbar1, AppConstants.PROGRESS, 25, 75)
        progressAnimator3.duration = 800
        progressAnimator3.interpolator = LinearInterpolator()
        progressAnimator3.repeatCount = ValueAnimator.INFINITE

        set1.play(progressAnimator1).before(progressAnimator2)
        set1.play(progressAnimator2).before(progressAnimator3)

        /**pbProgressbar2 && set2**/
        pbProgressbar2.progress = 40
        val progressAnimator4 = ObjectAnimator.ofInt(pbProgressbar2, AppConstants.PROGRESS, 40, 20)
        progressAnimator4.duration = 500
        progressAnimator4.interpolator = LinearInterpolator()
        progressAnimator4.repeatCount = ValueAnimator.INFINITE
        val progressAnimator5 = ObjectAnimator.ofInt(pbProgressbar2, AppConstants.PROGRESS, 20, 90)
        progressAnimator5.duration = 500
        progressAnimator5.interpolator = LinearInterpolator()
        progressAnimator5.repeatCount = ValueAnimator.INFINITE
        val progressAnimator6 = ObjectAnimator.ofInt(pbProgressbar2, AppConstants.PROGRESS, 90, 40)
        progressAnimator6.duration = 500
        progressAnimator6.interpolator = LinearInterpolator()
        progressAnimator6.repeatCount = ValueAnimator.INFINITE

        set2.play(progressAnimator4).before(progressAnimator5)
        set2.play(progressAnimator5).before(progressAnimator6)

        /**pbProgressbar3 && set3**/
        pbProgressbar3.progress = 55
        val progressAnimator7 = ObjectAnimator.ofInt(pbProgressbar3, AppConstants.PROGRESS, 55, 80)
        progressAnimator7.duration = 500
        progressAnimator7.interpolator = LinearInterpolator()
        progressAnimator7.repeatCount = ValueAnimator.INFINITE
        val progressAnimator8 = ObjectAnimator.ofInt(pbProgressbar3, AppConstants.PROGRESS, 80, 15)
        progressAnimator8.duration = 500
        progressAnimator8.interpolator = LinearInterpolator()
        progressAnimator8.repeatCount = ValueAnimator.INFINITE
        val progressAnimator9 = ObjectAnimator.ofInt(pbProgressbar3, AppConstants.PROGRESS, 15, 55)
        progressAnimator9.duration = 500
        progressAnimator9.interpolator = LinearInterpolator()
        progressAnimator9.repeatCount = ValueAnimator.INFINITE

        set3.play(progressAnimator7).before(progressAnimator8)
        set3.play(progressAnimator8).before(progressAnimator9)
    }
    fun setProcessBarForCountDown(
        set1: AnimatorSet,
        pbProgressbar1: ProgressBar,
    ) {
        /**pbProgressbar1 && set1**/
        pbProgressbar1.progress = 0
        val progressAnimator1 = ObjectAnimator.ofInt(pbProgressbar1, AppConstants.PROGRESS, 0, 100)
        progressAnimator1.duration = 3000
        progressAnimator1.interpolator = LinearInterpolator()

        set1.play(progressAnimator1)
//        set1.start()


    }

}