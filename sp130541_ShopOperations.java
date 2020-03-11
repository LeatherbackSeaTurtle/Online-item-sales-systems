package student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import operations.ShopOperations;

public class sp130541_ShopOperations implements ShopOperations {
	
	Connection connection = DB.getInstance().getConnection();
	Statement stmt = null;
	ResultSet rs = null;
	String upit;
	int n;


	@Override
	public int createShop(String name, String cityName) {
		
		upit = "SELECT COUNT(*) FROM Prodavnica WHERE ImeProdavnice = '" + name + "'";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			n = rs.getInt(1);
			if ( n > 0 ) return -1;
			
			upit = "SELECT IdGrada FROM Grad WHERE ImeGrada = '" + cityName + "'";
			rs = stmt.executeQuery(upit);
			rs.next();
			n = rs.getInt(1);
			
			upit = "INSERT INTO Prodavnica (ImeProdavnice, IdGrada, Popust, RacunProdavnice) VALUES ( '"+ name + "' , " + n + " , 0 , 0 )";
			int pom = stmt.executeUpdate(upit);
			if ( pom <= 0 ) return -1;
			
			upit = "SELECT IdProdavnice FROM Prodavnica WHERE IdGrada = " + n + " AND ImeProdavnice = '" + name + "'";
			rs = stmt.executeQuery(upit);
			rs.next();
			n = rs.getInt(1);
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n;
	}

	@Override
	public int setCity(int shopId, String cityName) {
		
		upit = "SELECT IdGrada FROM Grad WHERE ImeGrada = '" + cityName + "'";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				if ( n < 0 ) return -1;
				
				upit = "UPDATE Prodavnica SET IdGrada = " + n + " WHERE IdProdavnice = " + shopId;
				n = stmt.executeUpdate(upit);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (n <= 0) return -1;
		return 1;
	}

	@Override
	public int getCity(int shopId) {
		
		upit = "SELECT IdGrada FROM Prodavnica WHERE IdProdavnice = " + shopId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return n;
	}

	@Override
	public int setDiscount(int shopId, int discountPercentage) {
		
		upit = "UPDATE Prodavnica SET Popust = " + discountPercentage + " WHERE IdProdavnice = " + shopId;
		try {
			stmt = connection.createStatement();
			n = stmt.executeUpdate(upit);
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( n <= 0 ) return -1;
		return 1;
	}

	@Override
	public int increaseArticleCount(int articleId, int increment) {
		
		upit = "SELECT BrojArtikala FROM Artikal WHERE IdArtikla = " + articleId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				if ( n < 0 ) return -1;
				
				n += increment;
				if ( n < 0 ) return -1;
				upit = "UPDATE Artikal SET BrojArtikala = " + n + " WHERE IdArtikla = " + articleId;
				stmt.executeUpdate(upit);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n;
	}

	@Override
	public int getArticleCount(int articleId) {
		
		upit = "SELECT BrojArtikala FROM Artikal WHERE IdArtikla = " + articleId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n;
	}

	@Override
	public List<Integer> getArticles(int shopId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdArtikla FROM Artikal WHERE IdProdavnice = " + shopId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			while (rs.next()) {
				lista.add(rs.getInt(1));
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lista;
	}

	@Override
	public int getDiscount(int shopId) {
		
		upit = "SELECT Popust FROM Prodavnica WHERE IdProdavnice = " + shopId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n;
	}

}
