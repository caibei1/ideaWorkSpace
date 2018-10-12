package com.fuzamei.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.fuzamei.constant.PubConstants.BankMoneyFlowFlag;
import com.fuzamei.http.HttpRequest;
import com.fuzamei.pojo.bean.BankFlowResultBean;
import com.fuzamei.pojo.po.QueryBankFlowBean;
import com.fuzamei.service.BankMoneyFlowService;
import com.fuzamei.util.ConfReadUtil;

public class QueryBankFlow {
	private static final Logger logger = LoggerFactory.getLogger(QueryBankFlow.class);

	@Autowired
	private BankMoneyFlowService bankMoneyFlowService;

	public void queryMoneyFlow() {
		//logger.info("query bank flow start ..........");
		HttpRequest httpRequest = new HttpRequest();
		QueryBankFlowBean rcchargeRecord = new QueryBankFlowBean();
		rcchargeRecord.setAccountid("33050110451700000060");
		rcchargeRecord.setFlag(BankMoneyFlowFlag.RECHARGE);
		String param = JSON.toJSONString(rcchargeRecord);
		String result = "";
		try {
			result = httpRequest.sendPost(ConfReadUtil.getProperty("bank_query_flow"), param);
		} catch (Exception e) {
			//logger.info("query bank flow exception : " + e);
		}

		//logger.info("query bank flow result : " + result);
		if (null != result && !("").equalsIgnoreCase(result)) {
			BankFlowResultBean bankFlowResultBean = new BankFlowResultBean();
			try {
				bankFlowResultBean = (BankFlowResultBean) JSON.parseObject(result, BankFlowResultBean.class);
			} catch (Exception e) {
				//logger.error("query bank flow result to bean fail : exception = " + e.getMessage());
			}

			if (("200").equals(bankFlowResultBean.getCode())) {
				List<String> resultList = bankMoneyFlowService.addBankRecharge(bankFlowResultBean);
				if (resultList.size() > 0) {
					rcchargeRecord.setTranflows(resultList);
					param = JSON.toJSONString(rcchargeRecord);
					try {
						// logger.info("确认请求参数 ：" + param);
						 httpRequest.sendPost(ConfReadUtil.getProperty("bank_check"),param);
					} catch (Exception e) {
						//logger.info("reply bank flow exception : " + e);
					}
				}
			} else {
				//logger.error("query bank flow file result code : " + bankFlowResultBean.getCode());
		}
		}
		//logger.info("query bank flow end ..........");
	}
}
