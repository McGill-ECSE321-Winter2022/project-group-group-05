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
import mcgill.ecse321.grocerystore.dto.ShiftDto;
import mcgill.ecse321.grocerystore.model.Shift;
import mcgill.ecse321.grocerystore.service.ShiftService;

/**
 * RESTful API for Shift service methods
 * 
 * @author Yida Pan
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class ShiftController {

  @Autowired
  private ShiftService service;


  @PostMapping(value = {"/shift/{name}", "/shift/{name}/"})
  public ShiftDto createShift(@PathVariable("name") String name,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime startTime,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime endTime)
      throws IllegalArgumentException {
    return convertToDto(service.createShift(name, Time.valueOf(startTime), Time.valueOf(endTime)));
  }

  @DeleteMapping(value = {"/shift/{name}", "/shift/{name}/"})
  public void deleteShift(@PathVariable("name") String name) throws IllegalArgumentException {
    service.deleteShiftByName(name);
  }

  @PatchMapping(value = {"/shift/{name}", "/shift/{name}/"})
  public ShiftDto updateShift(@PathVariable("name") String name,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime startTime,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME,
          pattern = "HH:mm") LocalTime endTime)
      throws IllegalArgumentException {
    Shift shift = service.getShift(name);
    if (startTime == null) {
      startTime = shift.getStartTime().toLocalTime();
    }
    if (endTime == null) {
      endTime = shift.getEndTime().toLocalTime();
    }
    return convertToDto(service.updateShift(name, Time.valueOf(startTime), Time.valueOf(endTime)));
  }

  @GetMapping(value = {"/shift/{name}", "/shift/{name}/"})
  public ShiftDto getShift(@PathVariable("name") String name) throws IllegalArgumentException {
    return convertToDto(service.getShift(name));
  }

  @GetMapping(value = {"/shift/getAll", "/shift/getAll/"})
  public List<ShiftDto> getAllShifts() {
    ArrayList<ShiftDto> shifts = new ArrayList<ShiftDto>();
    for (var shift : service.getAllShifts()) {
      shifts.add(convertToDto(shift));
    }
    return shifts;
  }

  /**
   * Converts domain objects to data transfer objects
   * 
   * @param shift - shift instance to be converted
   * @return shift parameter as a DTO
   */
  private ShiftDto convertToDto(Shift shift) throws IllegalArgumentException {
    if (shift.equals(null)) {
      throw new IllegalArgumentException("Invalid shift.");
    }
    return new ShiftDto(shift.getName(), shift.getStartTime(), shift.getEndTime());
  }

}
