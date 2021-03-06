/*************************************************************************************************
 * ********************************ADVISOR CIRCUIT*************************************************
 * ************************************************************************************************
 * @author AdvisorCircuit
 * @Date 30/11/2014
 * ************************************************************************************************/

package org.AC.controller;

import java.io.IOException;
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
import org.AC.DAO.MyAccountRequestDAO;
import org.AC.DAO.UserDetailsDAO;
import org.AC.DAO.UserNotificationDAO;
import org.AC.Util.GetRelativeImageURL;
import org.AC.dto.AdvisorNewDatesDTO;
import org.AC.dto.AdvisorProfileDTO;
import org.AC.dto.SessionDTO;
import org.AC.dto.UserDetailsDTO;
import org.AC.dto.UserRequestDTO;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/* *******************************CLASS SUMMARY****************************************************
 * 
 * This class will fetch all the request details to show on the view details page.
 * 
 * 
 *
 ***************************************************************************************************/
/**
 * Servlet implementation class UserMyAccountRequestViewDetailsController
 */
@WebServlet("/UserMyAccountRequestViewDetailsController")
public class UserMyAccountRequestViewDetailsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(UserMyAccountRequestViewDetailsController.class);

	/**************************************
	 * COMMENTS***************************************************** This method
	 * will fetch all the request details to show on the view details page.
	 * 
	 * @return :None
	 * @param : HttpServletRequest request HttpServletResponse response
	 * 
	 *
	 ***************************************************************************************************/
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.info("Entered doGet method of UserMyAccountRequestViewDetailsController");
		String rId = "";
		int aId = 0;
		int uId = 0;
		String advisorName = "";
		String picture = "";
		String userName = "";
		String relImage = "";
		String status = "";
		int sessionId = 0;
		String mode = "";
		rId = (String) request.getParameter("rId");
		try {
			uId = (int) request.getSession().getAttribute("userId");
		} catch (Exception e) {
			response.sendRedirect("Error");
		}
		if (rId != null && !("").equals(rId)) {
			List<UserRequestDTO> list1 = new ArrayList<UserRequestDTO>();
			List<AdvisorProfileDTO> list2 = new ArrayList<AdvisorProfileDTO>();
			List<UserDetailsDTO> list3 = new ArrayList<UserDetailsDTO>();
			List<SessionDTO> sessionDetails = new ArrayList<SessionDTO>();
			List<AdvisorNewDatesDTO> newDate = new ArrayList<AdvisorNewDatesDTO>();

			// Getting details of the user and user request using the requestId
			// from the userrequest table.
			MyAccountRequestDAO dao = new MyAccountRequestDAO();
			list1 = dao.getUserRequestDetails(rId);
			for (UserRequestDTO userRequestDTO : list1) {
				aId = userRequestDTO.getAdvisorId();
				status = userRequestDTO.getStatus();
				mode = userRequestDTO.getMode();
				if (!userRequestDTO.getMode().equals("email")) {
					userRequestDTO.setTimeString1(new SimpleDateFormat(
							"dd-MMM-yyyy' 'h:mm a").format(new Date(
							userRequestDTO.getTime1().getTime())));
					userRequestDTO.setTimeString2(new SimpleDateFormat(
							"dd-MMM-yyyy' 'h:mm a").format(new Date(
							userRequestDTO.getTime2().getTime())));
					userRequestDTO.setTimeString3(new SimpleDateFormat(
							"dd-MMM-yyyy' 'h:mm a").format(new Date(
							userRequestDTO.getTime3().getTime())));
					userRequestDTO.setTimeString4(new SimpleDateFormat(
							"dd-MMM-yyyy' 'h:mm a").format(new Date(
							userRequestDTO.getTime4().getTime())));
				} else {
					userRequestDTO.setTimeString1(new SimpleDateFormat(
							"dd-MMM-yyyy").format(new Date(userRequestDTO
							.getTime1().getTime())));
				}
				double discount = userRequestDTO.getDiscount()
						* userRequestDTO.getPrice() / 100;
				userRequestDTO.setDiscount((int) discount);
			}
			if (aId != 0 && uId != 0) {
				// After getting the advisorId and userId we need to get the
				// advisor photo and advisor name.
				MyAccountRequestDAO name = new MyAccountRequestDAO();
				list2 = name.getAdvisorName(aId);
				for (AdvisorProfileDTO advisorProfileDTO : list2) {
					advisorName = advisorProfileDTO.getName();
					picture = advisorProfileDTO.getImage();
				}
				MyAccountRequestDAO image = new MyAccountRequestDAO();
				list3 = image.getUserImage(uId);
				for (UserDetailsDTO userDetailsDTO : list3) {
					userName = userDetailsDTO.getFullName();
				}
			}
			if (!("").equals(picture)) {
				GetRelativeImageURL image = new GetRelativeImageURL();
				relImage = image.getImageURL(picture);
			}
			// Check if new dates are submitted by the advisor and getting the
			// session plan
			if (("REQUEST ACCEPTED BY ADVISOR WITH NEW DATES").equals(status)
					|| ("REQUEST ACCEPTED BY ADVISOR").equals(status)) {
				// Retrieving the session details using the request ID
				UserDetailsDAO session = new UserDetailsDAO();
				sessionDetails = session.getSessionDetails(rId);
				for (SessionDTO sessionDTO : sessionDetails) {
					sessionId = sessionDTO.getSessionId();
					if (("REQUEST ACCEPTED BY ADVISOR").equals(status)) {
						if (mode.equals("email")) {
							sessionDTO
									.setAcceptedDateString(new SimpleDateFormat(
											"dd-MMM-yyyy").format(new Date(
											sessionDTO.getAcceptedDate()
													.getTime())));
						} else {
							sessionDTO
									.setAcceptedDateString(new SimpleDateFormat(
											"dd-MMM-yyyy' 'h:mm a")
											.format(new Date(sessionDTO
													.getAcceptedDate()
													.getTime())));
						}
					}
				}
				if (("REQUEST ACCEPTED BY ADVISOR WITH NEW DATES")
						.equals(status)) {
					// Get the new Dates Submitted by the advisor
					UserDetailsDAO newDates = new UserDetailsDAO();
					newDate = newDates.getNewDates(sessionId);
				}
				for (AdvisorNewDatesDTO advisorNewDatesDTO : newDate) {

					if (mode.equals("email")) {
						advisorNewDatesDTO
								.setNewDateString1(new SimpleDateFormat(
										"dd-MMM-yyyy").format(new Date(
										advisorNewDatesDTO.getNewDate1()
												.getTime())));
					} else {
						advisorNewDatesDTO
								.setNewDateString1(new SimpleDateFormat(
										"dd-MMM-yyyy' 'h:mm a")
										.format(new Date(advisorNewDatesDTO
												.getNewDate1().getTime())));
						advisorNewDatesDTO
								.setNewDateString2(new SimpleDateFormat(
										"dd-MMM-yyyy' 'h:mm a")
										.format(new Date(advisorNewDatesDTO
												.getNewDate2().getTime())));
						advisorNewDatesDTO
								.setNewDateString3(new SimpleDateFormat(
										"dd-MMM-yyyy' 'h:mm a")
										.format(new Date(advisorNewDatesDTO
												.getNewDate3().getTime())));
					}
				}
			}
			
			String url =  request.getRequestURI() +"?" +request.getQueryString();
			url = url.substring(url.lastIndexOf('/')+1);
			UserNotificationDAO notify = new UserNotificationDAO();
			notify.SetNotificationRead(url, uId);
			if(!("").equals(advisorName) && !("").equals(picture) && !("").equals(userName) && list1.size() > 0){
				String uId1 = Integer.toString(uId);
				request.setAttribute("uId", uId1);
				request.setAttribute("advisorname", advisorName);
				request.setAttribute("userName", userName);
				request.setAttribute("image", relImage);
				request.setAttribute("list", list1);
				request.setAttribute("sessionDetails", sessionDetails);
				request.setAttribute("newDate", newDate);
				RequestDispatcher rd = getServletContext()
						.getRequestDispatcher("/UserRequest_ViewDetails.jsp");
				rd.forward(request, response);
			}

		}
		logger.info("Exit doGet method of UserMyAccountRequestViewDetailsController");

	}
}
