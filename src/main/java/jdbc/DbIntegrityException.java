package jdbc;

/*Excessão de integridade do banco de dados 
 * só posso excluir um departamento se não ouver 
 * vendendor
 */
public class DbIntegrityException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DbIntegrityException(String msg) {
		super(msg);
	}

}