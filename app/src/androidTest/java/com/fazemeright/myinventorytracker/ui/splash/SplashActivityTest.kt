package com.fazemeright.myinventorytracker.ui.splash

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.fazemeright.myinventorytracker.R
import com.fazemeright.myinventorytracker.isViewDisplayed
import com.fazemeright.myinventorytracker.ui.base.BaseUiTest
import org.hamcrest.Matchers.allOf
import timber.log.Timber

class SplashActivityTest : BaseUiTest<SplashActivity>() {

    override fun allViewsAreDisplayed() {
        R.id.tvAppVersion.isViewDisplayed()
        R.id.tvAppTagLine.isViewDisplayed()
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        val expected = try {
            Timber.d("Version name info called")
            val pInfo: PackageInfo =
                appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Debug Version"
        }
        onView(allOf(withId(R.id.tvAppVersion), withText(expected))).check(
            matches(
                isDisplayed()
            )
        )
    }

    override fun getActivity(): Class<SplashActivity> = SplashActivity::class.java
}