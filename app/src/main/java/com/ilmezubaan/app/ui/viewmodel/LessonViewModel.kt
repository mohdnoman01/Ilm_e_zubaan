package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel

class LessonViewModel : ViewModel() {

    val lessons = listOf(
        "Lesson 1: Basics",
        "Lesson 2: Greetings",
        "Lesson 3: Common Words"
    )
}
