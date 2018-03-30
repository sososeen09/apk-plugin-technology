package com.sososeen09.plugin.lib

import android.app.Activity
import android.app.Service
import android.widget.Toast

/**
 * Created by yunlong.su on 2018/3/29.
 */


fun Activity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Service.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}