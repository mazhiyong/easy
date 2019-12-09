package com.lairui.easy.ui.module5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jaeger.library.StatusBarUtil;
import com.lairui.easy.R;
import com.lairui.easy.api.MethodUrl;
import com.lairui.easy.basic.BasicActivity;
import com.lairui.easy.basic.MbsConstans;
import com.lairui.easy.listener.SoftKeyBoardListener;
import com.lairui.easy.mvp.view.RequestView;
import com.lairui.easy.ui.module.activity.LoginActivity;
import com.lairui.easy.ui.module5.adapter.ChatAdapter;
import com.lairui.easy.utils.tool.SPUtils;
import com.lairui.easy.utils.tool.UtilTools;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class KefuChatActivity extends BasicActivity implements RequestView {
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.back_text)
    TextView backText;
    @BindView(R.id.left_back_lay)
    LinearLayout leftBackLay;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.right_text_tv)
    TextView rightTextTv;
    @BindView(R.id.right_img)
    ImageView rightImg;
    @BindView(R.id.right_lay)
    LinearLayout rightLay;
    @BindView(R.id.divide_line)
    View divideLine;
    @BindView(R.id.rc_view)
    RecyclerView rcView;
    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout swRefresh;
    @BindView(R.id.edit_msg)
    EditText editMsg;
    @BindView(R.id.btn_chat_send)
    Button btnChatSend;
    @BindView(R.id.ll_chat)
    LinearLayout llChat;

    private ChatAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Map<String, Object>> mapList = new ArrayList<>();
    private List<Map<String, Object>> mapReceiveList = new ArrayList<>();
    private List<Map<String, Object>> mapOldReceiveList = new ArrayList<>();
    private String kind ="";


    private Handler handler2 = new Handler();

    //HTTP请求  轮询
    private Runnable cnyRunnable = new Runnable() {
        @Override
        public void run() {
            // 轮询回复消息
            obtainMsgAction();
            handler2.postDelayed(this, MbsConstans.SECOND_TIME_15);
        }
    };

    private void obtainMsgAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("type",kind);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        //mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_RECEIVER, map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler2.post(cnyRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler2 != null && cnyRunnable != null) {
            handler2.removeCallbacks(cnyRunnable);
        }
    }


    @Override
    public int getContentView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        return R.layout.activity_chat;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);

        titleText.setCompoundDrawables(null, null, null, null);
        divideLine.setVisibility(View.GONE);
        initView();
        titleText.setText("在线客服");

        //查询聊天记录
        getChatRecordAction();
    }

    private void getChatRecordAction() {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(KefuChatActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("type",kind);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
       // mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_RECORD, map);

    }

    private void initView() {
        swRefresh.setEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        rcView.setLayoutManager(layoutManager);
        /*for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<>();
            if (i % 2 == 0) {
                map.put("type", "0");
                map.put("content", "你好吗");
            } else {
                map.put("type", "1");
                map.put("content", "好你妹");
            }
            mapList.add(map);

        }*/
        adapter = new ChatAdapter(KefuChatActivity.this, mapList);
        rcView.setAdapter(adapter);
        llChat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llChat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                swRefresh.setRefreshing(false);
                //自动刷新
                //queryMessages(null);
            }
        });

        //下拉刷新
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //查询聊天记录
                getChatRecordAction();

            }
        });

        SoftKeyBoardListener.Companion.setListener(KefuChatActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //键盘显示
                scrollToBottom();
            }

            @Override
            public void keyBoardHide(int height) {
                //键盘隐藏
            }
        });
       /* editMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });*/



        editMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    btnChatSend.setEnabled(true);
                } else {
                    btnChatSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }



    @OnClick({R.id.left_back_lay, R.id.btn_chat_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_back_lay:
                finish();
                break;
            case R.id.btn_chat_send:
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(editMsg, 0);
                String text = editMsg.getText().toString();
                sendMsgAction(text);
                break;
        }
    }

    private void sendMsgAction(String content) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(KefuChatActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("cont",content);
        map.put("type",kind);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
       // mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_SEND, map);

    }

    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
/*
        switch (mType){
            case MethodUrl.CHAT_SEND:
                switch (tData.get("code") + "") {
                    case "0": //请求成功
                        Map<String, Object> map = new HashMap<>();
                        map.put("kind",kind);
                        map.put("status", "1");
                        map.put("text", editMsg.getText().toString());
                        adapter.addMessage(map);
                        scrollToBottom();
                        adapter.notifyItemChanged(adapter.getCount());
                        //adapter.notifyDataSetChanged();
                        editMsg.setText("");
                        editMsg.requestFocus();
                        break;
                    case "-1": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "1": //token过期
                        closeAllActivity();
                        Intent intent = new Intent(KefuChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                break;
            case MethodUrl.CHAT_RECEIVER:
                switch (tData.get("code") + "") {
                    case "0": //请求成功
                        if (!UtilTools.empty(tData.get("data")+"")){
                            List<Map<String,Object>> list = (List<Map<String, Object>>) tData.get("data");
                            if (!UtilTools.empty(list) && list.size()>0){
                                mapReceiveList.clear();
                                for (Map<String,Object> map1:list){
                                    if ((map1.get("status")+"").equals("0")){ //收到的消息
                                        Log.i("show","receiver:"+map1.get("text"));
                                        mapReceiveList.add(map1);
                                    }
                                }
                                if (mapOldReceiveList.size() == 0){//首次轮询
                                    mapOldReceiveList.addAll(mapReceiveList);
                                    return;
                                }

                                int number = mapReceiveList.size() - mapOldReceiveList.size();
                                if (number>0){
                                    for (int i = mapOldReceiveList.size(); i < mapReceiveList.size(); i++) {
                                        Log.i("show","&&&&&&&&&&&&&&:"+mapReceiveList.get(i).get("text"));
                                        mapReceiveList.get(i).put("kind",kind);
                                        adapter.addMessage( mapReceiveList.get(i));
                                        scrollToBottom();
                                        adapter.notifyItemChanged(adapter.getCount());
                                        //adapter.notifyDataSetChanged();
                                    }
                                    mapOldReceiveList.clear();
                                    mapOldReceiveList.addAll(mapReceiveList);

                                }

                            }
                        }
                        break;
                    case "-1": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "1": //token过期
                        closeAllActivity();
                        Intent intent = new Intent(KefuChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                break;
            case MethodUrl.CHAT_RECORD:
                switch (tData.get("code") + "") {
                    case "0": //请求成功

                        if (!UtilTools.empty(tData.get("data")+"")){
                            List<Map<String,Object>> list = (List<Map<String, Object>>) tData.get("data");
                            mapList.clear();
                            if (!UtilTools.empty(list) && list.size()>0){
                                for (Map<String,Object> map:list){
                                    map.put("kind",kind);
                                }
                                mapList.addAll(list);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        swRefresh.setRefreshing(false);
                        break;
                    case "-1": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;

                    case "1": //token过期
                        closeAllActivity();
                        Intent intent = new Intent(KefuChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                break;

        }
*/
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {
        dealFailInfo(map,mType);
    }
}
