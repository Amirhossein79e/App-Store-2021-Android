package com.amirhosseinemadi.appstore.viewmodel

import android.content.Context
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
    val error:MutableLiveData<String>

    val signUpResponse:MutableLiveData<ResponseObject<UserModel>>
    val signInResponse:MutableLiveData<ResponseObject<UserModel>>
    val validateResponse:MutableLiveData<ResponseObject<String>>
    val updateResponse:MutableLiveData<ResponseObject<List<AppModel>>>

    var emailStr:String = ""
    var passwordStr:String = ""
    var usernameStr:String = ""
    var passwordReStr:String = ""
    val visib:MutableLiveData<Int> = MutableLiveData<Int>().also{ it.value = View.GONE }
    val btnText:MutableLiveData<String> = MutableLiveData<String>().also{ it.value = accountCallback.getStr(R.string.sign_in) }
    private var signIn:Boolean

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        signUpResponse = MutableLiveData()
        signInResponse = MutableLiveData()
        validateResponse = MutableLiveData()
        updateResponse = MutableLiveData()
        signIn = true
    }


    fun btnClick(view:View)
    {
        if (signIn)
        {
            if (Utilities.validateEmail(emailStr))
            {
                if (Utilities.validatePassword(passwordStr))
                {
                    accountCallback.signIn(emailStr,passwordStr)
                }else
                {
                    accountCallback.onMessage("رمز عبور حداقل ۸ رقم می باشد")
                }
            }else
            {
                accountCallback.onMessage("ایمیل وارد شده صحیح نیست");
            }
        }else
        {
            if (Utilities.validateEmail(emailStr))
            {
                if (usernameStr.length > 2)
                {
                    if (Utilities.validatePassword(passwordStr))
                    {
                        if (passwordStr.equals(passwordReStr))
                        {
                            accountCallback.signUp(emailStr, passwordStr, usernameStr, PrefManager.getToken()!!)
                        } else
                        {
                            accountCallback.onMessage("رمز عبور با تکرار رمز عبور همخوانی ندارد")
                        }
                    } else
                    {
                        accountCallback.onMessage("رمز عبور حداقل ۸ رقم می باشد")
                    }
                }else
                {
                    accountCallback.onMessage("نام کاربری وارد شده صحیح نیست . نام کاربری حداقل دو کارکتر می باشد")
                }
            }else
            {
                accountCallback.onMessage("ایمیل وارد شده صحیح نیست");
            }
        }
    }


    fun signClick(view:View)
    {
        if (signIn)
        {
            signIn = false
            val str:SpannableString = SpannableString(accountCallback.getStr(R.string.sign_in_alt))
            str.setSpan(UnderlineSpan(),0,str.length,0)
            (view as AppCompatTextView).text = str
            btnText.value = accountCallback.getStr(R.string.sign_up)
            visib.value = View.VISIBLE
            emailStr = ""
            passwordStr = ""
        }else
        {
            signIn = true
            val str:SpannableString = SpannableString(accountCallback.getStr(R.string.sign_up_alt))
            str.setSpan(UnderlineSpan(),0,str.length,0)
            (view as AppCompatTextView).text = str
            btnText.value = accountCallback.getStr(R.string.sign_in)
            visib.value = View.GONE
        }
    }


    fun signUp(email:String, password:String, username:String,token:String)
    {
        apiCaller.signUpUser(email,password,username,token,object : SingleObserver<ResponseObject<UserModel>>
        {
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


    fun signIn(email:String, password:String)
    {
        apiCaller.signInUser(email,password,object : SingleObserver<ResponseObject<UserModel>>
        {
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


    fun validateUser(access:String)
    {
        apiCaller.validateUser(access,object : SingleObserver<ResponseObject<String>>
        {
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


    fun update(offset:Int, packages:List<JSONObject>, isUser:Boolean)
    {
        apiCaller.getUpdates(offset,packages,object : SingleObserver<ResponseObject<List<AppModel>>>
        {
            override fun onSubscribe(d: Disposable?) {
                if (!isUser)
                {
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
                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!"+e?.message)
            }

        })
    }


    fun getSignUpResponse(email:String, password:String, username:String,token:String) : MutableLiveData<ResponseObject<UserModel>>
    {
        signUp(email, password, username, token)
        return signUpResponse
    }


    public fun getSignInResponse(email:String, password:String) : MutableLiveData<ResponseObject<UserModel>>
    {
        signIn(email, password)
        return signInResponse
    }


    public fun getValidateResponse(access: String) : MutableLiveData<ResponseObject<String>>
    {
        validateUser(access)
        return validateResponse
    }


    fun getUpdateResponse(offset: Int, packages: List<JSONObject>, isUser: Boolean) : MutableLiveData<ResponseObject<List<AppModel>>>
    {
        update(offset, packages, isUser)
        return updateResponse
    }

}