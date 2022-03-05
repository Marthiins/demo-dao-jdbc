package model.dao;

import jdbc.DB;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {/*Vai ter operação staticas*/

	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
}
