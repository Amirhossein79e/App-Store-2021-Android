package com.amirhosseinemadi.appstore.viewmodel

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.model.entity.UserModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.callback.AccountCallback
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import org.json.JSONObject

class AccountVm(private val accountCallback:AccountCallback) : ViewModel() {

    private val apiCaller: ApiCaller

    val error: MutableLiveData<String>
    val signUpResponse: MutableLiveData<ResponseObject<UserModel>>
    val signInResponse: MutableLiveData<ResponseObject<UserModel>>
    val validateResponse: MutableLiveData<ResponseObject<String>>
    val updateResponse: MutableLiveData<ResponseObject<List<AppModel>>>

    var emailStr:MutableLiveData<String> = MutableLiveData<String>().also{ it.value = ""}
    var passwordStr:MutableLiveData<String> = MutableLiveData<String>().also{ it.value = ""}
    var usernameStr:MutableLiveData<String> = MutableLiveData<String>().also{ it.value = ""}
    var passwordReStr:MutableLiveData<String> = MutableLiveData<String>().also{ it.value = ""}
    val visib: MutableLiveData<Int> = MutableLiveData<Int>().also { it.value = View.GONE }
    val btnText: MutableLiveData<String> = MutableLiveData<String>().also { it.value = accountCallback.getStr(R.string.sign_in) }
    private var signIn: Boolean

    init {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        signUpResponse = MutableLiveData()
        signInResponse = MutableLiveData()
        validateResponse = MutableLiveData()
        updateResponse = MutableLiveData()
        signIn = true
    }


    fun btnClick(view: View) {
        if (signIn) {
            if (Utilities.validateEmail(emailStr.value!!)) {
                if (Utilities.validatePassword(passwordStr.value!!)) {
                    accountCallback.signIn(emailStr.value!!, passwordStr.value!!)
                } else {
                    accountCallback.onMessage(R.string.password_error)
                }
            } else {
                accountCallback.onMessage(R.string.email_error);
            }
        } else {
            if (Utilities.validateEmail(emailStr.value!!)) {
                if (usernameStr.value!!.length > 2) {
                    if (Utilities.validatePassword(passwordStr.value!!)) {
                        if (passwordStr.equals(passwordReStr)) {
                            accountCallback.signUp(
                                emailStr.value!!,
                                passwordStr.value!!,
                                usernameStr.value!!,
                                PrefManager.getToken()!!
                            )
                        } else {
                            accountCallback.onMessage(R.string.password_error_repeat)
                        }
                    } else {
                        accountCallback.onMessage(R.string.password_error)
                    }
                } else {
                    accountCallback.onMessage(R.string.username_error)
                }
            } else {
                accountCallback.onMessage(R.string.email_error);
            }
        }
    }


    fun signClick(view: View) {
        if (signIn)
        {
            signIn = false
            val str: SpannableString = SpannableString(accountCallback.getStr(R.string.sign_in_alt))
            str.setSpan(UnderlineSpan(), 0, str.length, 0)
            (view as AppCompatTextView).text = str
            btnText.value = accountCallback.getStr(R.string.sign_up)
            visib.value = View.VISIBLE
            emailStr.value = ""
            passwordStr.value = ""
        } else
        {
            signIn = true
            val str: SpannableString = SpannableString(accountCallback.getStr(R.string.sign_up_alt))
            str.setSpan(UnderlineSpan(), 0, str.length, 0)
            (view as AppCompatTextView).text = str
            btnText.value = accountCallback.getStr(R.string.sign_in)
            visib.value = View.GONE
        }
    }


    fun signUp(email: String, password: String, username: String, token: String) {
        apiCaller.signUpUser(
            email,
            password,
            username,
            token,
            object : SingleObserver<ResponseObject<UserModel>> {
                override fun onSubscribe(d: Disposable?) {
                    accountCallback.onShow()
                }

                override fun onSuccess(t: ResponseObject<UserModel>?) {
                    signUpResponse.value = t
                    accountCallback.onHide()
                }

                override fun onError(e: Throwable?) {
                    error.value = "signUp"
                    accountCallback.onHide()
                }

            })
    }


    fun signIn(email: String, password: String) {
        apiCaller.signInUser(email, password, object : SingleObserver<ResponseObject<UserModel>> {
            override fun onSubscribe(d: Disposable?) {
                accountCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<UserModel>?) {
                signInResponse.value = t
                accountCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "signIn"
                accountCallback.onHide()
            }

        })
    }


    fun validateUser(access: String) {
        apiCaller.validateUser(access, object : SingleObserver<ResponseObject<String>> {
            override fun onSubscribe(d: Disposable?) {
                accountCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<String>?) {
                validateResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = "validateUser"
                accountCallback.onHide()
            }
        })

    }


    fun update(offset: Int, packages: List<JSONObject>, isUser: Boolean) {
        apiCaller.getUpdates(
            offset,
            packages,
            object : SingleObserver<ResponseObject<List<AppModel>>> {
                override fun onSubscribe(d: Disposable?) {
                    if (!isUser) {
                        accountCallback.onShow()
                    }
                }

                override fun onSuccess(t: ResponseObject<List<AppModel>>?) {
                    updateResponse.value = t
                    accountCallback.onHide()
                }

                override fun onError(e: Throwable?) {
                    error.value = "update"
                    accountCallback.onHide()
                }

            })
    }

}