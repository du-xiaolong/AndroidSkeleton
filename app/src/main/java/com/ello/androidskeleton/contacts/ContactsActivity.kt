package com.ello.androidskeleton.contacts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.ello.androidskeleton.databinding.ActivityContactsBinding
import com.ello.base.common.BaseViewModel
import com.ello.base.common.BaseVmActivity
import com.ello.base.ktx.lllog
import com.ello.base.ktx.toast
import com.permissionx.guolindev.PermissionX

/**
 * 通讯录
 * @author dxl
 * @date 2023/4/20
 */
class ContactsActivity : BaseVmActivity<BaseViewModel, ActivityContactsBinding>() {
    companion object {
        fun start(context: Context) =
            context.startActivity(Intent(context, ContactsActivity::class.java))
    }

    //选择一个联系人
    private val launcher = registerForActivityResult(ActivityResultContracts.PickContact()) {
        it ?: return@registerForActivityResult
        contentResolver.query(
            it,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    cursor.columnNames.forEach { columnName ->
                        val columnIndex = cursor.getColumnIndex(columnName)
                        val value = when (cursor.getType(columnIndex)) {
                            Cursor.FIELD_TYPE_BLOB -> cursor.getBlobOrNull(columnIndex)
                                ?.decodeToString()
                            Cursor.FIELD_TYPE_FLOAT -> cursor.getFloatOrNull(columnIndex)
                                ?.toString()
                            Cursor.FIELD_TYPE_INTEGER -> cursor.getIntOrNull(columnIndex)
                                ?.toString()
                            Cursor.FIELD_TYPE_STRING -> cursor.getStringOrNull(columnIndex)
                            else -> "null"
                        }
                        lllog("$columnName:$value")

                    }

                    val hasPhoneNumber =
                        cursor.getIntOrNull(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            ?: 0
                    if (hasPhoneNumber > 0) {
                        val contactId =
                            cursor.getIntOrNull(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                                ?: 0
                        readPhoneNumbers(contactId)
                    }
                    lllog("-------------------------------------------------------")
                } while (cursor.moveToNext())
            }
        }
    }

    private fun readPhoneNumbers(contactId: Int) {
        //有电话
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ",
            arrayOf(contactId.toString()),
            null
        )?.use { phoneCursor ->
            val phoneMap = mutableMapOf<Int, String>()
            if (phoneCursor.moveToFirst()) {
                do {
                    val phoneType =
                        phoneCursor.getIntOrNull(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                    val number =
                        phoneCursor.getStringOrNull(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (phoneType != null && !number.isNullOrEmpty()) {
                        phoneMap[phoneType] = number
                    }
                } while (phoneCursor.moveToNext())
            }
            lllog(phoneMap)
        }
    }

    override fun init(savedInstanceState: Bundle?) {

        PermissionX.init(this).permissions(Manifest.permission.READ_CONTACTS)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "需要读取联系人",
                    "允许",
                    "拒绝"
                )
            }.onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去设置中手动开启【读取联系人】权限", "去设置", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    launcher.launch(null)
                } else {
                    "权限被拒绝".toast()
                }
            }


//        contentResolver.query(
//            ContactsContract.Contacts.CONTENT_URI,
//            null,
//            null,
//            null,
//            ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
//        )?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                do {
//                    cursor.columnNames.forEach { columnName ->
//                        val columnIndex = cursor.getColumnIndex(columnName)
//                        val value = when(cursor.getType(columnIndex)) {
//                            Cursor.FIELD_TYPE_BLOB -> cursor.getBlobOrNull(columnIndex)?.decodeToString()
//                            Cursor.FIELD_TYPE_FLOAT ->cursor.getFloatOrNull(columnIndex)?.toString()
//                            Cursor.FIELD_TYPE_INTEGER -> cursor.getIntOrNull(columnIndex)?.toString()
//                            Cursor.FIELD_TYPE_STRING -> cursor.getStringOrNull(columnIndex)
//                            else -> "null"
//                        }
//                        lllog("$columnName:$value")
//                    }
//                    lllog("-------------------------------------------------------")
//                } while (cursor.moveToNext())
//            }
//        }

    }


}