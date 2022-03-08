package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.HolidayRepository;
import mcgill.ecse321.grocerystore.model.Holiday;

@Service
public class HolidayService {
  @Autowired
  HolidayRepository holidayRepository;


  @Transactional
  public Holiday createHoliday(String name, int month, int day) {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Name cannot be empty!");
    }
    if (holidayRepository.findByName(name) != null) {
      throw new IllegalArgumentException("Holiday already exists!");
    }
    if ((month == 0) || (month > 12)) {
      throw new IllegalArgumentException("Month is out of range!");
    }
    if ((day == 0) || (day > 31)) {
      throw new IllegalArgumentException("Day is out of range!");
    }

    Holiday holiday = new Holiday();
    holiday.setName(name);
    holiday.setMonth(month);
    holiday.setDay(day);
    holidayRepository.save(holiday);
    return holiday;
  }

  @Transactional
  public Holiday getHoliday(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Name cannot be empty!");
    }
    Holiday holiday = holidayRepository.findByName(name);
    if (holiday == null) {
      throw new IllegalArgumentException("Holiday does not exist!");
    }
    return holiday;
  }

  @Transactional
  public void deleteHoliday(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Name cannot be empty!");
    }
    Holiday holidy = getHoliday(name);
    holidayRepository.delete(holidy);
  }

  @Transactional
  public Holiday updateHoliday(Holiday holiday, String name, int month, int day) {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Name cannot be empty!");
    }
    if ((month == 0) || (month > 12)) {
      throw new IllegalArgumentException("Month is out of range!");
    }
    if ((day == 0) || (day > 31)) {
      throw new IllegalArgumentException("Day is out of range!");
    }
    holiday.setName(name);
    holiday.setMonth(month);
    holiday.setDay(day);
    this.holidayRepository.save(holiday);
    holiday = null;
    return holiday;
  }


  @Transactional
  public List<Holiday> getAllHoliday() {
    ArrayList<Holiday> holidayList = holidayRepository.findAllByOrderByName();
    return holidayList;
  }

}


