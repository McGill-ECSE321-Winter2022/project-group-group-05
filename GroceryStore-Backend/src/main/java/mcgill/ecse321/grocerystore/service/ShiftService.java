
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
  public Shift CreateShift(String name, Time startTime, Time endTime)
      throws IllegalArgumentException {


    if (name == null) {
      throw new IllegalArgumentException("Name must not be null");
    }
    if (this.shiftRepo.findByName(name) != null) {
      throw new IllegalArgumentException("The shift already exist");
    }



    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time");
    }



    Shift Shift = new Shift();
    Shift.setStartTime(startTime);
    Shift.setEndTime(endTime);



    return shiftRepo.save(Shift);
  }

  @Transactional
  // Get the shift with the specific name

  public Shift getShift(String name) throws IllegalArgumentException {
    if (name == null) {
      throw new IllegalArgumentException("Name should not be null");
    }

    if (shiftRepo.findByName(name) == null) {
      throw new IllegalArgumentException("A shift with this name does not exist in the repository");
    }

    return shiftRepo.findByName(name);
  }
  // Get all the shifts stored in the repository


  @Transactional
  public List<Shift> getAllShifts() throws IllegalArgumentException {
    if (shiftRepo.findAll() == null) {
      throw new IllegalArgumentException("No shift exist in the repository");
    }
    return toList(shiftRepo.findAllByOrderByUsername());
  }

  @Transactional
  // Get all the shifts sorted by name in order
  public List<Shift> getAllShiftsByNameInOrder(String name) {
    return toList(shiftRepo.findByNameContainingOrderByName(name));
  }

  @Transactional
  public List<Shift> getAllShiftsByNameInReverse(String name) {
    return toList(shiftRepo.findByNameContainingOrderByNameDesc(name));
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
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null");
    }
    if (this.shiftRepo.findByName(name) == null) {
      throw new IllegalArgumentException("There is no shift with this name in the repository");
    }
    this.shiftRepo.delete(this.shiftRepo.findByName(name));
    return null;
  }

  @Transactional
  public Shift updateShift(Shift shift, String name, Time startTime, Time endTime) {
    // Make sure every argument is valid
    if (startTime == null) {
      throw new IllegalArgumentException("Shift must have a starting time");
    }

    if (endTime == null) {
      throw new IllegalArgumentException("Shift must have a ending time");
    }

    if (name == null) {
      throw new IllegalArgumentException("Shift must have a name");
    }

    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time");
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
