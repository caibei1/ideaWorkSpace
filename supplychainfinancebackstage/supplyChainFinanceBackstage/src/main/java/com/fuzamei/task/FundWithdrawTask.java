package com.fuzamei.task;

import java.net.BindException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.SynthesizedAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.fuzamei.constant.AuthEnum;
import com.fuzamei.constant.HintEnum;
import com.fuzamei.constant.States;
import com.fuzamei.mapper.FinanceManageMapper;
import com.fuzamei.mapper.UserMapper;
import com.fuzamei.pojo.dto.UserDetailDTO;
import com.fuzamei.pojo.po.Back;
import com.fuzamei.pojo.po.Cinfig;
import com.fuzamei.pojo.po.FinanceBankPO;
import com.fuzamei.pojo.po.Result;
import com.fuzamei.service.FinanceManageService;
import com.fuzamei.service.UserService;
import com.fuzamei.service.impl.FinanceManageServiceImpl;
import com.fuzamei.util.ConfReadUtil;
import com.fuzamei.util.HttpRequest;
import com.fuzamei.util.ResultResp;
import com.fuzamei.util.ValidationUtil;
import com.fuzamei.web.FinanceMangeAction;

@Component 
public class FundWithdrawTask {
	@Autowired
	public  FinanceManageService financeManageService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserMapper userMapper;
	private static final Logger LOGGER = LoggerFactory.getLogger(FundWithdrawTask.class);
	
	public void updateWeight() {
		try {
			FinanceBankPO financeBankPO=new FinanceBankPO();
			List<String> serialNumbers = new ArrayList<String>();//针对状态是15的serialNumbers
			List<String> serialNumbers2 = new ArrayList<String>();//针对状态是19的serialNumbers
			List<FinanceBankPO> financeBank=financeManageService.selectSerialnumberBankCard();
			if(financeBank!=null && financeBank.size()!=0) {
				for (int i = 0; i < financeBank.size(); i++) {
					String serialnumber=financeBank.get(i).getSerialnumber(); //id
					String bankCard=financeBank.get(i).getBankCard();         //银行卡号
					financeBankPO.setSerialnumber(serialnumber);
					financeBankPO.setBankCard(bankCard);
					if(serialnumber!=null && bankCard!=null) {
						String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("bank_flow_gloden_id"), JSON.toJSONString(financeBankPO));
						@SuppressWarnings("rawtypes")
						Result res=JSON.parseObject(result,Result.class);
						Back be=res.getData();
						if("2".equals(be.getDealresult())||"3".equals(be.getDealresult())||"4".equals(be.getDealresult())) {//返回过来的状态码如果是9代表等待处理
							//financeManageService.updateState(financeBankPO);
							Integer state = financeBank.get(i).getState();
							if(state==15) {
								serialNumbers.add(serialnumber);
							}
							if(state==19) {
								serialNumbers2.add(serialnumber);
							}
						}
					}
				}
			}
			if(serialNumbers.size()>0){
				financeBankPO.setSerialNumbers(serialNumbers);
				financeManageService.updateState(financeBankPO);//批量改状态15的  等待处理（已退款）  
			}
			
			if(serialNumbers2.size()>0){  
				financeBankPO.setSerialNumbers2(serialNumbers2);//这里改了setSerialNumbers2   4.14
				financeManageService.updateState2(financeBankPO);//批量改状态19的  处理中（入金拒绝3） 
			}
		} catch (Exception e) {
			//LOGGER.error("定时器更新入金退款操作异常,详情:{}",e.getMessage());
		}
    }
	
	////////////////////////////////////////////////////上面是实时刷新 入金退款和入金拒绝的////////////////////////////////////////////////////////////////////////////////////
	                                                  ///////下面是实时刷新出金同意的///////
	/**
	 * 出金复审同意（同样是转出金额）
	 */
	public void updateWeight2() {
		try {
			FinanceBankPO financeBankPO=new FinanceBankPO();
			List<FinanceBankPO> financeBank=financeManageService.selectOutGoldenAgree();//查询状态7 为复审通过得
			if(financeBank!=null && financeBank.size()!=0) {
			for (int i = 0; i < financeBank.size(); i++) {
				String serialnumber=financeBank.get(i).getSerialnumber(); //要请求得id
				String bankCard=financeBank.get(i).getBankCard();         //银行卡号
				System.out.println(financeBank.get(i).getSerialnumber()+"<--请求id是,卡号-->"+financeBank.get(i).getBankCard()+"jjj流水号"+financeBank.get(i).getMoneyFlowNo());
				financeBankPO.setSerialnumber(serialnumber);
				financeBankPO.setBankCard(bankCard);
				System.out.println(serialnumber+"低洼低洼的============================="+bankCard+""+financeBank.get(i).getMoneyFlowNo());
				if(serialnumber!=null && bankCard!=null) {
					  String result = new HttpRequest().sendPost(ConfReadUtil.getProperty("bank_flow_gloden_id"), JSON.toJSONString(financeBankPO));
					  System.out.println(result+"测试=============================");
					  @SuppressWarnings("rawtypes")
					  Result res=JSON.parseObject(result,Result.class);
					  Back be=res.getData();
					  
					  //如果返回结果是234 并且 flag是1得话  说明这边冻结金额已经提走了，银行那边也显示转出成功了但是区块链没上成功，再下面方法里需要重新单独在上一次区块链
					  if("2".equals(be.getDealresult())||"3".equals(be.getDealresult())||"4".equals(be.getDealresult())) {//返回过来的状态码如果是234代表银行转出成功
						  System.out.println(result+"ZZZZ"+be.getDealresult());
						  Integer uid=financeBank.get(i).getUid();//通过复审人得uid   查询当前操作这个人得公钥私钥
						  UserDetailDTO userDetailDTO= userMapper.queryUserAuthority(uid);
						  financeBankPO.setUserDetailDTO(userDetailDTO);
						  financeBankPO.setEnterpriseId(financeBank.get(i).getEnterpriseId());
						  financeBankPO.setAmount(financeBank.get(i).getAmount());
						  financeBankPO.setMoneyFlowNo(financeBank.get(i).getMoneyFlowNo());
						  System.out.println(financeBank.get(i).getAmount()+"钱和编号"+financeBank.get(i).getMoneyFlowNo()+"Id是"+serialnumber);
						  financeManageService.updateOutGoldenState(financeBankPO);//第一步操作
					  
					  }
					  //这里：如果上面方法返回2.3.4去修改金额 和状态   如果金额修改好了状态也改了  但上区块链失败了这时候不给它抛出异常信息，直接判断如果区块链返回不管是什么异常信息（断网,代码,等等问题）是false就给数据库set进去 flag=1  签名也set进去
					  //到下面这个方法后 去数据库根据serialnumber(唯一性)查询flag=1得这条数据  就说明上面得方法刚才是金额和状态已经修改好了，就只是上链失败了，这时候需要重新在上一次区块链，别的不用操作，这时候账户提现金额已经提走了，总资产也减掉了，
					  //银行那边也提现成功了
					  //所以不能再次让他提款了
					  Integer flag= financeBank.get(i).getFlag();
					  if("2".equals(be.getDealresult())||"3".equals(be.getDealresult())||"4".equals(be.getDealresult()) && flag==1 ) {//金额前面已经修改掉了 就是上链没成功   这里是重新在上链
						  System.out.println("如果返回结果是234 并且 flag是1得话  说明这边冻结金额已经提走了，银行那边也显示转出成功了但是区块链没上成功，在此方法里需要重新单独在上一次区块链");
						  Integer uid=financeBank.get(i).getUid();//通过复审人得uid   查询当前操作这个人得公钥私钥
						  UserDetailDTO userDetailDTO= userMapper.queryUserAuthority(uid);
						  financeBankPO.setUserDetailDTO(userDetailDTO);
						  financeBankPO.setMoneyFlowNo(financeBank.get(i).getMoneyFlowNo());//流水编号
						  financeBankPO.setEnterpriseId(financeBank.get(i).getEnterpriseId());//企业Id
						  financeManageService.againOnTheChain(financeBankPO);
					  }
					  //如果是0.1.6.9.是处理中,什么都不操作
					  if("0".equals(be.getDealresult())||"1".equals(be.getDealresult())||"6".equals(be.getDealresult())||"9".equals(be.getDealresult())) {//返回过来的状态码如果是9代表等待处理
						  System.out.println("0.1.6.9.是处理中,什么都不操作");
						}
					  //如果银行返回是5和A说明银行那边就是出金失败了  那我这边就把要提现得金额在还给可用金额上面   然后告诉区块链那边是失败  给他传个false
					  if("A".equals(be.getDealresult())||"5".equals(be.getDealresult())) {//返回过来的状态码如果是9代表等待处理
						  System.out.println("如果是5和A就是失败的意思,直接就把总资产还到之前没提现金额的时候，和可用金额也给还到之前没提现金额的时候，和把冻结金额减走要准备提现的金额");
						  Integer uid=financeBank.get(i).getUid();//通过复审人得uid   查询当前操作这个人得公钥私钥 后面签名上链
						  UserDetailDTO userDetailDTO= userMapper.queryUserAuthority(uid);
						  financeBankPO.setUserDetailDTO(userDetailDTO);
						  financeBankPO.setEnterpriseId(financeBank.get(i).getEnterpriseId());//企业Id
						  financeBankPO.setAmount(financeBank.get(i).getAmount());
						  financeBankPO.setMoneyFlowNo(financeBank.get(i).getMoneyFlowNo());//流水编号
						  financeManageService.updateOutGoldenState2(financeBankPO);
						}
				   }
			}
		 }
		} catch (Exception e) { 
			//LOGGER.error("定时器更新出金退款操作异常,详情:{}",e.getMessage());
		}
	
		
	}

	
}
