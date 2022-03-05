package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {

	public void insert(Department obj);

	public void update(Department obj);

	public void deleteById(Department obj);

	Department findById(Integer id);/* Retronando um Department */

	List<Department> findAll();/* Retornar todos os departamentos tem que ser umalista */
}

	
