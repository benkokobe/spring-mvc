// FINAL
package com.apress.springrecipes.court.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

import com.apress.springrecipes.court.domain.Player;
import com.apress.springrecipes.court.domain.Reservation;
import com.apress.springrecipes.court.domain.ReservationValidator;
import com.apress.springrecipes.court.domain.SportType;
import com.apress.springrecipes.court.domain.SportTypeEditor;
import com.apress.springrecipes.court.service.ReservationService;

@Controller
@RequestMapping("/reservationForm")
@SessionAttributes("reservation")
public class ReservationFormController {

	private ReservationService reservationService;
	private ReservationValidator reservationValidator;

	@Autowired
	public ReservationFormController(ReservationService reservationService, ReservationValidator reservationValidator) {
		this.reservationService = reservationService;
		this.reservationValidator = reservationValidator;
	}
	
	  // Create attribute for model 
    // Will be represented as drop box Sport Types in reservationForm
    @ModelAttribute("sportTypes")
    public List<SportType> populateSportTypes() {
        return reservationService.getAllSportTypes();

    }

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(@RequestParam(required = false, value = "username") String username,
            Model model) {

		Reservation reservation = new Reservation();
		reservation.setPlayer(new Player(username, null));
		// Add reservation to model so it can be display in view
		model.addAttribute("reservation", reservation);

		return "reservationForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	// Model reservation object, BindingResult and SessionStatus as parameters
	public String submitForm(
			@ModelAttribute("reservation") Reservation reservation,
			BindingResult result, SessionStatus status) {
		// User is finished validate reservation object
        reservationValidator.validate(reservation, result);
        System.out.println("Validation ... start ...");
        if (result.hasErrors()) {
    	    // Errors, return to reservationForm view
        	System.out.println("HAS error!!!! ERROR!!!");
                return "reservationForm";
            } else {
            	System.out.println("OK OK NO ERROR");
            	reservationService.make(reservation);
            	status.setComplete();
            	// Redirect to reservationSuccess URL, defined in ReservationSuccessController
            	return "reservationSuccess";
            }
	}
	
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                dateFormat, true));
        binder.registerCustomEditor(SportType.class, new SportTypeEditor(reservationService));
    }
}
