package mcgill.ecse321.grocerystore.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.HolidayDto;
import mcgill.ecse321.grocerystore.model.Holiday;
import mcgill.ecse321.grocerystore.service.HolidayService;

/**
 * REST API for Holiday service methods
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class HolidayController {

  @Autowired
  private HolidayService service;

  /*
   * Create Holiday
   */
  @PostMapping(value = {"/holiday/{name}", "/holiday/{name}/"})
  public HolidayDto createHoliday(@PathVariable("name") String name, @RequestParam Date date)
      throws IllegalArgumentException {
    return convertToDto(service.createHoliday(name, date));
  }

  /*
   * Delete Holiday
   */
  @DeleteMapping(value = {"/holiday/{name}", "/holiday/{name}/"})
  public void deleteHoliday(@PathVariable("name") String name) throws IllegalArgumentException {
    service.deleteHoliday(name);
  }

  /*
   * Get Holiday
   */
  @GetMapping(value = {"/holiday/{name}", "/holiday/{name}/"})
  public HolidayDto getHoliday(@PathVariable("name") String name) throws IllegalArgumentException {
    return convertToDto(service.getHoliday(name));
  }

  /*
   * Update Holiday
   */
  @PatchMapping(value = {"/holiday/{name}", "/holiday/{name}/"})
  public HolidayDto updateHoliday(@PathVariable("name") String name, @RequestParam Date date)
      throws IllegalArgumentException {
    HolidayDto holiday = getHoliday(name);
    if (date == null) {
      date = holiday.getDate();
    }
    return convertToDto(service.updateHoliday(name, date));
  }

  /*
   * Get All Holidays
   */
  @GetMapping(value = {"/holiday/getAll", "/holiday/getAll/"})
  public List<HolidayDto> getAllHolidays() {
    ArrayList<HolidayDto> holidays = new ArrayList<>();
    for (var holiday : service.getAllByDate()) {
      holidays.add(convertToDto(holiday));
    }
    return holidays;
  }

  private HolidayDto convertToDto(Holiday holiday) {
    if (holiday == null) {
      throw new IllegalArgumentException("There is no such Holiday!");
    }
    HolidayDto holidayDto = new HolidayDto(holiday.getName(), holiday.getDate());
    return holidayDto;
  }
}
