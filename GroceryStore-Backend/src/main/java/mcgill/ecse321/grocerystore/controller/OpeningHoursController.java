package mcgill.ecse321.grocerystore.controller;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.OpeningHoursDto;
import mcgill.ecse321.grocerystore.model.OpeningHours;
import mcgill.ecse321.grocerystore.service.OpeningHoursService;

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
      @RequestParam Time startH, @RequestParam Time endH) throws IllegalArgumentException {
    return convertToDto(service.createOpeningHours(daysOfWeek, startH, endH));
  }

  /*
   * Delete OpeningHours
   */
  @DeleteMapping(value = {"/openingH/{daysOfWeek}", "/openingH/{daysOfWeek}/"})
  @ResponseStatus(value = HttpStatus.OK)
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
  @ResponseStatus(value = HttpStatus.OK)
  public OpeningHoursDto updateOpeningHours(@PathVariable("daysOfWeek") String daysOfWeek,
      @RequestParam(required = false) Time startH, @RequestParam(required = false) Time endH)
      throws IllegalArgumentException {
    if (startH != null && endH != null)
      service.updateOpeningHours(daysOfWeek, startH, endH);
    return convertToDto(service.getOpeningHours(daysOfWeek));
  }

  /*
   * Get All OpeningHours
   */
  @GetMapping(value = {"/openingH/getAll", "/openingH/getAll/"})
  public List<OpeningHoursDto> getAllOpeningHours() {
    ArrayList<OpeningHoursDto> openingHours = new ArrayList<OpeningHoursDto>();
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
