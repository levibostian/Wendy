package com.levibostian.wendyexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.curiosityio.wendyexample.R
import com.levibostian.wendy.WendyConfig
import com.levibostian.wendy.listeners.TaskRunnerListener
import com.levibostian.wendy.service.PendingTask
import com.levibostian.wendy.service.PendingTasks
import com.levibostian.wendy.types.ReasonPendingTaskSkipped
import com.levibostian.wendyexample.extension.closeKeyboard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TaskRunnerListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity_main_create_pending_task_button.setOnClickListener {
            if (activity_main_custom_data_edittext.text.isBlank()) {
                activity_main_custom_data_edittext.error = "You must enter data here."
            } else {
                val pendingTask = FooPendingTask(
                        activity_main_manually_run_checkbox.isChecked,
                        if (activity_main_group_id_edittext.text.isNullOrBlank()) null else activity_main_group_id_edittext.text.toString(),
                        activity_main_custom_data_edittext.text.toString()
                )
                PendingTasks.sharedInstance().addTask(pendingTask)
                closeKeyboard()
            }
        }

        activity_main_tasks_recyclerview.layoutManager = LinearLayoutManager(this)
        refreshListOfTasks()

        WendyConfig.addTaskRunnerListener(this)
    }

    private fun refreshListOfTasks() {
        val recyclerViewAdapter = PendingTasksRecyclerViewAdapter(PendingTasks.sharedInstance().getAllTasks())
        recyclerViewAdapter.listener = object : PendingTasksRecyclerViewAdapter.Listener {
            override fun manuallyRunPressed(task: PendingTask) {
                PendingTasks.sharedInstance().runTask(task.id)
            }
        }
        activity_main_tasks_recyclerview.adapter = recyclerViewAdapter
    }

    override fun newTaskAdded(id: Long) {
        refreshListOfTasks()
    }
    override fun runningTask(task: PendingTask) {
    }
    override fun taskSkipped(reason: ReasonPendingTaskSkipped, task: PendingTask) {
    }
    override fun taskComplete(success: Boolean, task: PendingTask) {
        Handler().postDelayed({
            refreshListOfTasks()
        }, 1000)
    }
    override fun allTasksComplete() {
    }

}
