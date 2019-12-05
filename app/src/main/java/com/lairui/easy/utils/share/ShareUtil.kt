package com.lairui.easy.utils.share


import android.content.Context
import android.graphics.Bitmap
import android.util.Log


import com.lairui.easy.ui.temporary.activity.AboutUsActivity

import java.util.HashMap

import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback

/**
 *
 */
object ShareUtil {

    fun showShare(mContext: Context, title: String, text: String, url: String) {
        val oks = OnekeyShare()
        /*oks.addHiddenPlatform(QQ.NAME);
        oks.setImageData();
        oks.setSilent(true);*/
        oks.disableSSOWhenAuthorize()
        oks.shareContentCustomizeCallback = ShareContentCustomizeCallback { platform, paramsToShare ->
            if ("SinaWeibo" == platform.name) {
                paramsToShare.text = "玩美夏日，护肤也要肆意玩酷！" + "www.mob.com"
                paramsToShare.imageUrl = "https://hmls.hfbank.com.cn/hfapp-api/9.png"
                /*paramsToShare.setFilePath(ResourcesManager.getInstace(MobSDK.getContext()).getFilePath());*/
                /*paramsToShare.setUrl("http://sharesdk.cn");*/
            }
            if ("Wechat" == platform.name) {
                paramsToShare.title = title
                paramsToShare.text = text
                /*paramsToShare.setWxUserName("");
                    paramsToShare.setW*/
                /*Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                    paramsToShare.setImageData(imageData);*/
                //paramsToShare.setImageUrl("http://scene3d.4dage.com/images/imagesZrbrfZzI/thumbSmallImg.jpg?m=7");
                paramsToShare.url = url
                paramsToShare.shareType = Platform.SHARE_WEBPAGE
                Log.d("ShareSDK", paramsToShare.toMap().toString())
                //Toast.makeText(MainActivity.this, "点击微信分享啦", Toast.LENGTH_SHORT).show();
            }
            if ("WechatMoments" == platform.name) {
                paramsToShare.title = title
                paramsToShare.text = text
                //paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                paramsToShare.url = url
                paramsToShare.shareType = Platform.SHARE_WEBPAGE
            }
            if ("QQ" == platform.name) {
                paramsToShare.title = title
                paramsToShare.titleUrl = url
                paramsToShare.text = text
                //paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
            }
            if ("Facebook" == platform.name) {
                paramsToShare.text = "我是共用的参数，这几个平台都有text参数要求，提取出来啦"
                paramsToShare.imageUrl = "https://hmls.hfbank.com.cn/hfapp-api/9.png"
            }
            if ("Twitter" == platform.name) {
                paramsToShare.text = "我是共用的参数，这几个平台都有text参数要求，提取出来啦"
                paramsToShare.imageUrl = "https://hmls.hfbank.com.cn/hfapp-api/9.png"
                /*paramsToShare.setUrl("http://sharesdk.cn");*/
            }
            if ("ShortMessage" == platform.name) {
                paramsToShare.text = text + "\n" + url
                paramsToShare.title = title
            }
            if ("Email" == platform.name) {
                paramsToShare.text = text + "\n" + url
                paramsToShare.title = title
            }
        }

        oks.callback = object : PlatformActionListener {
            override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
                Log.d("ShareLogin", "onComplete ---->  分享成功")
                platform.name
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                Log.d("ShareLogin", "onError ---->  失败" + throwable.stackTrace)
                Log.d("ShareLogin", "onError ---->  失败" + throwable.message)
                Log.d("ShareLogin", "onError ---->  失败$i")
                Log.d("ShareLogin", "onError ---->  失败$platform")
                throwable.printStackTrace()
            }

            override fun onCancel(platform: Platform, i: Int) {
                Log.d("ShareLogin", "onCancel ---->  分享取消")
            }
        }

        // 启动分享GUI
        oks.show(mContext)
    }


    fun weXinShare(text: String, title: String, url: String, bitmap: Bitmap, imgUrl: String, platformActionListener: AboutUsActivity.MyPlatformActionListener) {
        /* Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setText(text);
        shareParams.setTitle(title);
        shareParams.setUrl(url);
        shareParams.setImageData(bitmap);
        shareParams.setImageUrl(imgUrl);
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        platform.setPlatformActionListener(platformActionListener);*/
        /*platform.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				Log.d("ShareLogin", "onComplete ---->  分享成功");
				platform.getName();
			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {
				Log.d("ShareLogin", "onError ---->  失败" + throwable.getStackTrace());
				Log.d("ShareLogin", "onError ---->  失败" + throwable.getMessage());
				Log.d("ShareLogin", "onError ---->  失败" + i);
				Log.d("ShareLogin", "onError ---->  失败" + platform);
				throwable.printStackTrace();
			}

			@Override
			public void onCancel(Platform platform, int i) {
				Log.d("ShareLogin", "onCancel ---->  分享取消");
			}
		});*/
        //        platform.share(shareParams);
    }

    /* public static void qqShare(String text, String title, String url, String imgUrl, AboutUsActivity.MyPlatformActionListener platformActionListener){
        Platform platform = ShareSDK.getPlatform(QQ.NAME);
        Platform.ShareParams shareParams = new  Platform.ShareParams();
        shareParams.setText(text);
        shareParams.setTitle(title);
        shareParams.setTitleUrl(url);
        shareParams.setImageUrl(imgUrl);
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        platform.setPlatformActionListener(platformActionListener);
		*//*platform.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				Log.d("ShareLogin", "onComplete ---->  分享成功");
				platform.getName();
			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {
				Log.d("ShareLogin", "onError ---->  失败" + throwable.getStackTrace());
				Log.d("ShareLogin", "onError ---->  失败" + throwable.getMessage());
				Log.d("ShareLogin", "onError ---->  失败" + i);
				throwable.printStackTrace();
			}

			@Override
			public void onCancel(Platform platform, int i) {
				Log.d("ShareLogin", "onCancel ---->  分享取消");
			}
		});*//*
        platform.share(shareParams);
    }
*/

    /*public static void shareEmail(){
		Platform platform = ShareSDK.getPlatform(Email.NAME);
		Platform.ShareParams shareParams = new  Platform.ShareParams();
		shareParams.setTitle(ResourcesManager.getInstace(MobSDK.getContext()).getTitle());
		shareParams.setText(ResourcesManager.getInstace(MobSDK.getContext()).getText());
		shareParams.setImagePath(ResourcesManager.getInstace(MobSDK.getContext()).getImagePath());
		shareParams.setImageUrl(ResourcesManager.getInstace(MobSDK.getContext()).getImageUrl());
		platform.setPlatformActionListener(platformActionListener);
		platform.share(shareParams);
	}

	public static void shareSMS(){
		Platform platform = ShareSDK.getPlatform(ShortMessage.NAME);
		Platform.ShareParams shareParams = new  Platform.ShareParams();
		shareParams.setText(ResourcesManager.getInstace(MobSDK.getContext()).getText());
		shareParams.setTitle(ResourcesManager.getInstace(MobSDK.getContext()).getTitle());
		shareParams.setImagePath(ResourcesManager.getInstace(MobSDK.getContext()).getImagePath());
		shareParams.setImageUrl(ResourcesManager.getInstace(MobSDK.getContext()).getImageUrl());
		platform.setPlatformActionListener(platformActionListener);
		platform.share(shareParams);
	}*/


}

