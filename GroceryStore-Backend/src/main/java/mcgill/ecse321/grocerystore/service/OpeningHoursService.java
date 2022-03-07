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
  public OpeningHours createOpeningHours(String daysOfWeek, Time startTime, Time endTime) {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of Week cannot be empty!");
    }
    if (openingHoursRepository.findByDaysOfWeek(daysOfWeek) != null) {
      throw new IllegalArgumentException("This opening hour already exists");
    }
    if (startTime == null || endTime == null) {
      throw new IllegalArgumentException("Start and end time cannot be empty!");
    }
    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Start time must be earlier than end time!");
    }

    OpeningHours openingHours = new OpeningHours();
    openingHours.setDaysOfWeek(daysOfWeek);
    openingHours.setStartTime(startTime);
    openingHours.setEndTime(endTime);
    return openingHours;
  }

  @Transactional
  public OpeningHours getOpeningHours(String daysOfWeek) {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    OpeningHours openingHours = openingHoursRepository.findByDaysOfWeek(daysOfWeek);
    if (openingHours == null) {
      throw new IllegalArgumentException("This opneing hour does not exist!");
    }
    return openingHours;
  }

  @Transactional
  public void deleteOpeningHours(String daysOfWeek) throws IllegalArgumentException {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }
    OpeningHours openingHours = getOpeningHours(daysOfWeek);
    openingHoursRepository.delete(openingHours);
  }

  @Transactional
  public OpeningHours updateOpeningHours(OpeningHours openingHours, String daysOfWeek,
      Time startTime, Time endTime) {
    if (daysOfWeek == null || daysOfWeek.trim().length() == 0) {
      throw new IllegalArgumentException("Day of week cannot be empty!");
    }

    if (startTime == null) {
      throw new IllegalArgumentException("OpeningHours must have a starting time!");
    }

    if (endTime == null) {
      throw new IllegalArgumentException("OpeningHours must have a ending time!");
    }

    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Start time must be earlier than end time!");
    }
    openingHours.setDaysOfWeek(daysOfWeek);
    openingHours.setStartTime(startTime);
    openingHours.setEndTime(endTime);
    this.openingHoursRepository.save(openingHours);
    openingHours = null;
    return openingHours;
  }

  @Transactional
  public List<OpeningHours> getAllOpeningHours() {
    ArrayList<OpeningHours> openingHoursList = openingHoursRepository.findAllByOrderByDaysOfWeek();
    return openingHoursList;
  }

}
