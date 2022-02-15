package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;

public interface EmployeeScheduleRepository extends CrudRepository<EmployeeSchedule, Long> {

  EmployeeSchedule findById(long id);

}
