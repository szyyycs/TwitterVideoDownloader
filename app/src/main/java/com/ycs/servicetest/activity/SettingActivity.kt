package com.ycs.servicetest.activity


import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.ycs.servicetest.common.Config
import com.ycs.servicetest.common.Config.defaultDownloadPath
import com.ycs.servicetest.common.KVKey
import com.ycs.servicetest.compose.AddDatePickerDialog
import com.ycs.servicetest.compose.SettingItems
import com.ycs.servicetest.compose.ShowBottomPopUp
import com.ycs.servicetest.type.SettingType
import com.ycs.servicetest.utils.KVUtil
import com.ycs.servicetest.utils.StatusBarUtil
import com.ycs.servicetest.view.CustomIosAlertDialog
import com.ycs.servicetest.view.CustomToolBar
import kotlinx.coroutines.launch


class SettingActivity : ComponentActivity() {

    private val map: LinkedHashMap<String, String> =
        linkedMapOf(
            "设置下载路径" to "设置视频的下载文件夹名字，命名为.开头的文件夹名可以在相册中不显示，只在应用中显示",
            "设置彩蛋弹出日期" to "在设置的日期内弹出彩蛋",
            "设置是否开启指纹验证" to "开启后，需要指纹验证后才能进入视频列表",
            "设置Tiktok模式是否随机展示" to "随机显示所有的视频",
            "设置小红书模式播放可上下滑" to "开启后点击播放时变为抖音模式",
            "关于VideoDownload" to ""
        )
    private val path =
        MutableLiveData<String>(KVUtil.getString(KVKey.SAVE_URL, defaultDownloadPath))

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val tips by path.observeAsState()
            val openDatePickerDialog = remember {
                mutableStateOf(false)
            }
            val birthDayList = mutableStateListOf<String>().apply {
                getBirthdayDate(this)
            }
            val isOpenFingerVerify = remember {
                mutableStateOf(
                    KVUtil.getBool(
                        KVKey.OPEN_FINGER,
                        Config.DEFAULT_OPEN_FINGER,
                        KVKey.SETTING
                    ) ?: Config.DEFAULT_OPEN_FINGER
                )
            }
            val isShufflePlay = remember {
                mutableStateOf(
                    KVUtil.getBool(
                        KVKey.SHUFFLE_VIDEOS,
                        Config.DEFAULT_SHUFFLE_VIDEOS,
                        KVKey.SETTING
                    ) ?: Config.DEFAULT_SHUFFLE_VIDEOS
                )
            }
            val isXhsPlayIntoTiktok = remember {
                mutableStateOf(
                    KVUtil.getBool(
                        KVKey.XHS_PLAY_MODE_TYPE,
                        Config.DEFAULT_IS_XHS_PLAY_INTO_TIKTOK,
                        KVKey.SETTING
                    ) ?: Config.DEFAULT_IS_XHS_PLAY_INTO_TIKTOK
                )
            }
            val scope = rememberCoroutineScope()
            ShowBottomPopUp(list = birthDayList, openDialog = openDatePickerDialog) { state ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CustomToolBar("设置")
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .background(Color(0xffeeeeee))
                                .padding(top = 20.dp)
                        ) {
                            itemsIndexed(map.keys.toList()) { index, message ->
                                SettingItems(
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {

                                                }, onTap = {
                                                    when (index) {
                                                        0 -> showDialog()
                                                        1 -> scope.launch { state.show() }
                                                        2 -> {}
                                                        3 -> {}
                                                    }
                                                }

                                            )
                                        },
                                    title = message,
                                    description = map[message]!!,
                                    tips = if (index == 0) Environment.getExternalStorageDirectory()
                                        .toString() + "/" + tips else "",
                                    settingType = when (index) {
                                        2 -> SettingType.FingerType(isOpenFingerVerify)
                                        3 -> SettingType.ShuffleVideosType(isShufflePlay)
                                        4 -> SettingType.XhsPlayModeType(isXhsPlayIntoTiktok)
                                        else -> null
                                    }
                                )
                            }
                        }
                    }
                }
            }
            AddDatePickerDialog(openDialog = openDatePickerDialog) { month, day, name ->
                birthDayList.add("${name}.${month}.${day}")
                KVUtil.setData(name, "$month.$day", KVKey.BIRTH_DAY)
            }
        }
        StatusBarUtil.setStatusBarColor(this, isLight = true)
    }

    private fun getBirthdayDate(list: MutableList<String>) {
        val mmkv = KVUtil.getMMKV(KVKey.BIRTH_DAY)
        val allKey = mmkv.allKeys()
        allKey?.let {
            for (key in allKey) {
                list.add("${key}.${mmkv.decodeString(key)}")
            }
        }
    }


    private fun showDialog() {
        val dialog = CustomIosAlertDialog(this@SettingActivity).builder()
        dialog.setEditText(path.value)
            .setTitle("设置你的下载文件夹名称")
            .setPositiveButton(
                "确定"
            ) { _: View? ->
                if (dialog.editText.isNotEmpty()) {
                    KVUtil.setData(KVKey.SAVE_URL, dialog.editText.trim())
                    path.value = dialog.editText.trim()
                    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消") { _: View? -> }
            .show()

    }

}
