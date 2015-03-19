package org.AC.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.AC.DAO.SessionFeedBackDAO;
import org.AC.Util.SendMail;
import org.AC.Util.SetFile;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class SessionFeedBackController
 */
@WebServlet("/SessionFeedBackController")
@MultipartConfig
public class SessionFeedBackController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SessionFeedBackController.class); 


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entered doPost method of SessionFeedBackController");
		String body = request.getParameter("body");
		String sId = request.getParameter("sId");
		String type = request.getParameter("type");
		String subject = request.getParameter("subject");
		String advisor = request.getParameter("advisor");
		String action = request.getParameter("action");
		String reject = request.getParameter("reject");
		String again = request.getParameter("again");
		String email = request.getParameter("email");
		String target = request.getParameter("target");
		String isApprove =request.getParameter("isApprove");
		if(sId != null && body!= null && subject != null && type == null && advisor ==null && reject == null && email==null){
			//Check if the user has already sent the feedback
			SessionFeedBackDAO checkfeedback = new SessionFeedBackDAO();
			Boolean isFeedBackGiven = checkfeedback.CheckFeedBack(sId);
			if(!isFeedBackGiven){
				Properties prop = new Properties();
			    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Path.properties");
			    prop.load(resourceAsStream);
				String path = prop.getProperty("SESSION_FEEDBACK_FILE_PATH");
				Part filePart = request.getPart("myFile");
				String fileName = getFileName(filePart);
				String fileURL="";
				if(fileName != null){
					SetFile file = new SetFile();
					fileURL = file.PutFile(request, response, sId,path);
				}
				SessionFeedBackDAO feedback = new SessionFeedBackDAO();
				Boolean isFeedbackCommit= feedback.SetUserFeedBack(sId,body,subject,fileURL);
				if(isFeedbackCommit){
					 String subjects = "Session FeedBack given by User";
					 String content = "Hi,<br>A session feedback has been recieved for: <br>Session Id : " +sId+ "<br>Given By : USER"+"<br><img src=\"http://www.advisorcircuit.com/Test/assets/img/logo_black.png\" style='float:right' width='25%'>";
					 SendMail mail = new SendMail(subjects, content, "admin@advisorcircuit.com","admin@advisorcircuit.com");
					 mail.start();
					 response.getWriter().write("<p style='color: #c84c4e'>Your Feedback has been sent</p>");
				}
			}else{
				if(again != null){
					Properties prop = new Properties();
				    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Path.properties");
				    prop.load(resourceAsStream);
					String path = prop.getProperty("SESSION_FEEDBACK_FILE_PATH");
					Part filePart = request.getPart("myFile");
					String fileName = getFileName(filePart);
					String fileURL="";
					if(fileName != null){
						SetFile file = new SetFile();
						fileURL = file.PutFile(request, response, sId,path);
					}
					SessionFeedBackDAO feedback = new SessionFeedBackDAO();
					Boolean isFeedbackCommit= feedback.SetUserFeedBackAgain(sId,body,subject,fileURL);
					if(isFeedbackCommit){
						 String subjects = "Session FeedBack given by User again after rejection";
						 String content = "Hi,<br>A session feedback has been recieved again after rejection for: <br>Session Id : " +sId+ "<br>Given By : USER"+"<br><img src=\"http://www.advisorcircuit.com/Test/assets/img/logo_black.png\" style='float:right' width='25%'>";
						 SendMail mail = new SendMail(subjects, content, "admin@advisorcircuit.com","admin@advisorcircuit.com");
						 mail.start();
						 response.getWriter().write("<p style='color: #c84c4e'>Your Feedback has been sent</p>");
					}
				}else{
					response.getWriter().write("<p style='color: #c84c4e'>Your Feedback has already been sent</p>");
				}
			}
		}else if (sId != null && type != null &&  reject == null &&  again == null && email==null ) {
			//update the user feedback status as approved
			SessionFeedBackDAO approve = new SessionFeedBackDAO();
			Boolean isStatusCommit = approve.SetFeedBackStatus(sId,type);
			if(isStatusCommit){
				response.getWriter().write("Feedback has been approved");
			}
		}else if (sId != null && body!= null && subject != null && advisor != null && advisor.equals("true")  &&  reject == null && again == null && email==null) {
			Properties prop = new Properties();
		    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Path.properties");
		    prop.load(resourceAsStream);
			String path = prop.getProperty("SESSION_FEEDBACK_FILE_PATH");
			Part filePart = request.getPart("myFile");
			String fileName = getFileName(filePart);
			String fileURL="";
			if(fileName != null){
				SetFile file = new SetFile();
				fileURL = file.PutFile(request, response, sId,path);
			}
			//Delete previous 
			SessionFeedBackDAO feedback = new SessionFeedBackDAO();
			Boolean isFeedbackCommit= feedback.SetAdvisorFeedBack(sId,body,subject,fileURL);
			if(isFeedbackCommit){
				 String subjects = "Session FeedBack Reply By Advisor";
				 String content = "Hi,<br>A session feedback reply has been recieved for:<br>Session Id : " +sId+ "<br>Given By : ADVISOR"+"<br><img src=\"http://www.advisorcircuit.com/Test/assets/img/logo_black.png\" style='float:right' width='25%'>";
				 SendMail mail = new SendMail(subjects, content, "admin@advisorcircuit.com","admin@advisorcircuit.com");
				 mail.start();
				 response.getWriter().write("<p style='color: #c84c4e'>Your Feedback has been sent</p>");
			}
			
		}else if(sId != null && action != null &&  reject == null && again == null && email==null){
			Boolean showfeedback = false;
			if(action.equals("show")){
				SessionFeedBackDAO feedback = new SessionFeedBackDAO();
				showfeedback = feedback.SessionFeedback(sId,true);
				if(showfeedback){
					response.getWriter().write("Follow up mail is now visible");
				}	
			}else{
				SessionFeedBackDAO feedback = new SessionFeedBackDAO();
				showfeedback = feedback.SessionFeedback(sId,false);
				if(showfeedback){
					response.getWriter().write("Follow up mail is now hidden");
				}	
			}
				
		}else if (sId != null && type != null &&  reject != null && again == null && email==null ) {
			
			SessionFeedBackDAO rejects = new SessionFeedBackDAO();
			Boolean isStatusCommit = rejects.SetFeedBackStatusRejected(sId,type);
			if(isStatusCommit){
				if(type.equals("user")){
					response.getWriter().write("Feedback has been rejected. Please send offline mail to the user with the reason of rejection");
				}else {
					response.getWriter().write("Feedback has been rejected. Please send offline mail to the advisor with the reason of rejection");
				}
			}
		}else if (sId != null && body!= null && subject != null && email != null && email.equals("user")) {
			//Check if the user has already sent the feedback
			SessionFeedBackDAO checkmail = new SessionFeedBackDAO();
			Boolean isFeedBackGiven = checkmail.CheckMail(sId);
			if(!isFeedBackGiven){
				Properties prop = new Properties();
			    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Path.properties");
			    prop.load(resourceAsStream);
				String path = prop.getProperty("SESSION_EMAIL_FILE_PATH");
				Part filePart = request.getPart("myFile");
				String fileName = getFileName(filePart);
				String fileURL="";
				if(fileName != null){
					SetFile file = new SetFile();
					fileURL = file.PutFile(request, response, sId,path);
				}
				SessionFeedBackDAO feedback = new SessionFeedBackDAO();
				Boolean isFeedbackCommit= feedback.SetUserEmail(sId,body,subject,fileURL);
				if(isFeedbackCommit){
					 String subjects = "Session Mail given by User";
					 String content = "Hi,<br>A session mail has been recieved for: <br>Session Id : " +sId+ "<br>Given By : USER"+"<br><img src=\"http://www.advisorcircuit.com/Test/assets/img/logo_black.png\" style='float:right' width='25%'>";
					 SendMail mail = new SendMail(subjects, content, "admin@advisorcircuit.com","admin@advisorcircuit.com");
					 mail.start();
					 response.getWriter().write("<p style='color: #c84c4e'>Your Mail has been sent</p>");
				}
				
			}else{
				if(again != null && again.equals("true")){
					Properties prop = new Properties();
				    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Path.properties");
				    prop.load(resourceAsStream);
					String path = prop.getProperty("SESSION_EMAIL_FILE_PATH");
					Part filePart = request.getPart("myFile");
					String fileName = getFileName(filePart);
					String fileURL="";
					if(fileName != null){
						SetFile file = new SetFile();
						fileURL = file.PutFile(request, response, sId,path);
					}
					SessionFeedBackDAO feedback = new SessionFeedBackDAO();
					Boolean isFeedbackCommit= feedback.SetUserMailAgain(sId,body,subject,fileURL);
					if(isFeedbackCommit){
						 String subjects = "Session Mail given by User again after rejection";
						 String content = "Hi,<br>A session Mail has been recieved again after rejection for: <br>Session Id : " +sId+ "<br>Given By : USER"+"<br><img src=\"http://www.advisorcircuit.com/Test/assets/img/logo_black.png\" style='float:right' width='25%'>";
						 SendMail mail = new SendMail(subjects, content, "admin@advisorcircuit.com","admin@advisorcircuit.com");
						 mail.start();
						 response.getWriter().write("<p style='color: #c84c4e'>Your Mail has been sent</p>");
					}
					
				}else{
					response.getWriter().write("Your Mail has already been sent");
				}
			}
		}else if (sId != null && body!= null && subject != null && email != null && email.equals("advisor")) {
			Properties prop = new Properties();
		    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Path.properties");
		    prop.load(resourceAsStream);
			String path = prop.getProperty("SESSION_EMAIL_FILE_PATH");
			Part filePart = request.getPart("myFile");
			String fileName = getFileName(filePart);
			String fileURL="";
			if(fileName != null){
				SetFile file = new SetFile();
				fileURL = file.PutFile(request, response, sId,path);
			}
			//Delete previous 
			SessionFeedBackDAO feedback = new SessionFeedBackDAO();
			Boolean isFeedbackCommit= feedback.SetAdvisorMail(sId,body,subject,fileURL);
			if(isFeedbackCommit){
				 String subjects = "Session Mail Reply By Advisor";
				 String content = "Hi,<br>A session mail reply has been recieved for:<br>Session Id : " +sId+ "<br>Given By : ADVISOR"+"<br><img src=\"http://www.advisorcircuit.com/Test/assets/img/logo_black.png\" style='float:right' width='25%'>";
				 SendMail mail = new SendMail(subjects, content, "admin@advisorcircuit.com","admin@advisorcircuit.com");
				 mail.start();
				 response.getWriter().write("<p style='color: #c84c4e'>Your Mail has been sent</p>");
			}
		}else if (sId != null && target != null && isApprove != null) {
			
			if(isApprove.equals("true")){
				//update the user feedback status as approved
				SessionFeedBackDAO approve = new SessionFeedBackDAO();
				Boolean isStatusCommit = approve.SetApproveMailStatus(sId,target);
				if(isStatusCommit){
					response.getWriter().write("Mail has been approved");
				}
			}else{
				//update the user feedback status as approved
				SessionFeedBackDAO rejected = new SessionFeedBackDAO();
				Boolean isStatusCommit = rejected.SetRejectMailStatus(sId,target);
				if(isStatusCommit){
					if(target.equals("advisor")){
						response.getWriter().write("Mail has been rejected. Please send offline mail to the advisor with the reason of rejection");
					}else{
						response.getWriter().write("Mail has been rejected. Please send offline mail to the user with the reason of rejection");
					}
				}
			}
		}
		logger.info("Exit doPost method of SessionFeedBackController");
	}
	
	private String getFileName(final Part part) {
		logger.info("Entered getFileName method of SetFile");			
		try{
		    for (String content : part.getHeader("content-disposition").split(";")) {
		        if (content.trim().startsWith("filename")) {
		            return content.substring(
		                    content.indexOf('=') + 1).trim().replace("\"", "");
		        }
		    }
			logger.info("Exit getFileName method of SetFile");			
		}catch(Exception e){
			logger.error("getFileName method of SetFile threw error:"+e.getMessage());
			e.printStackTrace();
		}
	    return null;
	}
}