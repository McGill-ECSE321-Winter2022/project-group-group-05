package mcgill.ecse321.grocerystore.dao;

import java.sql.Time;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Shift;

public interface ShiftRepository extends CrudRepository<Shift, String> {

  Shift findByName(String name);


  List<Shift> findByNameIgnoreCaseContainingOrderByName(String nameFragment);

  List<Shift> findByNameIgnoreCaseContainingOrderByNameDesc(String nameFragment);


  List<Shift> findByStartTimeAfter(Time time);

  List<Shift> findByEndTimeBefore(Time time);


  List<Shift> findAllByOrderByName();



}
