package com.lairui.easy.ui.module1.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lairui.easy.R
import com.lairui.easy.basic.BasicRecycleViewAdapter
import java.util.*

class CoinInfoAdapter( mContext: Context) : BasicRecycleViewAdapter(mContext) {
    private var coinList: List<Map<String, Any>>? = null
    fun setList(coinList: List<Map<String, Any>>?) {
        this.coinList = coinList
        notifyDataSetChanged()
    }

    override val itemRes: Int
        protected get() = R.layout.item_coin_info

    fun getCoinList(): List<Map<String, Any>> {
        return if (coinList == null) {
            ArrayList()
        } else coinList!!
    }

    override fun getViewHolder(view: View?): RecyclerView.ViewHolder {
        val metrics = mContext.resources.displayMetrics
        val widthPixels = metrics.widthPixels
        val layoutParams = view!!.layoutParams
        layoutParams.width = widthPixels / 3
        view.layoutParams = layoutParams
        return CoinInfoViewHolder(view)
    }

    override fun bindClickListener(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val coinInfoViewHolder = viewHolder as CoinInfoViewHolder?
        val map: Map<*, *> = coinList!![position]

        coinInfoViewHolder!!.tvCoinName.text = map["name"].toString() + ""
        coinInfoViewHolder.tvCoinPrice.text = map["price"].toString() + ""

        if (map["ratio"].toString().contains("-") || map["amount"].toString().contains("-")){
            coinInfoViewHolder.tvCoinConvert.text = map["amount"].toString() + ""
            coinInfoViewHolder.tvCoinRatio.text = map["ratio"].toString() + "%"
        }else{
            coinInfoViewHolder.tvCoinConvert.text ="+"+ map["amount"].toString() + ""
            coinInfoViewHolder.tvCoinRatio.text = "+"+map["ratio"].toString() + "%"
        }

        //coinInfoViewHolder.tvCoinConvert.setText(UiTools.getString(R.string.defaultCny).replace("%S", frontBean.getCnyNumber()));
/* if (UtilTools.empty(MbsConstans.COLOR_LOW) || UtilTools.empty(MbsConstans.COLOR_TOP)){
            //0 红跌绿涨   1红涨绿跌
            String colorType =  SPUtils.get(mContext, MbsConstans.SharedInfoConstans.COLOR_TYPE,"0").toString();
            if (colorType.equals("0")){
                MbsConstans.COLOR_LOW = MbsConstans.COLOR_RED;
                MbsConstans.COLOR_TOP = MbsConstans.COLOR_GREEN;
            }else {
                MbsConstans.COLOR_LOW = MbsConstans.COLOR_GREEN;
                MbsConstans.COLOR_TOP = MbsConstans.COLOR_RED;

            }
        }*/
/* if ((map.get("increase") + "").contains("-")) {
            coinInfoViewHolder.tvCoinRatio.setText(map.get("increase") + "");

            coinInfoViewHolder.tvCoinPrice.setTextColor(Color.parseColor(MbsConstans.COLOR_LOW));
            coinInfoViewHolder.tvCoinRatio.setTextColor(Color.parseColor(MbsConstans.COLOR_LOW));
        } else {
            coinInfoViewHolder.tvCoinRatio.setText("" +map.get("increase"));
            coinInfoViewHolder.tvCoinPrice.setTextColor(Color.parseColor(MbsConstans.COLOR_TOP));
           coinInfoViewHolder.tvCoinRatio.setTextColor(Color.parseColor(MbsConstans.COLOR_TOP));
        }*/
    }

    override fun getItemCount(): Int {
        return if (coinList != null && coinList!!.size > 0) {
            coinList!!.size
        } else 0
    }

    internal class CoinInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCoinName: TextView
        val tvCoinPrice: TextView
        val tvCoinRatio: TextView
        val tvCoinConvert: TextView

        init {
            tvCoinName = itemView.findViewById(R.id.tvCoinName)
            tvCoinPrice = itemView.findViewById(R.id.tvCoinPrice)
            tvCoinRatio = itemView.findViewById(R.id.tvCoinRatio)
            tvCoinConvert = itemView.findViewById(R.id.tvCoinConvert)
        }
    }

}