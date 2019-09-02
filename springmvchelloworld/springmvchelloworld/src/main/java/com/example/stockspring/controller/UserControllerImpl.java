package com.example.stockspring.controller;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.stockspring.SendMail;
import com.example.stockspring.model.Login;
import com.example.stockspring.model.Otp;
import com.example.stockspring.model.ResetPassword;
import com.example.stockspring.model.User;
import com.example.stockspring.model.Username;
import com.example.stockspring.service.UserService;
import com.example.stockspring.service.UserServiceImpl;

@Controller
public class UserControllerImpl implements UserController {


	@Autowired 
	private UserService userService=new UserServiceImpl();
	SendMail sendMail=new SendMail();
	int otp;
	User u=new User();
	

	//----------------registers as user---------------------------------------------------------------------------------------------

	@RequestMapping(value = "/registerUser", method = RequestMethod.POST) 
	public String registerUser(@Valid User user, BindingResult result, Model model) throws SQLException { 
		System.out.println("inside post method"); 
		Random r = new Random(); 
		int num = (r.nextInt(90000) + 10000);
		user.setId(BigInteger.valueOf(num));
		user.setUsertype("user");
		System.out.println(user);
		otp=sendMail.sendMail(user.getEmail());
		u.setId(user.getId());
		u.setConfirmed(user.getConfirmed());
		u.setEmail(user.getEmail());
		u.setMobilenumber(user.getMobilenumber());
		u.setUsername(user.getUsername());
		u.setUsertype(user.getUsertype());
		u.setPassword(user.getPassword());
		/*if(userService.registerUser(user)) {
			System.out.println("if true");
			return	"redirect:/Home"; 
		}
		else {
			System.out.println("if false");
			return "error";
		} */
		 return "redirect:/otp";

	}

	//--------------------------registers as admin-----------------------------------------------------------------------------
	
	@RequestMapping(value = "/registerAdmin", method = RequestMethod.POST) 
	public String registerAdmin(@Valid User user, BindingResult result, Model model) throws SQLException { 
		System.out.println("inside post method"); 
		Random r = new Random(); 
		int num = r.nextInt(90000) + 10000;
		user.setId(BigInteger.valueOf(num));
		user.setUsertype("admin");
		System.out.println(user);
		otp=sendMail.sendMail(user.getEmail());
		u.setId(user.getId());
		u.setConfirmed(user.getConfirmed());
		u.setEmail(user.getEmail());
		u.setMobilenumber(user.getMobilenumber());
		u.setUsername(user.getUsername());
		u.setUsertype(user.getUsertype());
		u.setPassword(user.getPassword());
		/*if(userService.registerUser(user)) {
			System.out.println("if true");
			return	"redirect:/Home"; 
		}
		else {
			System.out.println("if false");
			return "error";
		} */
		 return "redirect:/otp";

	}

	//------------------------opens otp page-------------------------------
	
	@RequestMapping(value = "/otp", method = RequestMethod.GET)
	public String otpPage(ModelMap model) throws SQLException {
		System.out.println("add employee");
		Otp otp1=new Otp();
		//e.setEmail("sdfsf");
		//	e.setSalary(4564.56f);
		model.addAttribute("o", otp1);
		return "OTP";

	}
	
	//---------------------------verifies OTP and registers as admin /user-----------------------------------------------------------------
	
	@RequestMapping(value = "/verifyOtp", method = RequestMethod.POST) 
	public String registerStock(@Valid Otp otp1, BindingResult result, Model model) throws SQLException { 
		System.out.println("inside post method");
		System.out.println("Otp1 otp is:"+otp1.getOtp());
		System.out.println("Otp sent: " + otp );
		if(otp==otp1.getOtp())
		{
			u.setConfirmed(1);
			if(userService.registerUser(u)) {
				System.out.println("if true");
				return	"redirect:/Home"; 
			}
			else {
				System.out.println("if false");
				return "redirect:/otp";
			}
		}
			
		else 
			return "redirect:/otp"; 
	}
	
	@RequestMapping(value = "/verify")
	@ResponseBody
	public String verify(@RequestParam String otp2) throws SQLException {
		System.out.println("=========================OTP verification=====================");
		String ot=Integer.toString(otp);
		if(ot.equals(otp2))
			return "Right";
		else
			return "Wrong";
	}
	
	//------------------------------validates login credentials-----------------------------------------------
	
	@RequestMapping(value = "/login", method = RequestMethod.POST) 
	public String loginUser(@Valid Login login, BindingResult result, Model model, HttpSession session) throws SQLException { 
		System.out.println("inside post method"); 
		System.out.println("password"+login.getPassword());
		User user=userService.loginUser(login);
		
		System.out.println("++++++++++++++++++++"+user);
		if(user!=null && user.getPassword().equals(login.getPassword())) {
			
			session.setAttribute("user", user);
			if((user.getUsertype()).equals("user"))
				return	"redirect:/userLandingPage";
			else
				return "redirect:/adminLandingPage";
		}
		else 
			return "redirect:/Home"; 

	}

	//-------------------Opens Admin Landing Page-------------------------

	@RequestMapping(value = "/adminLandingPage", method = RequestMethod.GET)
	public String adminLanding(ModelMap model) throws SQLException {
		return "adminLandingPage";

	}	

	//-------------------------Opens userLAnding PAge-----------------------
	
	@RequestMapping(value = "/userLandingPage", method = RequestMethod.GET)
	public String userLanding(ModelMap model) throws SQLException {
		return "userLandingPage";

	}	

	//--------------------logout-----------------------------------------
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String login(HttpSession session) throws SQLException {
		session.invalidate();
		return "redirect:/Home";

	}	

	//----------------------opens access denied page if a user wants to access a page associated with admin's account or the vice versa---------------
	
	@RequestMapping(value = "/AccessDenied", method = RequestMethod.GET)
	public String AccessDenied(ModelMap model) throws SQLException {
		return "AccessDenied";

	}	
	
	//------------------------ajax function: Checks availability of username which must be unique --------------------------------------
	
	@RequestMapping(value = "/checkAdminid")
	@ResponseBody
	public String checkAdminid(@RequestParam String admin) {
		System.out.println("=========================check email controller=====================");
		System.out.println("Admin=============="+admin);
		if (userService.checkAdminid(admin)) {
			return "Username is Available";
		} else
			return "Username is taken";
	}
	
	//-------------------------ajax function: Checks login validation and displays alert box in case of wrong credentials--------------------
	
	@RequestMapping(value = "/checkValidation")
	@ResponseBody
	public String checkValidation(@RequestParam String uname, String pass) throws SQLException {
		System.out.println("=========================check Validation=====================");
		System.out.println("Password==============="+pass);
		Login login=new Login();
		login.setUsername(uname);
		login.setPassword(pass);
		User user=userService.loginUser(login);
		if(user!=null && user.getPassword().equals(login.getPassword()))
			return "Right";
		else
			return "Wrong";
	}
	
	//------------------------Opens forgot password Page-------------------------------------
	
	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
	public String forgotpassword(ModelMap model) throws SQLException {
		Username u=new Username();
		model.addAttribute("o", u);
		return "ForgotPassword";

	}
	
	//------------------------sends password through mail -------------------------------------------
	
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public String forgot(@Valid Username username,ModelMap model) throws SQLException {
		User u1=userService.forgotPassword(username.getUsername());
		if(u1!=null) {
		sendMail.sendMail(u1.getEmail(), u1.getPassword());
		return "redirect:/Home";
		}
		else
			return "redirect:/forgotpassword";

	}
	
	//--------------------------Opens reset password page-------------------------------------------
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public String resetpassword(ModelMap model) throws SQLException {
		ResetPassword u=new ResetPassword();
		model.addAttribute("e1", u);
		return "ResetPassword";

	}
	
	//---------------------------Resets password-----------------------------------------------------
	
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String reset(@Valid ResetPassword resetPassword ,ModelMap model) throws SQLException {
		Login login=new Login();
		login.setUsername(resetPassword.getUname());
		login.setPassword(resetPassword.getOldpass());
		User user=userService.loginUser(login);
		user.setPassword(resetPassword.getNewpass());
		userService.registerUser(user);
		return "redirect:/resetPassword";

	}
}



