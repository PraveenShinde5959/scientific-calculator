package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.EvaluationResult
import com.example.util.MathEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Scientific Calculator", appName)
  }

  @Test
  fun `test scientific math evaluation`() {
    val result = MathEvaluator.evaluate("2 + 3 * 4", isRadian = false)
    assertTrue(result is EvaluationResult.Success)
    assertEquals("14", (result as EvaluationResult.Success).result)

    val trigResult = MathEvaluator.evaluate("sin(90)", isRadian = false)
    assertTrue(trigResult is EvaluationResult.Success)
    assertEquals("1", (trigResult as EvaluationResult.Success).result)
  }
}
