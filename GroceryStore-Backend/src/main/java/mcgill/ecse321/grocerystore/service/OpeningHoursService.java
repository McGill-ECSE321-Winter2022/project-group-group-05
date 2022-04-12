package mcgill.ecse321.grocerystore.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.OpeningHoursRepository;
import mcgill.ecse321.grocerystore.model.OpeningHours;

/**
 * Service methods for OpeningHours
 *
 */
@Service
public class OpeningHoursService {
  @Autowired
  OpeningHoursRepository openingHoursRepository;

  @Transactional
  public OpeningHours createOpeningHours(String daysOfWeek, Time startH, Time endH)
      throws IllegalArgumentException {

    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    if ((!daysOfWeek.equals("Monday")) && (!daysOfWeek.equals("Tuesday"))
        && (!daysOfWeek.equals("Wednesday")) && (!daysOfWeek.equals("Thursday"))
        && (!daysOfWeek.equals("Friday")) && (!daysOfWeek.equals("Saturday"))
        && (!daysOfWeek.equals("Sunday"))) {
      throw new IllegalArgumentException("Day of week is not valid!");
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
    if (endH.before(startH)) {
      throw new IllegalArgumentException("Start time must be earlier than end time!");
    }

    OpeningHours openingH = new OpeningHours();
    openingH.setDaysOfWeek(daysOfWeek);
    openingH.setStartTime(startH);
    openingH.setEndTime(endH);
    return openingHoursRepository.save(openingH);
  }

  @Transactional
  public OpeningHours getOpeningHours(String daysOfWeek) throws IllegalArgumentException {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    OpeningHours openingH = openingHoursRepository.findByDaysOfWeek(daysOfWeek);
    if (openingH == null) {
      throw new IllegalArgumentException("This opening hour does not exist!");
    }
    return openingH;
  }

  @Transactional
  public OpeningHours updateOpeningHours(String daysOfWeek, Time startH, Time endH)
      throws IllegalArgumentException {
    if (startH == null) {
      throw new IllegalArgumentException("Start time cannot be empty!");
    }
    if (endH == null) {
      throw new IllegalArgumentException("End time cannot be empty!");
    }
    if (endH.before(startH)) {
      throw new IllegalArgumentException("Start time must be earlier than end time!");
    }
    OpeningHours openingH = getOpeningHours(daysOfWeek);
    openingH.setStartTime(startH);
    openingH.setEndTime(endH);
    return this.openingHoursRepository.save(openingH);
  }


  @Transactional
  public void deleteOpeningHours(String daysOfWeek) throws IllegalArgumentException {
    OpeningHours openingH = getOpeningHours(daysOfWeek);
    openingHoursRepository.delete(openingH);
  }

  @Transactional
  public List<OpeningHours> getAll() {
    ArrayList<OpeningHours> openingHoursList = openingHoursRepository.findAllByOrderByDaysOfWeek();
    return openingHoursList;
  }

}
