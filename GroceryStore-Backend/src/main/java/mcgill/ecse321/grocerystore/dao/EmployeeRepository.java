package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, String> {

  Employee findByUsername(String name);

}
