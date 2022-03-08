package mcgill.ecse321.grocerystore.service;

import java.sql.Time;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.OpeningHoursRepository;
import mcgill.ecse321.grocerystore.model.OpeningHours;

@Service
public class OpeningHoursService {
  @Autowired
  OpeningHoursRepository openingHoursRepository;

  @Transactional
  public OpeningHours createOpeningHours(String daysOfWeek, Time startH, Time endH) {

    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    if (openingHoursRepository.findByDaysOfWeek(daysOfWeek) != null) {
      throw new IllegalArgumentException("This opening hour already exist!");
    }
    if (startH == null) {
      throw new IllegalArgumentException("Start time cannot be empty!");
    }
    if (endH == null) {
      throw new IllegalArgumentException("End time cannot be empty!");
    }
    if (startH != null && startH != null && endH.before(startH)) {
      throw new IllegalArgumentException("Start time must be earlier than end time!");
    }

    OpeningHours openingH = new OpeningHours();
    openingH.setDaysOfWeek(daysOfWeek);
    openingH.setStartTime(startH);
    openingH.setEndTime(endH);
    return openingH;
  }

  @Transactional
  public OpeningHours getOpeningHours(String daysOfWeek) {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    OpeningHours openingH = openingHoursRepository.findByDaysOfWeek(daysOfWeek);
    if (openingH == null) {
      throw new IllegalArgumentException("This opneing hour does not exist!");
    }
    return openingH;
  }

  @Transactional
  public OpeningHours updateOpeningHours(OpeningHours openingH, String daysOfWeek, Time startH,
      Time endH) {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    if (startH == null) {
      throw new IllegalArgumentException("Start time cannot be empty!");
    }
    if (endH == null) {
      throw new IllegalArgumentException("End time cannot be empty!");
    }
    if (startH != null && startH != null && endH.before(startH)) {
      throw new IllegalArgumentException("Start time must be earlier than end time!");
    }
    openingH.setDaysOfWeek(daysOfWeek);
    openingH.setStartTime(startH);
    openingH.setEndTime(endH);
    this.openingHoursRepository.save(openingH);
    openingH = null;
    return openingH;
  }


  @Transactional
  public void deleteOpeningHours(String daysOfWeek) throws IllegalArgumentException {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    OpeningHours openingH = getOpeningHours(daysOfWeek);
    openingHoursRepository.delete(openingH);
  }

  @Transactional
  public List<OpeningHours> getAllOpeningHours() {
    ArrayList<OpeningHours> openingHoursList = openingHoursRepository.findAllByOrderByDaysOfWeek();
    return openingHoursList;
  }

}
