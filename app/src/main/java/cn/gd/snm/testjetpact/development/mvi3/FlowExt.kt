package cn.gd.snm.testjetpact.development.mvi3

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


/**
 * 封装repeatOnLifecycle和协程launch。
 *
 */
fun AppCompatActivity.launchRepeatOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend () -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            action()
        }
    }
}