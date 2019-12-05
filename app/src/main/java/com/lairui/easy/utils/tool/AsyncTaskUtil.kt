package com.lairui.easy.utils.tool

import android.os.AsyncTask

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

object AsyncTaskUtil {

    var callback: TaskCallback? = null
    var mcallback: ProgressBack? = null


    /**
     * 设置核心线程数并开始执行
     * @param num  核心线程数
     * @param callback  执行结果回调处理
     */
    fun excute(num: Int, callback: TaskCallback) {
        //设置核心线程数
        setCoreThreadNum(num)
        MyAsyncTask().execute()
        LogUtil.i("show", "当前线程：" + Thread.currentThread().name)
    }


    /**
     * AsyncTask  支持串行（线程同步） 和 并行（线程异步 默认4个线程异步）
     * 通过反射机制  自定义并行的线程数
     * @param num num == 1  为线程同步（串行）
     */
    private fun setCoreThreadNum(num: Int) {
      /*  try {
            //Class<?> cls = Class.forName("AsyncTask");
            val setExecutor = AsyncTask<*, *, *>::class.java.getMethod("setDefaultExecutor", Executor::class.java)

            //阻塞任务队列 最大128个任务
            val sPoolWorkQueue = LinkedBlockingQueue<Runnable>(128)

            //新建线程工厂
            val sThreadFactory = object : ThreadFactory {
                private val mCount = AtomicInteger(1)

                override fun newThread(r: Runnable): Thread {
                    return Thread(r, "AsyncTask #" + mCount.getAndIncrement())
                }
            }
            //核心线程池大小  最大线程池大小  空闲线程存活时间  时间单位
            val CPU_COUNT = Runtime.getRuntime().availableProcessors()
            val CORE_POOL_COUNT = Math.max(2, Math.min(CPU_COUNT - 1, 4))
            val MAX_POOL_COUNT = CPU_COUNT * 2 + 1
            LogUtil.i("show", "CPU_COUNT:$CPU_COUNT  默认：CORE_POOL_COUNT:$CORE_POOL_COUNT   MAX_POOL_COUNT:$MAX_POOL_COUNT")
            val threadPoolExecutor = ThreadPoolExecutor(
                    num, MAX_POOL_COUNT, 30, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory)
            threadPoolExecutor.allowCoreThreadTimeOut(true)

            val task = MyAsyncTask()

            setExecutor.invoke(task, threadPoolExecutor)

        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()

        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }*/

    }

    /**
     * 自定义AsyncTask
     */


    private class MyAsyncTask : AsyncTask<Map<Any, Any>, Int, Map<Any, Any>>() {
        init {
            mcallback = object : ProgressBack {
                @WorkerThread
                override fun updateProgress(vararg values: Int) {
                    publishProgress(values[0])
                }
            }
            mcallback = mcallback

        }

        //开始执行前（UI线程里的初始化工作）
        @MainThread
        override fun onPreExecute() {
            super.onPreExecute()
            if (callback != null) {
                callback!!.initOnUI()
            }
        }


        //取消
        override fun onCancelled() {
            super.onCancelled()
            if (callback != null) {
                callback!!.cancellTask()
            }
        }

        //子线程执行
        @WorkerThread
        override fun doInBackground(vararg maps: Map<Any, Any>): Map<Any, Any>? {
            return if (callback != null) {
                callback!!.doTask(*maps)
            } else null

        }


        //主线程更新UI
         @MainThread
         override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            if (callback != null) {
                callback!!.progressTask(*values)
            }
        }


        //线程执行完成
        @MainThread
        override fun onPostExecute(objectObjectMap: Map<Any, Any>) {
            super.onPostExecute(objectObjectMap)
            if (callback != null) {
                callback!!.resultTask(objectObjectMap)
            }
        }


    }

    interface TaskCallback {
        //UI线程中初始化
        fun initOnUI()

        //取消线程任务
        fun cancellTask()

        //执行线程任务，执行完毕返回执行结果
        fun doTask(vararg maps: Map<Any, Any>): Map<Any, Any>

        //执行线程任务进度
        fun progressTask(vararg values: Int?)

        //执行完成获取执行结果并处理
        fun resultTask(objectObjectMap: Map<Any, Any>)

    }

    interface ProgressBack {
        //更新进度值
        fun updateProgress(vararg values: Int)
    }
}
