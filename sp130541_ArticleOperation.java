package student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import operations.ArticleOperations;

public class sp130541_ArticleOperation implements ArticleOperations {
	
	Connection connection = DB.getInstance().getConnection();

	@Override
	public int createArticle(int shopId, String articleName, int articlePrice) {
		String upit = "INSERT INTO Artikal (IdProdavnice, ImeArtikla, Cena, BrojArtikala) VALUES ( " + shopId + " , '" + articleName + "' , " + articlePrice + " , 0 )";
		try {
			Statement stmt = connection.createStatement();
			int n = stmt.executeUpdate(upit);
			if (n > 0) {
				upit = "SELECT IdArtikla FROM Artikal WHERE ImeArtikla = '" + articleName + "' AND IdProdavnice = " + shopId;
				ResultSet rs = stmt.executeQuery(upit);
				if ( rs.next() ) {
					n = rs.getInt(1);
					rs.close();
					rs = null;
					stmt.close();
					stmt = null;
					return n;
				}
			}
		
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return -1;
		
	}

}
