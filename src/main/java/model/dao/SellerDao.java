package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {

	public void insert(Seller obj);

	public void update(Seller obj);

	//public void update(Seller obj);
	public void deleteById(Integer id);

	Seller findById(Integer id);/* Retornando um Seller */

	List<Seller> findAll();/* Retornar todos os departamentos tem que ser umalista */

	List<Seller> findByDepartment(Department department);/* Buscar por Departamento */


}
