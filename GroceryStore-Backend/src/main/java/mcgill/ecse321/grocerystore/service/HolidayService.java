package mcgill.ecse321.grocerystore.service;

import java.sql.Date;
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
  public Holiday createHoliday(String name, Date date) {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Name cannot be empty!");
    }
    if (holidayRepository.findByName(name) != null) {
      throw new IllegalArgumentException("Holiday already exists!");
    }
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be empty!");
    }

    Holiday holiday = new Holiday();
    holiday.setName(name);
    holiday.setDate(date);
    return holidayRepository.save(holiday);
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
    Holiday holidy = this.getHoliday(name);
    this.holidayRepository.delete(holidy);
  }

  @Transactional
  public Holiday updateHoliday(String name, Date date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be empty!");
    }
    Holiday holiday = getHoliday(name);
    holiday.setDate(date);
    return this.holidayRepository.save(holiday);
  }

  @Transactional
  public List<Holiday> getAll() {
    return holidayRepository.findAllByOrderByName();
  }

  @Transactional
  public List<Holiday> getAllByDate() {
    return holidayRepository.findAllByOrderByDateAsc();
  }

}

