package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {/*Vai ter operação staticas*/

	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}
}
