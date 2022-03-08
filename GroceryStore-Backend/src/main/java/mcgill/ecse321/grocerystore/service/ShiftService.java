
package mcgill.ecse321.grocerystore.service;



import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.Shift;

public class ShiftService {
  @Autowired
  ShiftRepository shiftRepo;


  @Transactional
  public Shift createShift(String name, Time startTime, Time endTime)
      throws IllegalArgumentException {


    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift name cannot be empty.");
    }
    if (this.shiftRepo.findByName(name) != null) {
      throw new IllegalArgumentException("Name is already taken.");
    }
    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time");
    }



    Shift Shift = new Shift();
    Shift.setName(name);
    Shift.setStartTime(startTime);
    Shift.setEndTime(endTime);



    return shiftRepo.save(Shift);
  }

  @Transactional
  // Get the shift with the specific name

  public Shift getShift(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift name cannot be empty.");
    }

    if (shiftRepo.findByName(name) == null) {

      throw new IllegalArgumentException("Shift with this name does not exist.");
    }

    return shiftRepo.findByName(name);
  }



  @Transactional
  // Get all the shifts sorted by name in order
  public List<Shift> getAllShiftsByNameInOrder(String name) {
    return toList(shiftRepo.findByNameIgnoreCaseContainingOrderByName(name));
  }

  @Transactional
  public List<Shift> getAllShiftsByNameInReverseOrder(String name) {
    return toList(shiftRepo.findByNameIgnoreCaseContainingOrderByNameDesc(name));
  }


  // Get all the shifts before a given time
  @Transactional
  public List<Shift> getAllShiftsBefore(Time beforeTime) throws IllegalArgumentException {
    if (beforeTime == null) {
      throw new IllegalArgumentException("The input time shall not be null");
    }
    if (shiftRepo.findByEndTimeBefore(beforeTime) == null) {
      throw new IllegalArgumentException("No shift starts before this time in the repository");
    }
    return toList(shiftRepo.findByEndTimeBefore(beforeTime));

  }

  // Get all the shifts after a given time
  @Transactional
  public List<Shift> getAllShiftsAfter(Time afterTime) throws IllegalArgumentException {
    if (afterTime == null) {
      throw new IllegalArgumentException("The input time shall not be null");
    }
    if (shiftRepo.findByStartTimeAfter(afterTime) == null) {
      throw new IllegalArgumentException("No shift starts after this time in the repository");
    }
    return toList(shiftRepo.findByStartTimeAfter(afterTime));

  }


  @Transactional
  public Shift deleteShift(Shift shift) throws IllegalArgumentException {
    if (shift == null) {
      throw new IllegalArgumentException("Input shift cannot be null");
    }
    shiftRepo.delete(shift);
    return shift;
  }


  @Transactional
  public Shift deleteShiftByName(String name) {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift name cannot be empty.");
    }
    if (this.shiftRepo.findByName(name) == null) {
      throw new IllegalArgumentException("Shift with this name does not exist.");
    }
    this.shiftRepo.delete(this.shiftRepo.findByName(name));
    return null;
  }


  @Transactional
  public Shift updateShift(Shift shift, String name, Time startTime, Time endTime) {
    // Make sure every argument is valid


    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift must have a name.");
    }

    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time.");
    }
    shift.setEndTime(endTime);
    shift.setStartTime(startTime);
    shift.setName(name);
    this.shiftRepo.save(shift);
    shift = null;
    return shift;
  }


  private <T> List<T> toList(Iterable<T> iterable) {
    List<T> resultList = new ArrayList<T>();
    for (T t : iterable) {
      resultList.add(t);
    }
    return resultList;
  }
}
