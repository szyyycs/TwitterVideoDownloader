package com.ycs.servicetest.activity


import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.ycs.servicetest.R
import com.ycs.servicetest.common.Config.defaultDownloadPath
import com.ycs.servicetest.common.KVKey
import com.ycs.servicetest.compose.DatePicker
import com.ycs.servicetest.ui.theme.BlueLight
import com.ycs.servicetest.ui.theme.BlueMain
import com.ycs.servicetest.utils.BiometricUtil
import com.ycs.servicetest.utils.KVUtil
import com.ycs.servicetest.utils.StatusBarUtil
import com.ycs.servicetest.utils.VibratorUtil
import com.ycs.servicetest.utils.showToast
import com.ycs.servicetest.view.CustomIosAlertDialog
import com.ycs.servicetest.view.CustomToolBar
import kotlinx.coroutines.launch


class SettingActivity : ComponentActivity() {
    private val settingList: MutableList<String> by lazy {
        mutableListOf(
            "设置下载路径",
            "设置彩蛋弹出日期",
            "设置是否开启指纹验证",
            "关于VideoDownload"
        )
    }
    private val path =
        MutableLiveData<String>(KVUtil.getString(KVKey.SAVE_URL, defaultDownloadPath))

    private val descriptionList: MutableList<String> by lazy {
        mutableListOf(
            "设置视频的下载文件夹名字，命名为.开头的文件夹名可以在相册中不显示，只在应用中显示",
            "在设置的日期内弹出彩蛋",
            "开启后，需要指纹验证后才能进入视频列表",
            ""
        )
    }

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
                mutableStateOf(KVUtil.getBool(KVKey.OPEN_FINGER, false, KVKey.SETTING) ?: false)
            }
            val scope = rememberCoroutineScope()
            ShowBottomPopUp(list = birthDayList, openDialog = openDatePickerDialog) { state ->
                Surface(
                    modifier = Modifier.fillMaxSize()/*, color = MaterialTheme.colorScheme.background*/
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CustomToolBar("设置")
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .background(Color(0xffeeeeee))
                                .padding(top = 20.dp)
                        ) {
                            itemsIndexed(settingList) { index, message ->
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
                                    message,
                                    descriptionList[index],
                                    if (index == 0) Environment.getExternalStorageDirectory()
                                        .toString() + "/" + tips else "",
                                    if (index == 2) isOpenFingerVerify else null
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowBottomPopUp(
    list: MutableList<String>,
    openDialog: MutableState<Boolean>,
    content: @Composable (ModalBottomSheetState) -> Unit
) {

    val skipHalfExpanded by remember { mutableStateOf(false) }
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = skipHalfExpanded
    )
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            AddBirthDayDialogContent(list = list) {
                openDialog.value = true
            }
        }) {
        content(state)
    }
}


@Composable
fun AddBirthDayDialogContent(list: MutableList<String>, onAddDate: () -> Unit) {

    var deleteStateList by remember {
        mutableStateOf(MutableList(list.size) { mutableStateOf(false) })
    }
    deleteStateList = MutableList(list.size) { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp)
    ) {

        Text(
            text = "目前设置的日期",
            fontSize = 18.sp,
            color = Color(0xff555555),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(20.dp),
            fontWeight = FontWeight.Bold
        )

        Divider(
            color = Color(0xFFf2f2f2),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Text(
                    text = "<空>",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    color = Color(0xffbbbbbb),
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Divider(
                color = Color(0xFFf2f2f2),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 30.dp)
            )

        } else {
            LazyColumn {
                itemsIndexed(list) { index, item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .pointerInput(Unit) {
                                detectTapGestures(onLongPress = {
                                    if (!deleteStateList[index].value) {
                                        VibratorUtil.vibrator(100)
                                        deleteStateList[index].value = true
                                    }
                                }, onTap = {
                                    deleteStateList =
                                        MutableList(list.size) { mutableStateOf(false) }
                                })
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {

                            Text(
                                text = parseItem(item),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                color = Color(0xff666666),
                                textAlign = TextAlign.Center,
                            )
                            //删除块
                            Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                                AnimatedVisibility(deleteStateList[index].value,
                                    enter = slideInHorizontally { 500 },
                                    exit = slideOutHorizontally { 500 }
                                ) {
                                    Text(
                                        text = "删除",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .width(70.dp)
                                            .fillMaxHeight()
                                            .padding(end = 10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(colorResource(R.color.colorRed))
                                            .pointerInput(Unit) {
                                                detectTapGestures(
                                                    onTap = {
                                                        KVUtil.remove(
                                                            list[index].split(".")[0],
                                                            KVKey.BIRTH_DAY
                                                        )
                                                        deleteStateList.removeAt(index)
                                                        list.removeAt(index)
                                                    }
                                                )
                                            }
                                            .padding(top = 12.dp, bottom = 12.dp),
                                        style = TextStyle.Default.copy(
                                            color = colorResource(R.color.white),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                        Divider(
                            color = Color(0xFFf2f2f2),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 30.dp)
                        )
                    }

                }
            }
        }
        Image(
            painter = painterResource(id = R.drawable.add),
            contentDescription = "add",
            modifier = Modifier
                .padding(vertical = 15.dp)
                .height(20.dp)
                .width(20.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    onAddDate()
                }
        )

    }


}

fun parseItem(item: String): String {
    val temp = item.split(".")
    if (temp.size == 3) {
        return "${temp[1]}月${temp[2]}日（ ${temp[0]} ）"
    }
    return ""
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDatePickerDialog(
    openDialog: MutableState<Boolean>,
    onDateAdded: (Int, Int, String) -> Unit
) {
    AnimatedVisibility(
        visible = openDialog.value,
        enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)),
        exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
    ) {
        BasicAlertDialog(
            onDismissRequest = { openDialog.value = false }) {
            DatePicker(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                title = "选择要生效的日期",
                onDismiss = { selected, month, day, name ->
                    openDialog.value = false
                    if (selected) {
                        onDateAdded(month, day, name)
                    }
                })
        }
    }
}

@Preview
@Composable
fun te() {
    SettingItems(Modifier, "11", "222", "33", mutableStateOf(true))
}

@Composable
fun SettingItems(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    tips: String,
    isSwitchItem: MutableState<Boolean>? = null,
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(Color.White)
    ) {

        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = title, fontSize = 17.sp,
                    color = Color(0xff444444),
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 10.dp,
                        top = 20.dp,
                        bottom = if (description.isNotBlank()) 10.dp else 20.dp
                    )
                )
                Text(
                    text = tips,
                    fontSize = 11.sp,
                    color = Color(0xffaaaaaa),
                    modifier = Modifier
                        .padding(
                            end = 50.dp,
                            bottom = if (description.isNotBlank()) 10.dp else 20.dp
                        )
                )
            }
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color(0xffaaaaaa),
                    modifier = Modifier
                        .padding(bottom = 20.dp, start = 20.dp, end = 50.dp)

                )
            }
        }
        if (isSwitchItem != null) {
            Switch(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
                checked = isSwitchItem.value,
                onCheckedChange = {
                    if (BiometricUtil.biometricEnable()) {
                        isSwitchItem.value = !isSwitchItem.value
                        KVUtil.setData(KVKey.OPEN_FINGER, isSwitchItem.value, KVKey.SETTING)
                    } else {
                        showToast("硬件不支持或设备未开启指纹验证")
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BlueMain,
                    checkedTrackColor = BlueLight
                )
            )
        } else {
            Image(
                painter = painterResource(id = R.mipmap.enter),
                contentDescription = "",
                modifier = Modifier
                    .width(50.dp)
                    .align(Alignment.CenterEnd)
                    .size(25.dp)
            )
        }

    }
}

