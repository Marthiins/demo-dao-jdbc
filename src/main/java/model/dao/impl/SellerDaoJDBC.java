package model.dao.impl;//model.dao.implementação

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.DB;
import jdbc.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	/* Dependencia com o banco de Dados */
	private Connection conn;

	/* Construtor obrigatório */
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteById(Seller obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public Seller findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		try { /* Iniciando o PreparedStatement */
			st = conn.prepareStatement(
			"SELECT seller.*,department.Name as DepName " 
			+ "FROM seller INNER JOIN department "
			+ "ON seller.DepartmentId = department.Id " 
			+ "WHERE seller.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs, dep);
				return obj;

			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthdate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	
	/*findAll faz buscar todos os vendedores com nome de departamentos*/
	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try { /* Iniciando o PreparedStatement */
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+"FROM seller INNER JOIN department "
					+"ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
		
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();/*Estrutura Map vazio*/
			
			while (rs.next()) {/*Meu resultado pode ter 0 ou mais valores*/
			
				/*Criei o map la em cima vazio, vou guardar 
				 * dentro do Map qualquer departamento que eu instanciar
				 * e a cada vez que passar pelo while vou testar
				 * se o departamento já existe.
				 * Como é feito isso vou no map.get e buscar um departamento que tem o rs.getInt("DepartmentId" */
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {/*Teste*/
				dep  = instantiateDepartment(rs);
				map.put(rs.getInt("DepartmentId"), dep);/*Salvar o departamento dentro do MAP*/
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);

			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try { /* Iniciando o PreparedStatement */
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+"FROM seller INNER JOIN department "
					+"ON seller.DepartmentId = department.Id "
					+"WHERE DepartmentId = ? "
					+ "ORDER BY Name");

			st.setInt(1, department.getId());
		
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();/*Estrutura Map vazio*/
			
			while (rs.next()) {/*Meu resultado pode ter 0 ou mais valores*/
			
				/*Criei o map la em cima vazio, vou guardar 
				 * dentro do Map qualquer departamento que eu instanciar
				 * e a cada vez que passar pelo while vou testar
				 * se o departamento já existe.
				 * Como é feito isso vou no map.get e buscar um departamento que tem o rs.getInt("DepartmentId" */
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if(dep == null) {/*Teste*/
				dep  = instantiateDepartment(rs);
				map.put(rs.getInt("DepartmentId"), dep);/*Salvar o departamento dentro do MAP*/
				}

				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);

			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

}
