package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import operations.BuyerOperations;

public class sp130541_BuyerOperations implements BuyerOperations {
	
	Connection connection = DB.getInstance().getConnection();
	Statement stmt = null;
	ResultSet rs = null;
	String upit;
	int n;

	@Override
	public int createBuyer(String name, int cityId) {
		
		upit = "INSERT INTO Kupac (ImeKupca, IdGrada, Kredit) VALUES ( '" + name + "' , " + cityId + " , 0 )";
		try {
			stmt = connection.createStatement();
			n = stmt.executeUpdate(upit);
			if (n > 0) {
				upit = "SELECT IdKupca FROM Kupac WHERE ImeKupca = '" + name + "' AND IdGrada = " + cityId;
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
	public int setCity(int buyerId, int cityId) {
		
		upit = "UPDATE Kupac SET IdGrada = " + cityId + " WHERE IdKupca = " + buyerId;
		try {
			stmt = connection.createStatement();
			n = stmt.executeUpdate(upit);
			stmt.close();
			stmt = null;
			if (n >0) return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getCity(int buyerId) {
		
		upit = "SELECT IdGrada FROM Kupac WHERE IdKupca = " + buyerId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
				return n;
			}
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}

	@Override
	public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
		
		BigDecimal uk = null;
		upit = "SELECT Kredit FROM Kupac WHERE IdKupca = " + buyerId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				uk = rs.getBigDecimal(1);
				uk = uk.add(credit);
				upit = "UPDATE Kupac SET Kredit = " + uk.toString() + " WHERE IdKupca = " + buyerId;
				n = stmt.executeUpdate(upit);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return uk;
	}

	@Override
	public int createOrder(int buyerId) {
		
		upit = "INSERT INTO Porudzbina (IdKupca, Stanje, UkCena) VALUES ( " + buyerId + " , 'created' , 0 )";
		try {
			stmt = connection.createStatement();
			n = stmt.executeUpdate(upit);
			if (n > 0) {
				upit = "SELECT IdPorudzbine FROM Porudzbina WHERE IdKupca = " + buyerId + " AND Stanje = 'created'";
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
	public List<Integer> getOrders(int buyerId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdPorudzbine FROM Porudzbina WHERE IdKupca = "+ buyerId;
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
	public BigDecimal getCredit(int buyerId) {
		
		BigDecimal uk = null;
		upit = "SELECT Kredit FROM Kupac WHERE IdKupca = " + buyerId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				uk = rs.getBigDecimal(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uk;
	}

}
