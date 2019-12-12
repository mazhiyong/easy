package com.lairui.easy.api

/**
 */
object MethodUrl {



    val refreshToken = "token/refresh"//获取刷新的token

    val tempToken = "token"//获取临时Token
    val imageCode = "imgcode"//图形验证码
    val regSms = "sms/reg"//注册短信验证码
    val registerAction = "user/register"//注册提交

    val uploadFile = "upload"//上传文件
    val headPath = "user/uploadheadpic"//上传文件


    val appVersion = "sys/appVersion"//更新app信息
    val shareUrl = "user/codeurl"//注册分享的url
    val userInfo = "user/info"//获取用户基本信息
    val nameCode = "code/all"//获取应用全局字典信息
    val idCardCheck = "verify/ocridcard"//身份证验证
    val idCardSubmit = "verify/idcard"//身份证验证提交
    val liveSubmit = "verify/living"//人脸识别提交
    val userAuthInfo = "verify/authinfo"//用户认证信息
    val laseAuthInfo = "verify/man"// 最近一次认证信息
    val peopleAuth = "verify/man"// 人工认证         //方法名一样，但是提交方式不一样

    val companyCheck = "verify/companyLegal"//企业认证信息提交
    val companyInfo = "verify/companyInfo"//企业详细信息查询
    val companyPay = "verify/companyPay"//企业打款申请
    val companyPayVerify = "verify/companyPayVerify"//企业打款验证

    val resetPassCode = "sms/pwd"// 重置密码获取短信验证码
    val modifyLoginPass = "secure/modifyPwd"// 修改登录密码
    val checkCode = "sms"// 验证短信验证码
    val resetPassSubmit = "secure/pwd"// 重置密码提交
    val clearCache = "sys/cache_clean"// 清除缓存

    val isTradePass = "secure/trdpwd/state"// 是否设置交易密码
    val checkTradePass = "secure/trdpwd/check"// 检测交易旧密码是否正确
    val setTradePass = "secure/trdpwd"// 设置交易密码
    val forgetTradePass = "sms/forgettrdpwd"// 交易密码忘记 获取短信验证码、


    val isInstallCer = "secure/ca"// 是否安装证书
    val qzImage = "secure/viewdzyz"// 查看电子签章图片


    val bankCardSms = "acct/card/fast/smsCode"// 获取绑定充值卡短信验证码
    val checkBankSms = "acct/card/fast/bind"// 绑定充值卡验证短信验证码  ---------------
    val supervisionConfig = "acct/supervisionConfig"// 查询资金托管配置  ---------------


    val changePhoneMsgCode = "sms/modtel"// 更换手机号获取短信验证码
    val changePhoneSubmit = "secure/logintel"// 更换新手机号提交信息

    val installCode = "sms/ca"// 安装证书 所需要的验证码操作
    val normalSms = "sms/common"// 通用验证码
    val installCerSubmit = "secure/ca"// 安装证书进行的操作
    val caConfig = "secure/ca/config"// 企业证书信息
    val checkCa = "secure/ca/matchingPay"// 证书是否已支付money  匹配来账信息
    val zsMoneyInfo = "secure/ca/keyCharge"// 转账信息


    val userTelephones = "user/telephones"// 查询新老手机号
    val setNewTel = "user/chgRepeatTel"// 设置新的手机号

    val userMoreInfo = "user/completeInfo"// 查看更多资料信息
    val submitUserInfo = "user/completeInfo"// 完善用户信息
    val minZuList = "code/40000002"// 民族列表
    val zhiyeList = "code/40000001"// 职业列表

    val bankCardList = "acct/card"// 银行卡列表
    val bankCardList2 = "acct/card/chanageList"// 银行卡列表-可变更提现  个人的情况下
    val bankCardLisGroup = "acct/card/group"// 银行卡列表-分组
    val checkBankCard = "acct/card/openBank"//  根据银行卡卡号获取所属银行
    val bankFourCheck = "verify/bankCard"//  银行卡四要素验证
    val bankCard = "acct/card"// 提现账户维护
    val bindCard = "acct/card/withdraw/bind"// 提现账户维护
    val balanceAccount = "acct/balance" //账户余额
    val tradeList = "acct/trade"// 交易记录列表
    val borrowList = "loan/list"// 借款记录
    val borrowDetail = "loan/detail"// 借款详细
    val payTheInfo = "loan/trustee"// 借款详细
    val repaymentList = "repay/list"// 还款记录
    val repayPlan = "repay/plan"// 还款计划
    val repaymentDetail = "repay/detail"// 还款详情
    val jiekuanSxList = "credit/list"// 授信列表
    val payCompanyList = "loan/payfirm"// 查询付款方
    val yszkList = "loan/yszklist"// 查询应收账款信息
    val hetongInfo = "loan/tradecont"// 查询贸易合同信息
    val addHetongInfo = "loan/completecont"// 添加完善贸易合同信息
    val ruchiAction = "loan/inpoolapply"// 应收账款入池
    val childfirm = "loan/childfirm"// 查询分子公司信息
    val totleMoney = "credit/total"// 总额度
    val shouxinDetail = "credit/detail"// 授信详情
    val hetongList = "credit/contract"// 合同列表  授信合同
    val peopleList = "user/union"// 共同借款人列表
    val addPeople = "credit/unionLoaner"// 添加共同借款人
    val allZichan = "acct/balance"// 总资产  余额查询
    val zhanghuList = "acct"// 账户列表
    val creConfig = "credit/config"// 查询授信申请配置
    val creUploadFile = "upload"// 文件上传
    val applySubmit = "credit/apply"// 申请额度最后提交操作
    val opnbnk = "acct/card/opnbnkList"// 开户行  比如建设银行，不是开户网点
    val erleiHuList = "NCBCard/list"// 二类户查询
    val erleiMoney = "NCBCard/balance"// 二类户余额查询
    val bindList = "NCBCard/bindList"// 绑定一类户信息
    val erleiHuBind = "NCBCard/bind"// 二类户绑定
    val serverRandom = "NCBCard/serverRandom"// 二类户 ---  服务器端随机数字
    val erLeihuPass = "NCBCard/encry"// 二类户 ---  设置密码
    val erLeihuPassOpen = "NCBCard/open"// 二类户 ---  开户 设置密码
    val erLeihuQianYue = "NCBCard/sign"// 网银签约


    val chongzhiMoneyCodeCheck = "acct/recharge/verify"// 充值钱短信验证码验证  ---------------
    val chongzhiSubmit = "acct/recharge"// 充值钱短信验证码验证  ---------------
    val tixianSubmit = "acct/withdraw"// 提现  ---------------
    val unbindCard = "acct/card/delete"// 解绑银行卡  提现  充值  都可以解绑  ---------------


    val workList = "notice/main"// 待办事项列表
    val prePeopleCheck = "credit/preAuditDetail"// 查询预授信_共同借款人
    val peopleCheckSure = "credit/preAudit"// 预授信审核-共同借款人
    val reqCheck = "credit/reqValid"// 授权申请校验
    val reqShouxinDetail = "credit/preDetail"// 预授信详情

    val daihouDetail = "afterLoan/files"// 贷后详情
    val daihouFujianSubmit = "afterLoan/files"// 贷后上传附件修改


    val signDetail = "credit/signDetail"// 授信签署详情
    val signSubmit = "credit/sign"// 授信签署提交操作

    val repayConfig = "repay/config"// 还款申请配置
    val repayCreate = "repay/contract"// 生成还款申请书
    val repayLixi = "repay/interest"// 利息计算
    val repayApply = "repay/apply"// 还款申请
    val pdfUrl = "viewer/pdf"//  （协议）网页链接（pdf文件的查看）


    val skList = "loan/payeeList"// 收款人列表
    val skPeopleAdd = "loan/addPayee"// 添加收款人信息
    val jiekuanConfig = "loan/config"// 借款申请配置
    val bankWdList = "acct/card/opnbnkwd"// 银行网点列表
    val jiekuanHetong = "loan/createConts"// 借款合同生成
    val jiekuanSubmit = "loan/apply"// 借款最后数据提交

    val shoumoneyLine = "loan/pondinfo"// 应收账款图示信息
    val shoumoneyList = "loan/pondinfo2"// 应收账款列表信息




    //易随配
    const val RIGIST_CODE = "register_code" //注册验证码
    const val RIGIST_ACITON = "register" //注册
    const val RIGIST_INFO = "register_protocol" //注册协议
    const val RISK_INFO = "risk_disclosure" //风险书协议
    const val STATEMENT_INFO = "investor_statement" //投资人声明
    const val FORGOT_CODE = "forgot_code" //忘记密码验证码
    const val FORGOT_ACTION = "forgot_password" //忘记密码重置
    const val LOGIN_ACTION = "login" //登录

    const val NEWS_LIST = "news_center" //新闻列表
    const val NOTICE_LIST = "notice_list" //公告列表
    const val HOME_INFO = "home_info" //账号信息


    const val ACCOUNT_INFO = "account_info" //账号信息
    const val CERTIFIED_INFO = "certified_info" //认证信息
    const val CERTIFIED_ACTION = "certification" //认证操作
    const val BANK_LIST = "user_bank" //银行卡列表
    const val BINDBANK_ACTION = "bind_bank" //添加或修改银行卡
    const val BANK_INFO = "bank_info" //银行卡信息
    const val BANK_CODE = "bank_code" //银行卡验证码
    const val SETTING_INFO = "set_info" //账号设置
    const val MODIFY_PAYCODE = "modify_pay" //修改支付密码
    const val MODIFY_PAYCODE_CODE = "modify_pay_code" //修改支付密码验证码
    const val MODIFY_PASSWORD = "modify_login" //修改登录密码验证码
    const val MODIFY_PHONE_OLD_CODE = "original_phone_code" //修改手机号获取原手机验证码
    const val MODIFY_PHONE_NEXT_CODE = "modify_phone_next" //修改手机号下一步验证码
    const val MODIFY_PHONE_NEW_CODE = "modify_phone_code" //修改手机号获取新手机验证码
    const val MODIFY_PHONE = "modify_phone" //修改手机号








    val K_LINE = "Coin/k_chart"//K线图


}
