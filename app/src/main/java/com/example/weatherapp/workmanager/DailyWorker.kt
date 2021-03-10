package com.example.weatherapp.workmanager

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

open class DailyWorker(private val context: Context, private val params: WorkerParameters) :
    ListenableWorker(context, params) {

    private val ioDispatcher = Dispatchers.IO

    private val constraints
        get() = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            CoroutineScope(ioDispatcher).launch {
                doWork(completer)
            }
        }
    }

    private suspend fun doWork(completer: CallbackToFutureAdapter.Completer<Result>) =
        withContext(ioDispatcher) {
            try {
                /**
                 * If setInitialDelay for 3 works with 15 minutes each
                 * Then it will delay for 15 minutes work 1
                 * Then delay again 15 minutes for work 2
                 * ... again and again
                 * It doesn't delay all 3 at the same time
                 *
                 * SOLUTION => Delay final work
                 */


                val saveResult = params.inputData.getBoolean(SAVE_RESULT, false)

                val delay = if (saveResult) 15L else 45L

                val uniqueFetchLocationWork =
                    OneTimeWorkRequest.Builder(FetchLocationWorker::class.java)
                        .setConstraints(constraints)
                        .addTag(LOCATION_WORKER_KEY)
                        .build()

                val uniqueFetchAPIWork =
                    OneTimeWorkRequest.Builder(AsyncAPIWorker::class.java)
                        .setConstraints(constraints)
                        .addTag(ASYNC_API_WORKER_KEY)
                        .build()

                val saveWorker =
                    OneTimeWorkRequest.Builder(StorageWorker::class.java)
                        .setConstraints(constraints)
                        .addTag(
                            SAVING_WORKER_KEY
                        ).build()

                // Create a worker of itself
                val dailyWorker = OneTimeWorkRequestBuilder<DailyWorker>()
                    .setConstraints(constraints)
                    .setInitialDelay(delay, TimeUnit.MINUTES)
                    .addTag(DAILY_WORK_NAME)
                    .build()

                WorkManager.getInstance(context)
                    .beginWith(uniqueFetchLocationWork)
                    .then(uniqueFetchAPIWork)
                    .then(saveWorker)
                    .then(dailyWorker)
                    .enqueue()

                completer.set(Result.success())

            } catch (e: Exception) {
                e.printStackTrace()
                completer.set(Result.failure(createOutputData(false)))
            }
        }


    private fun createOutputData(result: Boolean): Data =
        Data.Builder()
            .putBoolean(DAILY_RESULT, result)
            .build()
}