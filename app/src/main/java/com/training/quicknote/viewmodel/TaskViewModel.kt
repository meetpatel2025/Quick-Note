package com.training.quicknote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.training.quicknote.datamodel.Task

class TaskViewModel : ViewModel() {
    val taskList = mutableListOf<Task>()
}