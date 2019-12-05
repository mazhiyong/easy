package com.lairui.easy.utils.tool

import android.content.Context

import com.lairui.easy.basic.BasicApplication
import com.lairui.easy.basic.MbsConstans
import com.lairui.easy.mywidget.view.TipsToast

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.HashMap
import kotlin.collections.MutableList as MutableList1

object SelectDataUtil {

    private var mNameCode: MutableMap<String, Any>? = HashMap()

    //（top_up：充值，withdraw：提现，borrow：借款，repayment：还款，other：其他）
    val condition: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["code"] = "borrow"
            map["name"] = "借款"
            list.add(map)
            map = HashMap()
            map["code"] = "repayment"
            map["name"] = "还款"
            list.add(map)
            map = HashMap()
            map["code"] = "top_up"
            map["name"] = "充值"
            list.add(map)
            map = HashMap()
            map["code"] = "withdraw"
            map["name"] = "提现"
            list.add(map)
            map = HashMap()
            map["code"] = "other"
            map["name"] = "其他"
            list.add(map)

            return list
        }

    //国籍(CN:中国,HK:香港,MO:澳门,TW:台湾省,OTHER:其他)
    val country: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "中国"
            map["code"] = "CN"
            list.add(map)

            map = HashMap()
            map["name"] = "香港"
            map["code"] = "HK"
            list.add(map)

            map = HashMap()
            map["name"] = "澳门"
            map["code"] = "MO"
            list.add(map)

            map = HashMap()
            map["name"] = "台湾省"
            map["code"] = "TW"
            list.add(map)

            map = HashMap()
            map["name"] = "其他"
            map["code"] = "OTHER"
            list.add(map)
            return list
        }
    //婚姻状况(1:单身,2:已婚,3:离婚,4:丧偶)
    val marry: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "未婚"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "已婚"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "离婚"
            map["code"] = "3"
            list.add(map)

            map = HashMap()
            map["name"] = "丧偶"
            map["code"] = "4"
            list.add(map)

            return list
        }
    //学历(1:小学程度或以下,2:中学程度,3:预科/大专程度,4:学士,5:硕士或以上)
    val education: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "小学程度或以下"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "中学程度"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "预科/大专程度"
            map["code"] = "3"
            list.add(map)

            map = HashMap()
            map["name"] = "学士"
            map["code"] = "4"
            list.add(map)

            map = HashMap()
            map["name"] = "硕士或以上"
            map["code"] = "5"
            list.add(map)

            return list
        }
    //现居所有权(1:自置（无抵押）,2:已按揭,3:亲属拥有,4:由雇主提供,5:租用,6:其它)
    val house: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "自置（无抵押）"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "已按揭"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "亲属拥有"
            map["code"] = "3"
            list.add(map)

            map = HashMap()
            map["name"] = "由雇主提供"
            map["code"] = "4"
            list.add(map)

            map = HashMap()
            map["name"] = "租用"
            map["code"] = "5"
            list.add(map)

            map = HashMap()
            map["name"] = "其它"
            map["code"] = "6"
            list.add(map)

            return list
        }
    //工作性质(1:长期雇员,2:合约员工,3:非在职人士/临时工)
    val jobType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "长期雇员"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "合约员工"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "非在职人士/临时"
            map["code"] = "3"
            list.add(map)

            return list
        }
    //还款周期 1：一次性 2：单周 3：双周 4：月 5：季 6：半年 7：年 8：其他
    val hkZhouqi: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "一次性"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "单周"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "双周"
            map["code"] = "3"
            list.add(map)

            map = HashMap()
            map["name"] = "月"
            map["code"] = "4"
            list.add(map)

            map = HashMap()
            map["name"] = "季"
            map["code"] = "5"
            list.add(map)

            map = HashMap()
            map["name"] = "半年"
            map["code"] = "6"
            list.add(map)

            map = HashMap()
            map["name"] = "年"
            map["code"] = "7"
            list.add(map)

            map = HashMap()
            map["name"] = "其他"
            map["code"] = "8"
            list.add(map)

            return list
        }
    //利率方式 0：浮动 1：固定
    val lilvType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "浮动"
            map["code"] = "0"
            list.add(map)

            map = HashMap()
            map["name"] = "固定"
            map["code"] = "1"
            list.add(map)


            return list
        }
    //贷款种类 202010：个人-经营性贷款；101010：企业-流动资金贷款
    val daikuanType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "个人-经营性贷款"
            map["code"] = "202010"
            list.add(map)

            map = HashMap()
            map["name"] = "企业-流动资金贷款"
            map["code"] = "101010"
            list.add(map)

            return list
        }
    //贷款用途 0：个人经营 1：个人授信额度服务 2：个人综合消费 3：商品交易 4：资金周转
    val daikuanUse: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "个人经营"
            map["code"] = "0"
            list.add(map)

            map = HashMap()
            map["name"] = "个人授信额度服务"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "个人综合消费"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "商品交易"
            map["code"] = "3"
            list.add(map)

            map = HashMap()
            map["name"] = "资金周转"
            map["code"] = "4"
            list.add(map)
            return list
        }

    /**
     * 单笔借款期限单位(1:年,2:月,3:日)
     * @return
     */
    //单笔借款期限单位(1:年,2:月,3:日)
    val qixianDw: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "年"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "月"
            map["code"] = "2"
            list.add(map)

            map = HashMap()
            map["name"] = "日"
            map["code"] = "3"
            list.add(map)

            return list
        }

    //-------------------------------------------预授信用到的--------------------------------------------------------------
    //
    //还款方式( 1：利随本清 2：分期付息，到期还本，3：分期还本，分期付息，RPT-05：等额本息，RPT-06 等额本金
    /*  map = new HashMap<>();
        map.put("name","分期还本，分期付息");
        map.put("code","3");
        list.add(map);*/ val hkType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "利随本清"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "分期付息，到期还本"
            map["code"] = "2"
            list.add(map)
            map = HashMap()
            map["name"] = "等额本息"
            map["code"] = "RPT-05"
            list.add(map)
            map = HashMap()
            map["name"] = "等额本金"
            map["code"] = "RPT-06"
            list.add(map)

            return list
        }
    //
    //担保类型 0:保证担保;1:信用;2:差额回购;3:优先权处置;4:差额退款;5:应收账款转让;6:应收账款质押;7:代偿;8:质押;9:无担保
    val danbaoType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "保证担保"
            map["code"] = "0"
            list.add(map)
            map = HashMap()
            map["name"] = "信用"
            map["code"] = "1"
            list.add(map)
            map = HashMap()
            map["name"] = "差额回购"
            map["code"] = "2"
            list.add(map)
            map = HashMap()
            map["name"] = "优先权处置"
            map["code"] = "3"
            list.add(map)
            map = HashMap()
            map["name"] = "差额退款"
            map["code"] = "4"
            list.add(map)
            map = HashMap()
            map["name"] = "应收账款转让"
            map["code"] = "5"
            list.add(map)
            map = HashMap()
            map["name"] = "应收账款质押"
            map["code"] = "6"
            list.add(map)
            map = HashMap()
            map["name"] = "代偿"
            map["code"] = "7"
            list.add(map)
            map = HashMap()
            map["name"] = "质押"
            map["code"] = "8"
            list.add(map)
            map = HashMap()
            map["name"] = "无担保"
            map["code"] = "9"
            list.add(map)

            return list
        }
    //
    //有无追索权(0无;1有)
    val hasZhuisuo: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "无"
            map["code"] = "0"
            list.add(map)

            map = HashMap()
            map["name"] = "有"
            map["code"] = "1"
            list.add(map)

            return list
        }
    //
    //保理类型(1明保理;2暗保理)
    val baoliType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "明保理"
            map["code"] = "1"
            list.add(map)

            map = HashMap()
            map["name"] = "暗保理"
            map["code"] = "2"
            list.add(map)

            return list
        }
    //
    //出账品种(0:流动资金贷款 1：银行承兑)
    val chuzhangType: List<MutableMap<String, Any>>
        get() {
            val list = ArrayList<MutableMap<String, Any>>()
            var map: MutableMap<String, Any> = HashMap()
            map["name"] = "流动资金贷款"
            map["code"] = "0"
            list.add(map)

            map = HashMap()
            map["name"] = "银行承兑"
            map["code"] = "1"
            list.add(map)

            return list
        }


    val nameCodeData: MutableMap<String, Any>?
        get() {
            if (mNameCode == null || mNameCode!!.isEmpty()) {
                val mapData = HashMap<String, Any>()
                val nameCode = SPUtils.get(BasicApplication.context!!, MbsConstans.SharedInfoConstans.NAME_CODE_DATA, "")!!.toString() + ""
                if (!UtilTools.empty(nameCode)) {
                    mNameCode = JSONUtil.instance.jsonMap(nameCode)
                } else {
                    TipsToast.showToastMsg("配置文件丢失，请手动退出重新登录")
                }
                return mNameCode
            } else {
                return mNameCode
            }
        }


    //还款账户类型
    //账号主体（0：个人，1：企业）
    val hankuanAccountType: List<MutableMap<String, Any>>
        get() {
            val mDataList = ArrayList<MutableMap<String, Any>>()
            var map1: MutableMap<String, Any> = HashMap()

            map1["name"] = "结算账户还款"
            map1["code"] = "1"
            mDataList.add(map1)

            map1 = HashMap()
            map1["name"] = "资金账户还款"
            map1["code"] = "2"
            mDataList.add(map1)


            return mDataList
        }

    ////账户主体类型
    //账号主体（0：个人，1：企业）
    val registerType: List<MutableMap<String, Any>>
        get() {
            val mDataList = ArrayList<MutableMap<String, Any>>()
            var map1: MutableMap<String, Any> = HashMap()

            map1["name"] = "个人"
            map1["code"] = "0"
            mDataList.add(map1)

            map1 = HashMap()
            map1["name"] = "企业"
            map1["code"] = "1"
            mDataList.add(map1)


            return mDataList
        }

    //读取方法
    fun getJson(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager = context.assets
            val bf = BufferedReader(InputStreamReader(assetManager.open(fileName)))
            var line: String
            do {
                line = bf.readLine()
                if (line != null){
                    stringBuilder.append(line)
                }
            }while (true)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }

    fun guanxiPeople(): List<MutableMap<String, Any>> {
        //共同借款人与我的关系 0：配偶 1：父母 2：子女 3：其他
        val list = ArrayList<MutableMap<String, Any>>()
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "配偶"
        map["code"] = "0"
        list.add(map)

        map = HashMap()
        map["name"] = "父母"
        map["code"] = "1"
        list.add(map)

        map = HashMap()
        map["name"] = "子女"
        map["code"] = "2"
        list.add(map)

        map = HashMap()
        map["name"] = "其他"
        map["code"] = "3"
        list.add(map)

        return list
    }

    fun jieKuanStatus(): List<MutableMap<String, Any>> {
        //借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）
        val list = ArrayList<MutableMap<String, Any>>()
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "放款中"
        map["code"] = "1"
        list.add(map)

        map = HashMap()
        map["name"] = "已放款"
        map["code"] = "2"
        list.add(map)

        map = HashMap()
        map["name"] = "已结清"
        map["code"] = "3"
        list.add(map)
        map = HashMap()
        map["name"] = "已驳回"
        map["code"] = "4"
        list.add(map)

        return list
    }


    //应收凭证状态
    fun pingZhengStatus(): List<MutableMap<String, Any>> {
        //借款状态（1：放款中 2：还款中 3：已结清 4：已驳回）
        val list = ArrayList<MutableMap<String, Any>>()
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "正常"
        map["code"] = "1"
        list.add(map)

        map = HashMap()
        map["name"] = "已融资"
        map["code"] = "2"
        list.add(map)

        map = HashMap()
        map["name"] = "已核销"
        map["code"] = "3"
        list.add(map)
        map = HashMap()
        map["name"] = "已到期"
        map["code"] = "4"
        list.add(map)

        return list
    }

    fun jieKuanLimit(): List<MutableMap<String, Any>> {
        //借款期限（1个月、3个月、6个月、12个月、24个月、36个月、50个月）
        val list = ArrayList<MutableMap<String, Any>>()
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "1个月"
        map["code"] = "1"
        list.add(map)

        map = HashMap()
        map["name"] = "3个月"
        map["code"] = "3"
        list.add(map)

        map = HashMap()
        map["name"] = "6个月"
        map["code"] = "6"
        list.add(map)

        map = HashMap()
        map["name"] = "9个月"
        map["code"] = "9"
        list.add(map)

        map = HashMap()
        map["name"] = "12个月"
        map["code"] = "12"
        list.add(map)
        map = HashMap()
        map["name"] = "24个月"
        map["code"] = "24"
        list.add(map)
        map = HashMap()
        map["name"] = "36个月"
        map["code"] = "36"
        list.add(map)
        map = HashMap()
        map["name"] = "50个月"
        map["code"] = "50"
        list.add(map)

        return list
    }

    fun gongsi(): List<MutableMap<String, Any>> {
        //账户类型(1: 对公; 2: 对私
        val list = ArrayList<MutableMap<String, Any>>()
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "对公"
        map["code"] = "1"
        list.add(map)

        map = HashMap()
        map["name"] = "对私"
        map["code"] = "2"
        list.add(map)

        return list
    }

    fun getMaxQixian(max: Int, type: String): List<MutableMap<String, Any>> {//
        val maxList = ArrayList<MutableMap<String, Any>>()
        var list = jieKuanLimit()
        when (type) {
            "1"//借款期限单位（1：年 2：月 3：日）
            -> list = qixianNian()
            "2" -> list = SelectDataUtil.getNameCodeByType("loanLimit")
            "3" -> list = ArrayList()
        }
        for (map in list) {
            val code = map["code"]!!.toString() + ""
            val i = Integer.valueOf(code)
            if (i <= max) {
                maxList.add(map)
            }
        }
        return maxList
    }

    fun qixianNian(): List<MutableMap<String, Any>> {//
        //1年  2年  3年
        val list = ArrayList<MutableMap<String, Any>>()
        var map: MutableMap<String, Any> = HashMap()
        map["name"] = "1年"
        map["code"] = "1"
        list.add(map)

        map = HashMap()
        map["name"] = "2年"
        map["code"] = "2"
        list.add(map)

        map = HashMap()
        map["name"] = "3年"
        map["code"] = "3"
        list.add(map)

        return list
    }


    fun getMap(code: String, list: List<MutableMap<String, Any>>): MutableMap<String, Any> {

        val empty = HashMap<String, Any>()
        empty["name"] = ""
        for (map in list) {
            val c = map["code"]!!.toString() + ""
            if (code == c) {
                return map
            }
        }
        return empty
    }

    //  //根据name 获取相应的map  例如 "loanState":[{"1":"放款中"},{"2":"已放款"},{"3":"已结清"},{"4":"已驳回"},{"5":"已撤销"}]
    fun getMapByKey(key: String, list: List<MutableMap<String, Any>>): MutableMap<String, Any> {

        val empty = HashMap<String, Any>()
        empty[key] = ""
        for (map in list) {
            if (map.containsKey(key)) {
                return map
            }
        }
        return empty
    }

    //根据code 获取name
    fun getNameCodeByType(keyType: String): List<MutableMap<String, Any>> {
        val mapData = nameCodeData
        var mTypeList: MutableList1<MutableMap<String, Any>> = ArrayList()
        if (mapData != null && !mapData.isEmpty()) {
            mTypeList = mapData[keyType] as MutableList1<MutableMap<String, Any>>
        }
        return mTypeList
    }


    fun getListByKeyList(list: List<MutableMap<String, Any>>?): List<MutableMap<String, Any>> {
        val list1 = ArrayList<MutableMap<String, Any>>()

        if (list == null || list.size == 0) {

            return list1
        }
        for (map in list) {
            val resultMap = HashMap<String, Any>()
            for (key in map.keys) {
                resultMap["code"] = key
                resultMap["name"] = map[key]!!
            }
            list1.add(resultMap)
        }
        return list1
    }


}
