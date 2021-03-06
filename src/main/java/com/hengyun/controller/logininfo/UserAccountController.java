package com.hengyun.controller.logininfo;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hengyun.controller.BaseController;
import com.hengyun.domain.common.ResponseCode;
import com.hengyun.domain.information.DoctorInfo;
import com.hengyun.domain.information.Information;
import com.hengyun.domain.loginInfo.PasswordResult;
import com.hengyun.domain.loginInfo.RegisterResult;
import com.hengyun.domain.loginInfo.UserAccount;
import com.hengyun.domain.loginInfo.response.UserAccountResponse;
import com.hengyun.service.hospital.DocterService;
import com.hengyun.service.impl.logininfo.util.UserAccountResponseUtil;
import com.hengyun.service.impl.message.ProducerEmailServiceImpl;
import com.hengyun.service.impl.message.ProducerSmsServiceImpl;
import com.hengyun.service.information.InformationService;
import com.hengyun.service.logininfo.LoginInfoService;
import com.hengyun.service.logininfo.RegisterCacheService;
import com.hengyun.service.logininfo.UserAccountService;
import com.hengyun.service.util.EmailUtilService;
import com.hengyun.service.util.SmsUtilService;
import com.hengyun.util.json.JSONUtil;
import com.hengyun.util.mail.Register;
import com.hengyun.util.mail.SimpleMail;
import com.hengyun.util.regex.Validator;
import com.hengyun.util.sms.SubmitResult;
import com.hengyun.util.sms.sender.SmsSender;


/*
 *  用户账号管理，包括注册账号，修改账号等操作
 *  
 * */

@Controller
@RequestMapping("account")
public class UserAccountController extends BaseController{

	 private static final Logger log = LoggerFactory.getLogger(UserAccountController.class);
	@Resource
	private UserAccountService userAccountService;

	@Resource
	private RegisterCacheService registerCacheService;
	
	@Resource
	private  EmailUtilService emailService;
	
	@Resource
	private SmsUtilService smsService;
	
	@Resource
	private ProducerEmailServiceImpl producerEmailServiceImpl;
	
	@Resource
	private ProducerSmsServiceImpl producerSmsServiceImpl;
	
	@Resource
	private SimpleMail simpleMail;
	
	@Resource
	private DocterService docterService;
	
	@Resource
	private InformationService informationService;
	
	@Resource
	private LoginInfoService loginInfoService;
	

	/*
	 * 
	 *  发送短信
	 *  
	 * */
	public int  sms(String mobilephone){
		//手机是否注册
		if(userAccountService.existUser(mobilephone,"mobilephone")>0){
			log.info("手机号"+mobilephone+"已经注册");
			return -2;
		} else {
			//加载缓存
			if(!registerCacheService.existBySign(mobilephone)){
				registerCacheService.loadRegisterCache(mobilephone);
			}
	//		if(registerCacheService.getTryCount(mobilephone)<6){
				int codeNum = (int)(Math.random()*10000);
					codeNum = codeNum>1000?codeNum:codeNum+1000;
				
					registerCacheService.setConfirmCode(mobilephone, String.valueOf(codeNum));
					registerCacheService.addTryCount(mobilephone);
					
					SubmitResult result;
					SmsSender sms = new SmsSender(mobilephone,codeNum);
					result =  sms.send();
					if(result!=null){
						if(result.getCode()==2){
							return 2;
						}
					} else {
						log.error("手机号 "+mobilephone+" 发送短信失败");
						return -3;
					}
				
		}
		return -1;
	}
	
	/*
	 * 
	 *  发送邮件
	 * 
	 * */
	public int email(String email){
		
		if(userAccountService.existUser(email,"email")>0){
			log.info("邮箱"+email+"已经注册");
			return -2;
		} else {
			//加载缓存
			if(!registerCacheService.existBySign(email)){
				registerCacheService.loadRegisterCache(email);
			}
		//	if(registerCacheService.getTryCount(email)<6){
		
		//	registerCacheService.addTryCount(email);
			
			int codeNum = (int)(Math.random()*10000);
			codeNum = codeNum>1000?codeNum:codeNum+1000;
			registerCacheService.setConfirmCode(email, String.valueOf(codeNum));
			registerCacheService.addTryCount(email);
			String subject = "天衡会员注册邮件";
			String content = "欢迎注册天衡医疗会员，您本次注册时使用的验证码是"+codeNum+"如果非本人操作，请忽略。";
			
			String to = email;
			simpleMail.sendMail( subject,  content,  to);
			return 2;
		
		}
	
	}

	/*
	 * 
	 *  绑定亲情号请求
	 * 
	 * */
		@RequestMapping(value="/bindRelative",produces = "text/html;charset=UTF-8")
		@ResponseBody
		public String bindRelative(@RequestParam String data,HttpServletRequest request){
			JSONObject jsonObject =JSONUtil.parseObject(data);
			ResponseCode responseCode = new ResponseCode();
			String mobilephone = jsonObject.getString("mobilephone");
			int codeNum = (int)(Math.random()*10000);
			codeNum = codeNum>1000?codeNum:codeNum+1000;
			registerCacheService.setConfirmCode(mobilephone, String.valueOf(codeNum));
			registerCacheService.addTryCount(mobilephone);
			
			SubmitResult result;
			SmsSender sms = new SmsSender(mobilephone,codeNum);
			result =  sms.send();
			if(result.getCode()!=2){
				responseCode.setCode("110");
				responseCode.setMessage("请求发送失败，请重试");
				return JSON.toJSONString(responseCode);
			}
			
		
			responseCode.setCode("206");
			responseCode.setMessage("请求发送成功");
			
			return JSON.toJSONString(responseCode);
		
			
		}
		
		/*
		 * 
		 *  绑定亲情号请求
		 * 
		 * */
			@RequestMapping(value="/confirmRelative",produces = "text/html;charset=UTF-8")
			@ResponseBody
			public String confirmRelative(@RequestParam String data,HttpServletRequest request){
				int userId = (int)request.getAttribute("userId");
				JSONObject jsonObject =JSONUtil.parseObject(data);
				String confirmCode = jsonObject.getString("code");
				String mobilephone = jsonObject.getString("mobilephone");
				ResponseCode responseCode = new ResponseCode();
				Query query = Query.query(Criteria.where("mobilephone").is(mobilephone));
				UserAccount account2 = userAccountService.queryOne(query);
				int friendId = account2.getId();
				
				if(registerCacheService.getConfirmCode(mobilephone).equals(confirmCode)){
					//绑定亲情号
					userAccountService.bindFriend(userId, friendId);
				} else {
					responseCode.setCode("107");
					responseCode.setMessage("验证码错误");
				}

				responseCode.setCode("206");
				responseCode.setMessage("请求发送成功");
				
				return JSON.toJSONString(responseCode);
			
				
			}
	
	/*
	 * 
	 *  短信发送
	 * 
	 * */
	@RequestMapping(value="/smsSend",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String smsSend(@RequestParam String data){
		JSONObject jsonObject =JSONUtil.parseObject(data);
		
		String mobilephone = jsonObject.getString("mobilephone");
		
		RegisterResult registResult = new RegisterResult();
		int count =0;
		try {
			count = registerCacheService.getTryCount(mobilephone);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			registerCacheService.loadRegisterCache(mobilephone);
		}
		if(count>5){
			registResult.setCode("106");
			registResult.setMessage("发送次数过多");
			return JSON.toJSONString(registResult);
		} else {
			registerCacheService.addTryCount(mobilephone);
		}
		//查询改手机号是否注册
		int responseCode = sms(mobilephone);
		if(responseCode==-2){
			registResult.setCode("102");
			registResult.setMessage("用户已经存在");
		} else if(responseCode==2){
			registResult.setCode("205");
			registResult.setMessage("验证码发送成功");
		} else if(responseCode==-3){
			registResult.setCode("101");
			registResult.setMessage("验证码发送失败");
		} else if(responseCode ==-4) {
			registResult.setCode("106");
			registResult.setMessage("发送次数过多");
		} else if(responseCode==-1) {
			registResult.setCode("106");
			registResult.setMessage("未知错误");
		}
		
		return JSON.toJSONString(registResult);
	
		
	}

    /*
     * 
     *  短信接收注册
     * 
     * */
	@RequestMapping("/smsReceive")
	@ResponseBody
	public String smsReceive(@RequestParam String data){
		JSONObject jsonObject = JSONUtil.parseObject(data);
		String mobilephone = jsonObject.getString("mobilephone");
		String confirmCode = jsonObject.getString("code");
		String password =  jsonObject.getString("password");
		
		RegisterResult registResult = new RegisterResult();
		
		
		if(registerCacheService.getConfirmCode(mobilephone).equals(confirmCode)){
			
			UserAccount userAccount = new UserAccount();
			
			userAccount.setMobilephone(mobilephone);
			
			String encryptedPassword = new Md5Hash(password).toString();
			userAccount.setPassword(password);
			userAccount.setCatagory("patient");
			
			int id = userAccountService.registerAccount(userAccount);
		
			
			registerCacheService.updateRegisterCache(userAccount.getMobilephone());
			registResult.setCode("205");
			registResult.setMessage(String.valueOf(id));
			
		} else {	
			
			registResult.setCode("107");
			registResult.setMessage("test code error");
		}
		
	
		return JSON.toJSONString(registResult);
	}
	
	
	
	/*
	 * 邮箱发送验证码
	 * 
	 * */
	@RequestMapping(value="/mailSend",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String mailSend(@RequestParam String data){
		
		JSONObject jsonObject =JSONUtil.parseObject(data);
		
		String email = jsonObject.getString("email");
		RegisterResult registResult = new RegisterResult();
		//查询改邮箱是否注册
		int count =0;
		try {
			count = registerCacheService.getTryCount(email);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			registerCacheService.loadRegisterCache(email);
		}
		if(count>5){
			registResult.setCode("106");
			registResult.setMessage("发送次数过多");
			return JSON.toJSONString(registResult);
		} else {
			registerCacheService.addTryCount(email);
		}
		/*
		int responseCode =email( email);
		if(responseCode == -2){
			log.debug("邮箱"+email+"已经注册");
			registResult.setCode("102");
			registResult.setMessage("user exist");
		} else if(responseCode==2){
			registResult.setCode("205");
			registResult.setMessage("test code send success");
		} else if(responseCode == -4) {
			registResult.setCode("106");
			registResult.setMessage("too many times");
		}
`*/
		int codeNum = (int)(Math.random()*10000);
		codeNum = codeNum>1000?codeNum:codeNum+1000;
		registerCacheService.setConfirmCode(email, String.valueOf(codeNum));
	//	registerCacheService.addTryCount(email);
		String subject = "天衡会员注册邮件";
		String content = "欢迎注册天衡医疗会员，您本次注册时使用的验证码是"+codeNum+"如果非本人操作，请忽略。";
		String to = email;
		simpleMail.sendMail( subject,  content,  to);

		registResult.setCode("205");
		registResult.setMessage("验证码发送成功");
		return JSON.toJSONString(registResult);
	
	}
	
	/*
	 * 邮箱链接注册
	 * 
	 * */
	@RequestMapping(value="/mail",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String mail(@RequestParam String data){
		
		JSONObject jsonObject =JSONUtil.parseObject(data);
		
		String email = jsonObject.getString("email");
		RegisterResult registResult = new RegisterResult();
		//查询改邮箱是否注册
		int count =0;
		try {
			count = registerCacheService.getTryCount(email);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			registerCacheService.loadRegisterCache(email);
		}
		if(count>5){
			registResult.setCode("106");
			registResult.setMessage("发送次数过多");
			return JSON.toJSONString(registResult);
		} else {
			registerCacheService.addTryCount(email);
		}
	
		int codeNum = (int)(Math.random()*10000);
		codeNum = codeNum>1000?codeNum:codeNum+1000;
		registerCacheService.setConfirmCode(email, String.valueOf(codeNum));
	//	registerCacheService.addTryCount(email);
		String subject = "天衡会员注册邮件";
		String content = Register.register(email, String.valueOf(codeNum));
		String to = email;
		simpleMail.sendMail( subject,  content,  to);

		registResult.setCode("205");
		registResult.setMessage("验证码发送成功");
		return JSON.toJSONString(registResult);
	
	}
	
	   /*
	    * 邮箱接收注册
	    * 
	    * */
		@RequestMapping("/mailReceive")
		@ResponseBody
		public String mailReceive(@RequestParam String data){

			JSONObject jsonObject = JSONUtil.parseObject(data);
			String email = jsonObject.getString("email");
			String confirmCode = jsonObject.getString("code");
			String password =  jsonObject.getString("password");
			
			RegisterResult registResult = new RegisterResult();
			
			if(registerCacheService.getConfirmCode(email).equals(confirmCode)){
				UserAccount userAccount = new UserAccount();
				userAccount.setEmail(email);
				userAccount.setPassword(password);
				userAccount.setCatagory("patient");
				
				int id = userAccountService.registerAccount(userAccount);
				
				
				registerCacheService.updateRegisterCache(userAccount.getEmail());
				registResult.setCode("201");
				registResult.setMessage(String.valueOf(id));
				
			} else {
				
				registResult.setCode("107");
				registResult.setMessage("code error");
			}
			
		
			return JSON.toJSONString(registResult);
		}
	
	

		/*
		 * 
		 *  医生注册
		 * 
		 * */
		
		@RequestMapping(value="/register",produces = "text/html;charset=UTF-8")
		@ResponseBody
		public String docterRegister(@RequestParam String data){
			JSONObject jsonObject = JSONUtil.parseObject(data);
			ResponseCode response = new ResponseCode();
			UserAccount account = JSONUtil.toJavaObject(jsonObject, UserAccount.class);
			String username = jsonObject.getString("workNum");
		
			if(!docterService.exist(username)){
				response.setCode("103");
				response.setMessage("该医生为录入医院数据库");
				 return JSON.toJSONString(response);
			}
			if(userAccountService.existUserAccountBySign(username,"workNum")){
				response.setCode("102");
				response.setMessage("用户已经注册");
			} else{
				account.setCatagory("doctor");
			int id = userAccountService.registerAccount(account);
			
			response.setCode("201");
			response.setMessage(String.valueOf(id));
			}
			 return JSON.toJSONString(response);
		}
		
    /*
     *  查询账号
     *  
     * */
   
    @RequestMapping(value="/show",produces = "text/html;charset=UTF-8") 
    @ResponseBody  
    public  String   findUserAccount(){
    	List<UserAccount> userAccountList ;
    	userAccountList = userAccountService.getUserAccountALL();

    	 String jsonString= JSON.toJSONString(userAccountList);  
           
    	
        return jsonString;  
    }
    
    /*
     *  添加账号
     *  
     * */
    @RequestMapping(value="/add",produces = "text/html;charset=UTF-8") 
    @ResponseBody  
    public  String  addUserAccount(@RequestParam String data,HttpServletRequest request){
    	JSONObject jsonObject = JSONUtil.parseObject(data);
		ResponseCode response = new ResponseCode();
		UserAccount account = JSONUtil.toJavaObject(jsonObject, UserAccount.class);
    	userAccountService.registerAccount(account);
    	response.setCode("206");
    	response.setMessage("添加用户成功");
    	String jsonString= JSON.toJSONString(account);  
        return jsonString;  
    }
    
    /*
     * 查询userId账户信息
     * 
     * */
    @RequestMapping("/query") 
    @ResponseBody  
    public  String   queryAccount(@RequestParam String data){
    	JSONObject jsonObject =JSONUtil.parseObject(data);
    	int id = jsonObject.getIntValue("id");
    	UserAccount userAccount =null;
    	
    	try {
			userAccount=userAccountService.queryById(id);
			System.out.println(userAccount.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.debug(id+" 对应的用户不存在");
		}

    	 String jsonString= JSON.toJSONString(userAccount);  
           
    	
        return jsonString;  
    }
    
    /*
     * 
     *  修改密码
     *  
     * */
 
    @RequestMapping(value="/findPassword",produces = "text/html;charset=UTF-8") 
    @ResponseBody  
    public  String   findPassword(@RequestParam String data){
    	
    	JSONObject jsonObject =JSONUtil.parseObject(data);
		String mobilephone = jsonObject.getString("mobilephone");
		RegisterResult registResult = new RegisterResult();
		
		if(userAccountService.existUser(mobilephone,"mobilephone")<0){
			registResult.setCode("103");
			registResult.setMessage("用户未注册,不能修改密码");
		} else {
			int count =0;
			try {
				count = registerCacheService.getTryCount(mobilephone);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				registerCacheService.loadRegisterCache(mobilephone);
			}
			if(count>5){
				registResult.setCode("106");
				registResult.setMessage("发送次数过多");
				return JSON.toJSONString(registResult);
			} else {
				registerCacheService.addTryCount(mobilephone);
			}
			
			int codeNum = (int)(Math.random()*10000);
				codeNum = codeNum>1000?codeNum:codeNum+1000;
				registerCacheService.setConfirmCode(mobilephone, String.valueOf(codeNum));
			
				
				SubmitResult result;
				SmsSender sms = new SmsSender(mobilephone,codeNum);
				result =  sms.send();
				if(result!=null){
					if(result.getCode()==2){
						registResult.setCode("205");
						registResult.setMessage("验证码发送成功");
					}
				} else {
					registResult.setCode("101");
					registResult.setMessage("验证码发送失败");
				}
		
		}
		
		return JSON.toJSONString(registResult);
	
		
    }
    
    
    /*
     *  邮箱找密码
     *  
     * */
 
    @RequestMapping("/emailpassword") 
    @ResponseBody  
    public  String   emailPassword(@RequestParam String data){
    	
    	JSONObject jsonObject =JSONUtil.parseObject(data);
		String email = jsonObject.getString("email");
		RegisterResult registResult = new RegisterResult();
		
		if(userAccountService.existUser(email,"email")>0){
			registResult.setCode("103");
			registResult.setMessage("user not exist");
		} else {
			int count =0;
			try {
				count = registerCacheService.getTryCount(email);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				registerCacheService.loadRegisterCache(email);
			}
			if(count>5){
				registResult.setCode("106");
				registResult.setMessage("发送次数过多");
				return JSON.toJSONString(registResult);
			} else {
				registerCacheService.addTryCount(email);
			}
			
			int codeNum = (int)(Math.random()*10000);
				codeNum = codeNum>1000?codeNum:codeNum+1000;
				registerCacheService.setConfirmCode(email, String.valueOf(codeNum));
			//	registerCacheService.addTryCount(email);
				String subject = "天衡会员邮件找回密码";
				String content = "欢迎注册天衡医疗会员，您本次注册时使用的验证码是"+codeNum+"如果非本人操作，请忽略。";
				String to = email;
				simpleMail.sendMail( subject,  content,  to);

				registResult.setCode("205");
				registResult.setMessage("test code send success");
		}
		
		
		return JSON.toJSONString(registResult);
	
		
    }
    
    /*
     *  邮箱重置密码
     * 
     * */
    
    @ResponseBody  
    @RequestMapping("/emailReset") 
    public  String  emailReset(@RequestParam String data){
    	
    	JSONObject jsonObject = JSONUtil.parseObject(data);
		String email = jsonObject.getString("email");
		String confirmCode = jsonObject.getString("code");
		String password =  jsonObject.getString("password");
	
		PasswordResult passwordResult = new PasswordResult();
		int userId = userAccountService.existUser(email, "email");
		if(userId>0){
		if(registerCacheService.getConfirmCode(email).equals(confirmCode)){
			Query query = Query.query(Criteria.where("email").is(email).andOperator(Criteria.where("email").exists(true)));
			UserAccount userAccount = userAccountService.queryOne(query);
			String oldPassword = userAccount.getPassword();
			Update update = Update.update("password", password);
			 userAccountService.updateFirst(query, update);
			 passwordResult.setCode("206");
			 passwordResult.setMessage(String.valueOf(userId));
			 passwordResult.setOldPassword(oldPassword);
			
		} else {
		//	registerCacheService.addTryCount(email);
			passwordResult.setCode("107");
			passwordResult.setMessage("test code error");
		}
		}else {
			passwordResult.setCode("103");
			passwordResult.setMessage("user not exist");
		}
	
		return JSON.toJSONString(passwordResult);
       
    }
    
    /*
     *  手机重置密码
     * 
     * */
    
    @ResponseBody  
    @RequestMapping("/updatePassword") 
    public  String  updatePassword(@RequestParam String data){
    	
    	JSONObject jsonObject = JSONUtil.parseObject(data);
		String mobilephone = jsonObject.getString("mobilephone");
		String confirmCode = jsonObject.getString("code");
		String password =  jsonObject.getString("password");
	
		PasswordResult passwordResult = new PasswordResult();
		int  userId = userAccountService.existUser(mobilephone,"mobilephone");
		if(userId>0){
		if(registerCacheService.getConfirmCode(mobilephone).equals(confirmCode)){
			Query query = Query.query(Criteria.where("mobilephone").is(mobilephone).andOperator(Criteria.where("mobilephone").exists(true)));
			UserAccount userAccount = userAccountService.queryOne(query);
			String oldPassword = userAccount.getPassword();
			Update update = Update.update("password", password);
			 userAccountService.updateFirst(query, update);
			 passwordResult.setCode("206");
			 passwordResult.setMessage(String.valueOf(userId));
			 passwordResult.setOldPassword(oldPassword);
			
		} else {
			
			passwordResult.setCode("107");
			passwordResult.setMessage("test code error");
		}
		}else {
			passwordResult.setCode("103");
			passwordResult.setMessage("user not exist");
		}
	
		return JSON.toJSONString(passwordResult);
       
    }
    
    /*
     * 在线修改密码
     * 
     * */
    @ResponseBody  
    @RequestMapping("/changePassword") 
    public  String  changePassword(@RequestParam String data,HttpServletRequest request){
    	
    	JSONObject jsonObject = JSONUtil.parseObject(data);
    	String password = jsonObject.getString("password");
    	String tocken = request.getParameter("tocken");
    	PasswordResult response = new PasswordResult();
    	int userId = loginInfoService.isOnline(tocken);
    	if(userId>0){
    		Query query = Query.query(Criteria.where("id").is(userId));
    		UserAccount userAccount = userAccountService.queryOne(query);
    		String oldPassword = userAccount.getPassword();
    		Update update = Update.update("password", password);
    		userAccountService.updateFirst(query, update);
    		response.setCode("206");
    		//初始密码123456
    		//返回原始密码以及userId，存放在message里面
    		response.setMessage(String.valueOf(userId));
    		response.setOldPassword(oldPassword);
    		
    		
    	} else {
    		response.setCode("116");
    		response.setMessage("unlogin ");
    	}

		return JSON.toJSONString(response);
       
    }
    
    /*
     *  用户修改信息确认
     *  
     * */
    @ResponseBody  
    @RequestMapping(value="/selfSend",produces = "text/html;charset=UTF-8") 
    public  String  selfSend(@RequestParam String data,HttpServletRequest request){
    	
    	JSONObject jsonObject = JSONUtil.parseObject(data);
    	
    	String type = jsonObject.getString("type");
		String username = jsonObject.getString("username");

    	ResponseCode response = new ResponseCode();
  
    	int userId = (int)request.getAttribute("userId");
    	//用户在线
		int count =0;
		try {
			count = registerCacheService.getTryCount(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			registerCacheService.loadRegisterCache(username);
		}
		if(count>5){
			response.setCode("106");
			response.setMessage("发送次数过多");
			return JSON.toJSONString(response);
		} else {
			registerCacheService.addTryCount(username);
		}
    		if(type.equals("email")){						//修改邮箱
    					int codeNum = (int)(Math.random()*10000);
    					codeNum = codeNum>1000?codeNum:codeNum+1000;
    					registerCacheService.setConfirmCode(username, String.valueOf(codeNum));
    				
    					String subject = "天衡会员确认邮件";
    					String content = "您本次验证码是"+codeNum+"如果非本人操作，请忽略。";
    					String to = username;
    					simpleMail.sendMail( subject,  content,  to);
    					response.setCode("205");
    		    		response.setMessage("验证码发送成功 ");
    			
    			
    		} else if(type.equals("mobilephone")){			//修改手机号
    			
    			
					int codeNum = (int) (Math.random() * 10000);
					codeNum = codeNum > 1000 ? codeNum : codeNum + 1000;
					registerCacheService.setConfirmCode(username, String.valueOf(codeNum));

					SubmitResult result;
					SmsSender sms = new SmsSender(username, codeNum);
					result = sms.send();
					if (result != null) {
						if (result.getCode() == 2) {
							log.info("发送给手机 " + username + "的短信发送成功");
						}
					}
					response.setCode("205");
		    		response.setMessage("验证码发送成功 ");
				
    		}

		return JSON.toJSONString(response);
       
    }
    
    /*
     *  解绑手机邮箱
     *  
     * */
    @ResponseBody  
    @RequestMapping(value="/unbind",produces = "text/html;charset=UTF-8") 
    public  String  unbind(@RequestParam String data,HttpServletRequest request){
    	int userId = (int)request.getAttribute("userId");
    	JSONObject jsonObject = JSONUtil.parseObject(data);

    	ResponseCode response = new ResponseCode();
    	//String type = jsonObject.getString("type");
		String username = jsonObject.getString("username");
		String type=Validator.type(username);
    	String  confirmCode = jsonObject.getString("code");
    	
  	  if(!registerCacheService.getConfirmCode(username).equals(confirmCode)){
			response.setCode("107");	//106 发送次数过多
			response.setMessage("验证码错误");
			
		}else {
			int status = userAccountService.change(type, null, userId);
			response.setCode("205");
			response.setMessage("解绑成功");
		}
	
	
		return JSON.toJSONString(response);
       
    }
    
 
    
    /*
     *  验证更改绑定合法
     *  
     * */
    @ResponseBody  
    @RequestMapping(value="/validate",produces = "text/html;charset=UTF-8") 
    public  String  validate(@RequestParam String data,HttpServletRequest request){
    	
    	JSONObject jsonObject = JSONUtil.parseObject(data);
    	
    	//String type = jsonObject.getString("type");
		String username = jsonObject.getString("username");
		String type = Validator.type(username);
		
    	ResponseCode response = new ResponseCode();
    	
    	int userId = (int)request.getAttribute("userId");
    	int  user = 0;
    	//用户在线
    	int count =0;
		try {
			count = registerCacheService.getTryCount(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			registerCacheService.loadRegisterCache(username);
		}
		if(count>5){
			response.setCode("106");
			response.setMessage("发送次数过多");
			return JSON.toJSONString(response);
		} else {
			registerCacheService.addTryCount(username);
		}
    		if(type.equals("email")){						//修改邮箱
    			  user = userAccountService.existUser(username,"email");
    			  if(user>0){
    				  response.setCode("103");
    		    		response.setMessage("修改邮箱已经使用，请使用为使用邮箱 ");
    		    		return JSON.toJSONString(response);
    			  } else {
    				
    					int codeNum = (int)(Math.random()*10000);
    					codeNum = codeNum>1000?codeNum:codeNum+1000;
    					registerCacheService.setConfirmCode(username, String.valueOf(codeNum));
    				
    					String subject = "天衡会员确认邮件";
    					String content = "您本次验证码是"+codeNum+"如果非本人操作，请忽略。";
    					String to = username;
    					simpleMail.sendMail( subject,  content,  to);
    					response.setCode("205");
    		    		response.setMessage("验证码发送成功 ");
    			  }
    			
    		} else if(type.equals("mobilephone")){			//修改手机号
    			user = userAccountService.existUser(username,"mobilephone");
    			if(user>0){
    				  response.setCode("103");
  		    		response.setMessage("手机号已经使用，请使用为使用手机号");
  		    		return JSON.toJSONString(response);
				} else {
					
					int codeNum = (int) (Math.random() * 10000);
					codeNum = codeNum > 1000 ? codeNum : codeNum + 1000;
					registerCacheService.setConfirmCode(username, String.valueOf(codeNum));

					SubmitResult result;
					SmsSender sms = new SmsSender(username, codeNum);
					result = sms.send();
					if (result != null) {
						if (result.getCode() == 2) {
							log.info("发送给手机 " + username + "的短信发送成功");
						}
					}
					response.setCode("205");
		    		response.setMessage("验证码发送成功 ");
				}
    		}
    	
	
	
		return JSON.toJSONString(response);
       
    }
    
    /*
     *  更改第三方绑定信息
     *  
     * */
    @ResponseBody
    @RequestMapping(value="/changeThird",produces = "text/html;charset=UTF-8")
    public String changeThird(@RequestParam String data,HttpServletRequest request){
    	JSONObject jsonObject = JSONUtil.parseObject(data);
    	String type = jsonObject.getString("type");
		String username = jsonObject.getString("username");
	
    	String tocken = request.getParameter("tocken");
    	ResponseCode response = new ResponseCode();
    	int userId = loginInfoService.isOnline(tocken);
    	//更改第三放绑定信息
    	if(userId>0){
    		if(userAccountService.validateThirdUserBySign(username,type)!=null){
    			 response.setCode("103");
		    		response.setMessage("三方号已经使用，请使用未使用三方号");
				return JSON.toJSONString(response);
    		}
    		int status = userAccountService.change(type, username, userId);
    		response.setCode("206");
    		response.setMessage("绑定成功");
    	}
    	return JSON.toJSONString(response);
    }
    
    
    /*
     * 
     *  更改手机邮箱绑定信息
     *  
     * */
    @ResponseBody  
    @RequestMapping(value="/change",produces = "text/html;charset=UTF-8") 
    public  String  change(@RequestParam String data,HttpServletRequest request){
    	
    	JSONObject jsonObject = JSONUtil.parseObject(data);
  //  	String type = jsonObject.getString("type");
		String username = jsonObject.getString("username");
		String confirmCode = jsonObject.getString("code");
		String type = Validator.type(username);

    	ResponseCode response = new ResponseCode();
    	int userId = (int)request.getAttribute("userId");
    	
    
			if(type.equals("email")){			//更改邮箱
			
				if(userAccountService.existUser(username,"email")>0){
					 response.setCode("103");
	  		    	response.setMessage("邮箱已经使用，请使用为使用邮箱");
					return JSON.toJSONString(response);
				}
		
			if(!registerCacheService.getConfirmCode(username).equals(confirmCode)){
				response.setCode("107");
				response.setMessage("验证码错误");
				return JSON.toJSONString(response);
			}
			} else if(type.equals("moblephone")){			//更改手机号
				
				if(userAccountService.existUser(username,"mobilephone")>0){
					  response.setCode("103");
	  		    		response.setMessage("手机号已经使用，请使用未使用手机号");
					return JSON.toJSONString(response);
				}
			
				if(!registerCacheService.getConfirmCode(username).equals(confirmCode)){		
					
					response.setCode("107");
					response.setMessage("验证码错误");
					return JSON.toJSONString(response);
				}
			}
			else if(type.equals("username")){			//更改用户名
			
				if(userAccountService.existUser(username,"username")>0){
					  response.setCode("103");
	  		    		response.setMessage("用户名已经使用，请使用未使用用户名");
					return JSON.toJSONString(response);
				}
			}
			//更改用户绑定信息
    		int status = userAccountService.change(type, username, userId);
    		response.setCode("206");
    		response.setMessage("更新成功 ");
    	
	
		return JSON.toJSONString(response);
       
    }
    
    /*
     *  查询用户账号
     *  
     * */
    @ResponseBody  
    @RequestMapping(value="/info",produces = "text/html;charset=UTF-8") 
    public  String  info(HttpServletRequest request){
    //	JSONObject jsonObject = JSONObject.parseObject(data);
    //	String recordTime = jsonObject.getString("recordTime");
    	
    
	
    	int userId = (int)request.getAttribute("userId");
    	UserAccount account = userAccountService.getUserAccountById(userId);
    	  Query query =Query.query(Criteria.where("userId").is(userId));
        
           Information temp =informationService.queryOne(query);
           
           String nickname = temp.getTrueName();
           String sex = temp.getSex();
           String resume = temp.getResume();
           int age = temp.getAge();
           String birthday  = temp.getBirthday();

       


           String iconUrl = temp.getIconUrl();
           DoctorInfo doctorInfo = new DoctorInfo();
           doctorInfo.setIconUrl(iconUrl);
           doctorInfo.setBirthday(birthday);
          

           doctorInfo.setTrueName(nickname);
           doctorInfo.setUserId(userId);
           doctorInfo.setResume(resume);
        
           doctorInfo.setAge(age);
           doctorInfo.setSex(sex);

           
           
    	UserAccountResponse response = new UserAccountResponse();
    	account = UserAccountResponseUtil.transfer(account);
    
    	response.setCode("206");
    	response.setMessage("查询成功");
    	response.setAccount(account);
    	response.setInfo(doctorInfo);
	
		return JSON.toJSONString(response);
       
    }
}
