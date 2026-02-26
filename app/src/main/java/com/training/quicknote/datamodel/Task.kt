package com.training.quicknote.datamodel

data class Task(val description: String, val category:String)

// I have to create cusstom adapter for showing data into recyclerview
// so firstly I have to insert task in the edittext
// then I have to select category that which kind of topic is this, so there are three categories only : Personal, Work, Study
// each category has it's color so there are three color also
// now when user input any text then select category then clicks on save button then it should be visible on recycler view
// view is looks like category color then categoryType(amonng 3) and user's description
// I also upload the image so you can see that
// if user try to save without entering any task so it should be give error also if it does not select any type then it should be by default take personal


