package mcgill.ecse321.grocerystore.dao;

import java.sql.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;


public interface EmployeeScheduleRepository extends CrudRepository<EmployeeSchedule, Long> {

  EmployeeSchedule findById(long id);

  List<EmployeeSchedule> findAllByDate(Date date);

  List<EmployeeSchedule> findAllByOrderByDate();

  List<EmployeeSchedule> findAllByOrderByDateDesc();
 

 

}
