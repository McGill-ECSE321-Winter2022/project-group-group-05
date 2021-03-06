package mcgill.ecse321.grocerystore.dao;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.OpeningHours;

public interface OpeningHoursRepository extends CrudRepository<OpeningHours, String> {

  OpeningHours findByDaysOfWeek(String daysOfWeek);

  ArrayList<OpeningHours> findAllByOrderByDaysOfWeek();

}
