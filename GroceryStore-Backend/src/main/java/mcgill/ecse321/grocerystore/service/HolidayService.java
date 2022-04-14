package mcgill.ecse321.grocerystore.service;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.HolidayRepository;
import mcgill.ecse321.grocerystore.model.Holiday;

/**
 * RESTful Service methods for Holiday
 * 
 * @author Sibo Huang
 */
@Service
public class HolidayService {
  @Autowired
  HolidayRepository holidayRepository;

  /**
   * Creates a new holiday with the given name and date
   * 
   * @param name name of the holiday
   * @param date date of the holiday
   * @return the new Holiday instance created
   */
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

  /**
   * Returns the holiday with the given name
   * 
   * @param name name of the holiday
   * @return the Holiday instance with the given name
   * @throws IllegalArgumentException
   */
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

  /**
   * Deletes the specific holiday
   * 
   * @param name name of the holiday
   * @throws IllegalArgumentException
   */
  @Transactional
  public void deleteHoliday(String name) throws IllegalArgumentException {
    Holiday holidy = this.getHoliday(name);
    this.holidayRepository.delete(holidy);
  }

  /**
   * Change the date of the specific holiday
   * @param name name of the holiday
   * @param date new date of the holiday
   * @return the Holiday instance after the date is changed
   */
  @Transactional
  public Holiday updateHoliday(String name, Date date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be empty!");
    }
    Holiday holiday = getHoliday(name);
    holiday.setDate(date);
    return this.holidayRepository.save(holiday);
  }

  /**
   * Returns the sorted list of all holidays
   * @return a sorted list (ascending lexicographical order) of Holiday instances
   */
  @Transactional
  public List<Holiday> getAll() {
    return holidayRepository.findAllByOrderByName();
  }

  /**
   * Returns the sorted list of all holidays
   * @return a sorted list (ascending date order)of Holiday instances
   */
  @Transactional
  public List<Holiday> getAllByDate() {
    return holidayRepository.findAllByOrderByDateAsc();
  }

}

