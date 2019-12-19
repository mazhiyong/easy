package com.lairui.easy.ui.module5.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.lairui.easy.R
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.utils.imageload.GlideUtils
import com.lairui.easy.utils.imageload.GlideUtils.Companion.loadImage
import com.lairui.easy.utils.tool.JSONUtil
import com.lairui.easy.utils.tool.SPUtils.get
import com.lairui.easy.utils.tool.UtilTools.Companion.empty
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/**
 * @author :smile
 */
class ChatAdapter(context: Context, msgs: MutableList<Map<String, Any>>?) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    //文本
    private val TYPE_RECEIVER_TXT = 0
    private val TYPE_SEND_TXT = 1
    /**
     * 显示时间间隔:10分钟
     */
    private val TIME_INTERVAL = 10 * 60 * 1000.toLong()
    private var msgs: MutableList<Map<String, Any>>? = ArrayList()
    private val context: Context
    val count: Int
        get() = if (msgs == null) 0 else msgs!!.size

    fun addMessages(messages: List<Map<String, Any>>?) {
        msgs!!.addAll(0, messages!!)
        notifyDataSetChanged()
    }

    fun addMessage(message: Map<String, Any>) {
        msgs!!.add(message)
        //notifyDataSetChanged();
    }

    /**获取消息
     * @param position
     * @return
     */
    fun getItem(position: Int): Map<String, Any>? {
        return if (msgs == null) null else if (position >= msgs!!.size) null else msgs!![position]
    }

    /**移除消息
     * @param position
     */
    fun remove(position: Int) {
        msgs!!.removeAt(position)
        notifyDataSetChanged()
    }

    val firstMessage: Map<String, Any>?
        get() = if (null != msgs && msgs!!.size > 0) {
            msgs!![0]
        } else {
            null
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SEND_TXT) {
            SendTextHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_sent_message, parent, false))
        } else {
            ReceiveTextHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_received_message, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReceiveTextHolder) { //((ReceiveTextHolder)holder).showTime(shouldShowTime(position));
            holder.tv_message!!.text = msgs!![position]["text"].toString() + ""
            if (empty(msgs!![position]["img"])) {
                if (msgs!![position]["kind"].toString() + "" == "0") {
                    holder.iv_avatar!!.setImageResource(R.drawable.man)
                } else {
                    holder.iv_avatar!!.setImageResource(R.drawable.woman)
                }
            } else {
                loadImage(context, msgs!![position]["img"].toString() + "", holder.iv_avatar!!)
            }
        } else { //((SendTextHolder)holder).showTime(shouldShowTime(position));
/* if (UtilTools.empty(msgs.get(position).get("img"))){
                if (UtilTools.empty(MbsConstans.USER_MAP)) {
                    String s = SPUtils.get(context, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString();
                    MbsConstans.USER_MAP = JSONUtil.getInstance().jsonMap(s);
                }
                GlideUtils.loadImage2(context, MbsConstans.USER_MAP.get("portrait") + "",((SendTextHolder) holder).iv_avatar , R.drawable.default_headimg);
            }else {
                GlideUtils.loadImage2(context, msgs.get(position).get("img") + "",((SendTextHolder) holder).iv_avatar , R.drawable.default_headimg);
            }*/
//用户头像
            if (empty(MbsConstans.USER_MAP)) {
                val s = get(context, MbsConstans.SharedInfoConstans.LOGIN_INFO, "").toString()
                MbsConstans.USER_MAP = JSONUtil.instance.jsonMap(s)
            }
            GlideUtils.loadImage2(context, MbsConstans.USER_MAP!!["portrait"].toString() + "", (holder as SendTextHolder?)!!.iv_avatar!!, R.drawable.default_headimg)
            holder.tv_message.text = msgs!![position]["text"].toString() + ""
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = msgs!![position]
        return if (message["status"].toString() + "" == "0") {
            TYPE_RECEIVER_TXT
        } else {
            TYPE_SEND_TXT
        }
    }

    override fun getItemCount(): Int {
        return msgs!!.size
    }

    private fun shouldShowTime(position: Int): Boolean {
        return position == 0
        //long lastTime = msgs.get(position - 1).getCreateTime();
//long curTime = msgs.get(position).getCreateTime();
//return curTime - lastTime > TIME_INTERVAL;
    }

    internal inner class SendTextHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var iv_avatar: CircleImageView = itemView!!.findViewById(R.id.iv_avatar)

        var iv_fail_resend: ImageView = itemView!!.findViewById(R.id.iv_fail_resend)

        var tv_time: TextView = itemView!!.findViewById(R.id.tv_time)

        var tv_message: TextView = itemView!!.findViewById(R.id.tv_message)

        var tv_send_status: TextView = itemView!!.findViewById(R.id.tv_send_status)

        var progress_load: ProgressBar = itemView!!.findViewById(R.id.progress_load)
        private val mContext: Context? = null

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    internal inner class ReceiveTextHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        @BindView(R.id.iv_avatar)
        lateinit var iv_avatar: CircleImageView
        @BindView(R.id.tv_time)
        lateinit var tv_time: TextView
        @BindView(R.id.tv_message)
        lateinit var tv_message: TextView

        init {
            ButterKnife.bind(this, itemView!!)
        }
    }

    init {
        this.msgs = msgs
        this.context = context
    }

}