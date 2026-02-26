package com.training.quicknote.util

import com.training.quicknote.R

enum class Category(val displayName: String, val Categorycolor: Int) {
    WORK("Work", R.color.workCatClr),
    PERSONAL("Personal", R.color.personalCatClr),
    STUDY("Study", R.color.studyCatClr);

    override fun toString(): String {
        return displayName
    }
}