package com.lairui.easy.ui.temporary.activity

import android.widget.Toast

import com.lairui.easy.R
import com.lairui.easy.api.MethodUrl
import com.lairui.easy.basic.BasicActivity
import com.lairui.easy.mvp.view.RequestView
import com.lairui.easy.basic.MbsConstans

import java.util.HashMap

class TestActivity : BasicActivity(), RequestView {
    override val contentView: Int
        get() = R.layout.activity_test

    override fun init() {
        mInstance = this
        /*  Map<String, String> map = new HashMap<>();
        map.put("sort", "desc");
        map.put("page", 1 + "");
        map.put("pagesize", "10");
        map.put("time", new Date().getTime() / 1000 + "");
        map.put("key", "b319ddc604735aaad777d77d46d271e6");


        //weatherPresenterImp.requestPostToMap("c5bb749112664353af44bc99ed263857", "长沙");
        mRequestPresenterImp.requestPostToMap(MethodUrl.imgList, map);




        map.put("sort","desc");
        map.put("page",1+"");
        map.put("pagesize","10");
        map.put("time",new Date().getTime()/1000+"");
        map.put("key","b319ddc604735aaad777d77d46d271e6");*/

        //{
        /*"h2y_app_id": "string",
                "os_pd": {
            "client_version": "string",
                    "os_type": "string",
                    "os_version": "string"
        },
        "pd": {
            "account": "string",
                    "password": "string"
        },
        "token": "string"
    }*/

        /* Map<String,Object> mOsInfo = new HashMap<String,Object>();
        mOsInfo.put("client_version","1");
        mOsInfo.put("os_type","android");
        mOsInfo.put("os_version","7.0");*/

        val mParamInfo = HashMap<String, Any>()
        mParamInfo["account"] = "17621690984"
        mParamInfo["password"] = "123456"
        mParamInfo["os_type"] = MbsConstans.SYS_NAME
        mParamInfo["push_code"] = ""
        mParamInfo["push_id"] = ""


        /* Map<String, Object> map = new HashMap<>();
        map.put("os_pd", mOsInfo);
        map.put("pd", mParamInfo);
        map.put("h2y_app_id", "app");
        map.put("token","");*/

        /* Gson gson = new Gson();
        String jsonStr = gson.toJson(map);

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonStr);*/

        // mRequestPresenterImp.requestPostToMap(new HashMap<String, String>(),MethodUrl.loginAction, mParamInfo);


        /*  map.put("sort","desc");
        map.put("page",1+"");
        map.put("pagesize","10");
        map.put("time",new Date().getTime()/1000+"");
        map.put("key","b319ddc604735aaad777d77d46d271e6");

        mRequestPresenterImp.requestPostToMap("http://japi.juhe.cn/joke/"+MethodUrl.contentList, map);*/
    }


    override fun showProgress() {

    }

    override fun disimissProgress() {

    }

    override fun loadDataSuccess(mData: MutableMap<String, Any>, mType: String) {
        Toast.makeText(this@TestActivity, "madfasdf$mData", Toast.LENGTH_SHORT).show()
        when (mType) {
            MethodUrl.LOGIN_ACTION -> {
            }
        }

    }


    override fun loadDataError(map: MutableMap<String, Any>, mType: String) {
        val msg = map["msg"]!!.toString() + ""
        showToastMsg(msg)
    }

    companion object {

        lateinit var mInstance: TestActivity
    }
}
