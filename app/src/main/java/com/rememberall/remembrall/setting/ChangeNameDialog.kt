package com.rememberall.remembrall.setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.rememberall.remembrall.ApiClient
import com.rememberall.remembrall.databinding.DialogChangeNameBinding
import com.rememberall.remembrall.user.UserService
import com.rememberall.remembrall.user.res.UserNameResponse
import com.rememberall.remembrall.user.userinfo.SharedManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeNameDialog (
    context: Context
) : Dialog(context){ // 뷰를 띄워야 하므로 Dialog 클래스는 context를 인자로 받는다.
    private lateinit var binding: DialogChangeNameBinding
    private lateinit var onClickListener: OnDialogClickListener

    var userService : UserService = ApiClient.create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogChangeNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {
        binding.btnChangeName.setOnClickListener {
            val newName = binding.etNewName.text.toString().trim()
            val sharedManager : SharedManager by lazy { SharedManager(context) }
            var authToken = sharedManager.getCurrentUser().accessToken
            userService.changeUserName(authToken!!, newName).enqueue(object :
                Callback<UserNameResponse> {
                override fun onResponse(
                    call: Call<UserNameResponse>,
                    response: Response<UserNameResponse>
                ) {
                    Log.e("Username", response.body().toString())
                    if(response.body()?.success.toString() == "true"){
                        Toast.makeText(context,"닉네임 변경 성공", Toast.LENGTH_SHORT).show()
                        onClickListener.onClicked(newName)
                    }
                    dismiss()
                }
                override fun onFailure(call: Call<UserNameResponse>, t: Throwable) {
                    Toast.makeText(context,"닉네임 변경 실패", Toast.LENGTH_SHORT).show()
                }
                })

            dismiss()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        setOnCancelListener {
            Toast.makeText(context,"일기장 생성을 취소했어요", Toast.LENGTH_SHORT).show()
        }
    }

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }
}
