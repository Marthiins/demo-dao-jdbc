package model.dao.impl;//model.dao.implementação

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		PreparedStatement st = null;
		try { /* Montar o Statement */
			st = conn.prepareStatement(
					"INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) " + "VALUES "
							+ "(?, ?, ?, ?, ?)", /* Esse set 1,2,3,4,5, são as ? */
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());/* Porqeu set string direto porque é o nome */
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthdate().getTime()));/* Instanciando a data SQL */
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());/* Como chegar no departamento do id do vendedor */

			int linhasAfetadas = st.executeUpdate();/* Executar comando sql */

			if (linhasAfetadas > 0) {/* Se linhasAfetadas for maior que zero ele inseriu */
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) { /* Inserindo apenas um dado */
					int id = rs.getInt(1);
					obj.setId(id);
				}

				DB.closeResultSet(rs);
			}

			else {
				throw new DbException("Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try { /* Montar o Statement */
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " 
					+ "WHERE Id = ?");

			st.setString(1, obj.getName());/* Porqeu set string direto porque é o nome */
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthdate().getTime()));/* Instanciando a data SQL */
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());/* Como chegar no departamento do id do vendedor */
			st.setInt(6, obj.getId()); /* Id do vendedor */
			
			st.execute();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
	
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
		/*Configurar o valor do playcehold*/
			st.setInt(1 , id);
			
			st.executeUpdate();/*Fazer verificação*/
				
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}
	

	@Override
	public Seller findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;
		try { /* Iniciando o PreparedStatement */
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " 
			        + "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

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

	/* findAll faz buscar todos os vendedores com nome de departamentos */
	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try { /* Iniciando o PreparedStatement */
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "ORDER BY Name");

			rs = st.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();/* Estrutura Map vazio */

			while (rs.next()) {/* Meu resultado pode ter 0 ou mais valores */

				/*
				 * Criei o map la em cima vazio, vou guardar dentro do Map qualquer departamento
				 * que eu instanciar e a cada vez que passar pelo while vou testar se o
				 * departamento já existe. Como é feito isso vou no map.get e buscar um
				 * departamento que tem o rs.getInt("DepartmentId"
				 */
				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {/* Teste */
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);/* Salvar o departamento dentro do MAP */
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
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, department.getId());

			rs = st.executeQuery();

			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();/* Estrutura Map vazio */

			while (rs.next()) {/* Meu resultado pode ter 0 ou mais valores */

				/*
				 * Criei o map la em cima vazio, vou guardar dentro do Map qualquer departamento
				 * que eu instanciar e a cada vez que passar pelo while vou testar se o
				 * departamento já existe. Como é feito isso vou no map.get e buscar um
				 * departamento que tem o rs.getInt("DepartmentId"
				 */
				Department dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {/* Teste */
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);/* Salvar o departamento dentro do MAP */
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
