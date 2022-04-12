package mcgill.ecse321.grocerystore.controller;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.OpeningHoursDto;
import mcgill.ecse321.grocerystore.model.OpeningHours;
import mcgill.ecse321.grocerystore.service.OpeningHoursService;

/**
 * REST API for OpeningHours service methods
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class OpeningHoursController {

  @Autowired
  private OpeningHoursService service;

  /*
   * Create OpeningHours
   */
  @PostMapping(value = {"/openingH/{daysOfWeek}", "/openingH/{daysOfWeek}/"})
  public OpeningHoursDto createOpeningHours(@PathVariable("daysOfWeek") String daysOfWeek,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime startH,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime endH)
      throws IllegalArgumentException {
    return convertToDto(
        service.createOpeningHours(daysOfWeek, Time.valueOf(startH), Time.valueOf(endH)));
  }

  /*
   * Delete OpeningHours
   */
  @DeleteMapping(value = {"/openingH/{daysOfWeek}", "/openingH/{daysOfWeek}/"})
  public void deleteOpeningHours(@PathVariable("daysOfWeek") String daysOfWeek)
      throws IllegalArgumentException {
    service.deleteOpeningHours(daysOfWeek);
  }

  /*
   * Get OpeningHours
   */
  @GetMapping(value = {"/openingH/{daysOfWeek}", "/openingH/{daysOfWeek}/"})
  public OpeningHoursDto getOpeningHours(@PathVariable("daysOfWeek") String daysOfWeek)
      throws IllegalArgumentException {
    return convertToDto(service.getOpeningHours(daysOfWeek));
  }

  /*
   * Update OpeningHours
   */
  @PatchMapping(value = {"/openingH/{daysOfWeek}", "/openingH/{daysOfWeek}/"})
  public OpeningHoursDto updateOpeningHours(@PathVariable("daysOfWeek") String daysOfWeek,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime startH,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime endH)
      throws IllegalArgumentException {
    OpeningHoursDto openingH = getOpeningHours(daysOfWeek);
    if (startH == null) {
      startH = openingH.getStartTime().toLocalTime();
    }
    if (endH == null) {
      endH = openingH.getEndTime().toLocalTime();
    }
    return convertToDto(
        service.updateOpeningHours(daysOfWeek, Time.valueOf(startH), Time.valueOf(endH)));
  }

  /*
   * Get All OpeningHours
   */
  @GetMapping(value = {"/openingH/getAll", "/openingH/getAll/"})
  public List<OpeningHoursDto> getAllOpeningHours() {
    ArrayList<OpeningHoursDto> openingHours = new ArrayList<>();
    for (var openingH : service.getAll()) {
      openingHours.add(convertToDto(openingH));
    }
    return openingHours;
  }

  private OpeningHoursDto convertToDto(OpeningHours openingH) {
    if (openingH == null) {
      throw new IllegalArgumentException("There is no such Opening Hour!");
    }
    OpeningHoursDto openingHDto = new OpeningHoursDto(openingH.getDaysOfWeek(),
        openingH.getStartTime(), openingH.getEndTime());
    return openingHDto;
  }

}
