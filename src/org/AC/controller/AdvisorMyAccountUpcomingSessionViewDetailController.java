 /*************************************************************************************************
 * ********************************ADVISOR CIRCUIT*************************************************
 * ************************************************************************************************
 * @author AdvisorCircuit
 * @Date 30/11/2014
 * ************************************************************************************************/

package org.AC.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.AC.DAO.AdminNotificationDAO;
import org.AC.DAO.AdvisorMyAccountSessionDAO;
import org.AC.DAO.AdvisorNotificationDAO;
import org.AC.DAO.MyAccountRequestDAO;
import org.AC.DAO.SessionFeedBackDAO;
import org.AC.Util.GetRelativeImageURL;
import org.AC.Util.GetTimeLeftForReply;
import org.AC.dto.AdvisorProfileDTO;
import org.AC.dto.FeedbackDTO;
import org.AC.dto.SessionDTO;
import org.AC.dto.SessionFeedBackDTO;
import org.AC.dto.TimeDTO;
import org.AC.dto.UserDetailsDTO;
import org.AC.dto.UserRequestDTO;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/* *******************************CLASS SUMMARY****************************************************
* 
* This class will fetch  session  and user details to display on the view details form.
* 
* 
*
*************************************************************************************`**************/
/**
 * Servlet implementation class AdvisorMyAccountUpcomingSessionViewDetailController
 */
@WebServlet("/AdvisorMyAccountUpcomingSessionViewDetailController")
public class AdvisorMyAccountUpcomingSessionViewDetailController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AdvisorMyAccountUpcomingSessionViewDetailController.class);     
      
	
 	/**************************************COMMENTS***************************************************
	 * This method will fetch  session  and user details to display on the view details form.
	 *   
	 *   @return :None
	 *   @param : HttpServletRequest request
	 *   		  HttpServletResponse response
	 *   		  
	 *
	 ***************************************************************************************************/
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entered doGet method of AdvisorMyAccountUpcomingSessionViewDetailController");
		
		int advisorId = 0;
		String username = "";
		int userId = 0;
		String rId = "";
		String advisorName = "";
		String picture = "";
		String relImage = "";
		String userName= "";
		int sId = 0;
		String details = "";
		String path="";
		String mode="";
		Timestamp acceptedDate = null;
	    rId = (String) request.getParameter("rId");
		try{
	    advisorId = (int) request.getSession().getAttribute("advisorId"); 
	    username = (String) request.getSession().getAttribute("username");	    
		}catch(Exception e){
			response.sendRedirect("Error");
		}
		List<SessionDTO> list = new ArrayList<SessionDTO>();
		if(username != null &&  advisorId != 0 && !("").equals(username)){
			List<UserDetailsDTO> list2 = new ArrayList<UserDetailsDTO>();
			List<UserRequestDTO> list1 = new ArrayList<UserRequestDTO>();
			List<AdvisorProfileDTO> list3 = new ArrayList<AdvisorProfileDTO>();
			FeedbackDTO feed = new FeedbackDTO(); 
			//Getting the session details
			AdvisorMyAccountSessionDAO session = new AdvisorMyAccountSessionDAO();
			list = session.getSessionDetails(rId);
			for (SessionDTO SessionDTO : list) {
				sId = SessionDTO.getSessionId();
				userId = SessionDTO.getUserId();
				acceptedDate = SessionDTO.getAcceptedDate();
				SessionDTO.setAcceptedDateString(new SimpleDateFormat("dd-MMM-yyyy' 'h:mm a").format(new Date(SessionDTO.getAcceptedDate().getTime())));

			}
		
			MyAccountRequestDAO name = new MyAccountRequestDAO();
			list3 = name.getAdvisorName(advisorId);
			for (AdvisorProfileDTO advisorProfileDTO : list3) {
				advisorName = advisorProfileDTO.getName();
			}
			MyAccountRequestDAO image = new MyAccountRequestDAO();
			list2 = image.getUserImage(userId);
			for (UserDetailsDTO userDetailsDTO : list2) {
				userName = userDetailsDTO.getFullName();
				picture = userDetailsDTO.getImage();
			}
			if(!("").equals(picture)){
				GetRelativeImageURL image1 = new GetRelativeImageURL();
				 relImage = image1.getImageURL(picture);
			}
			List<TimeDTO> difference = new ArrayList<TimeDTO>();
			//After retrieving the user details, get the user request details
			MyAccountRequestDAO dao = new MyAccountRequestDAO();
			list1 = dao.getUserRequestDetails(rId);	
			GetTimeLeftForReply time = new GetTimeLeftForReply();
			difference = time.getTimeLeftForSession(acceptedDate);
			Boolean isFeedback = false;
			Boolean isModeDetail = false;
			for (UserRequestDTO userRequestDTO : list1) {
				mode= userRequestDTO.getMode();
				if(userRequestDTO.getService().equals("cvcritique") || userRequestDTO.getService().equals("mockinterview")){
					isFeedback = true;
				}
				if(userRequestDTO.getMode().equals("phone") || userRequestDTO.getMode().equals("webchat")){
					isModeDetail = true;
				}
				if(difference.size() > 0){
					for (TimeDTO timeDTO : difference) {
						userRequestDTO.setDays(timeDTO.getDay());
						userRequestDTO.setHours(timeDTO.getHours());
						userRequestDTO.setMinutes(timeDTO.getMinutes());
					}	
				}else{
					userRequestDTO.setDays(0);
					userRequestDTO.setHours(0);
					userRequestDTO.setMinutes(0);
				}
			}
			String sessionDate = "";
			String date = "";
			String time1 = "";
			if(acceptedDate != null){
				 sessionDate =  acceptedDate.toString();
				 Timestamp timestamp = new Timestamp(acceptedDate.getTime());
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				 SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("h:mm a");
				 date = simpleDateFormat.format(timestamp);
				 if(mode.equals("email")){
					 time1="N/A";
				 }else{
					 time1 = simpleDateFormat1.format(timestamp);
				 }
			}else{
				 date = "NOT FIXED";
				 time1 = "NOT FIXED";
			}
			if(isFeedback){
				//Getting the FeedBack Form Path
				MyAccountRequestDAO form = new MyAccountRequestDAO();
				feed = form.GetFeedbackPath(sId);
			}
			if(isModeDetail){
				//Getting Pin or Link
				MyAccountRequestDAO detail = new MyAccountRequestDAO();
				details = detail.GetModeDetails(sId);
			}
			SessionFeedBackDTO feedUser = new SessionFeedBackDTO();
			SessionFeedBackDTO feedAdvisor = new SessionFeedBackDTO();
			//Check if the user has given any feedback
			SessionFeedBackDAO feedback = new SessionFeedBackDAO();
			feedUser = feedback.GetUserFeedBackDetailsForAdvisor(sId);
			SessionFeedBackDAO feedbackAdvisor = new SessionFeedBackDAO();
			feedAdvisor = feedbackAdvisor.GetAdvisorFeedbackDetailsForAdvisor(sId);
			SessionFeedBackDTO emailUser = new SessionFeedBackDTO();
			SessionFeedBackDTO emailAdvisor = new SessionFeedBackDTO();
			//Check if the user has sent any mail
			SessionFeedBackDAO mail = new SessionFeedBackDAO();
			emailUser = mail.GetUserMailForAdvisor(sId);
			SessionFeedBackDAO mailAdvisor = new SessionFeedBackDAO();
			emailAdvisor = mailAdvisor.GetAdvisorMailForAdvisor(sId);
			
			String url =  request.getRequestURI() +"?" +request.getQueryString();
			url = url.substring(url.lastIndexOf('/')+1);
			AdvisorNotificationDAO notify = new AdvisorNotificationDAO();
			notify.SetNotificationRead(url, advisorId);
			if(list.size() > 0  && list1.size() > 0 && list2.size() > 0) {
				request.setAttribute("advisorname", advisorName);
				request.setAttribute("userName", userName);
				request.setAttribute("image", relImage);
				request.setAttribute("sessions", list);
				request.setAttribute("time", time1);
				request.setAttribute("date", date);
				request.setAttribute("feedUser", feedUser);
				request.setAttribute("feedAdvisor", feedAdvisor);
				request.setAttribute("emailUser", emailUser);
				request.setAttribute("emailAdvisor", emailAdvisor);
				request.setAttribute("details", details);
				request.setAttribute("requests", list1);
				request.setAttribute("userdetails", list2);
				request.setAttribute("sessionDate", sessionDate);
				request.setAttribute("feed", feed);
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Session_ViewDetails.jsp");
		        rd.forward(request, response);
			}
		}
		logger.info("Exit doGet method of AdvisorMyAccountUpcomingSessionViewDetailController");

	}
}
