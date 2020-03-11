package student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import operations.CityOperations;

public class sp130541_CityOperations implements CityOperations {
	
	Connection connection = DB.getInstance().getConnection();
	Statement stmt = null;
	ResultSet rs = null;
	String upit;
	int n;

	@Override
	public int createCity(String name) {
		
		upit = "SELECT COUNT(*) FROM Grad WHERE ImeGrada = '" + name + "'";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			n = rs.getInt(1);
			if ( n > 0) return -1;
			
			upit = "INSERT INTO Grad (ImeGrada) VALUES ( '" + name + "' )";
			n = stmt.executeUpdate(upit);
			if (n > 0) {
				upit = "SELECT IdGrada FROM Grad WHERE ImeGrada = '" + name + "'";
				rs = stmt.executeQuery(upit);
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

	@Override
	public List<Integer> getCities() {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdGrada FROM Grad";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			n = 0;
			while (rs.next()) {
				lista.add(rs.getInt(1));
				n++;
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (n > 0) return lista;
		return null;
	}

	@Override
	public int connectCities(int cityId1, int cityId2, int distance) {
		
		if (cityId1 == cityId2) return -1;
		
		upit = "SELECT COUNT(*) FROM Linija WHERE ( IdGrada1 = " + cityId1 + " AND IdGrada2 = " + cityId2 +
					" ) OR ( IdGrada1 = " + cityId2 + " AND IdGrada2 = " + cityId1 + " )";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			if (rs.getInt(1) <= 0) {
				upit = "INSERT INTO Linija (IdGrada1, IdGrada2, Rastojanje) VALUES ( " + cityId1 + " , " + cityId2 +
						" , "+ distance + " )";
				n = stmt.executeUpdate(upit);
				upit = "SELECT IdLinije FROM Linija WHERE IdGrada1 = " + cityId1 + " AND IdGrada2 = " + cityId2;
				rs = stmt.executeQuery(upit);
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

	@Override
	public List<Integer> getConnectedCities(int cityId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdGrada2 FROM Linija WHERE IdGrada1 = " + cityId;
		try {
			stmt = connection.createStatement();
			rs =stmt.executeQuery(upit);
			while (rs.next()) {
				lista.add(rs.getInt(1));
			}
			upit = "SELECT IdGrada1 FROM Linija WHERE IdGrada2 = " + cityId;
			rs =stmt.executeQuery(upit);
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
	public List<Integer> getShops(int cityId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdProdavnice FROM Prodavnica WHERE IdGrada = " + cityId;
		n = 0;
		try {
			stmt = connection.createStatement();
			rs =stmt.executeQuery(upit);
			while (rs.next()) {
				lista.add(rs.getInt(1));
				n++;
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (n > 0) return lista;
		return null;
	}

}
