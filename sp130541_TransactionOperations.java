package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import operations.TransactionOperations;

public class sp130541_TransactionOperations implements TransactionOperations {
	
	Connection connection = DB.getInstance().getConnection();
	Statement stmt = null;
	ResultSet rs = null;
	String upit;
	int n;

	@Override
	public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
		
		BigDecimal tr = null;
		upit = "SELECT coalesce( SUM(Iznos) , 0 ) FROM Transakcija WHERE IdKupca = " + buyerId + " AND Iznos > 0";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			tr = rs.getBigDecimal(1);
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( tr.doubleValue() < 0 ) return new BigDecimal(-1);
		return tr;
	}

	@Override
	public BigDecimal getShopTransactionsAmmount(int shopId) {
		
		BigDecimal tr = null;
		upit = "SELECT coalesce( SUM(Iznos) , 0 ) FROM Transakcija WHERE IdProdavnice = " + shopId + " AND Iznos < 0";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			tr = rs.getBigDecimal(1);
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BigDecimal pom = new BigDecimal(-1);
		tr = tr.multiply(pom);
		if ( tr.doubleValue() < 0 ) return new BigDecimal(-1);
		return tr;
	}

	@Override
	public List<Integer> getTransationsForBuyer(int buyerId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdTransakcije FROM Transakcija WHERE IdKupca = " + buyerId + " AND Iznos > 0";
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
		if (n <= 0 ) return null;
		return lista;
	}

	@Override
	public int getTransactionForBuyersOrder(int orderId) {
		
		upit = "SELECT IdTransakcije FROM Transakcija WHERE IdPorudzbine = " + orderId + " AND Iznos > 0";
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
		if ( n <= 0) return -1;
		return n;
	}

	@Override
	public int getTransactionForShopAndOrder(int orderId, int shopId) {
		
		upit = "SELECT IdTransakcije FROM Transakcija WHERE IdProdavnice = " + shopId + " AND IdPorudzbine = " + orderId + " AND Iznos < 0";
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
		if ( n <= 0) return -1;
		return n;
	}

	@Override
	public List<Integer> getTransationsForShop(int shopId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdTransakcije FROM Transakcija WHERE IdProdavnice = " + shopId + " AND Iznos < 0";
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
		if (n <= 0 ) return null;
		return lista;
	}

	@Override
	public Calendar getTimeOfExecution(int transactionId) {
		
		Date datum = null;
		Calendar kal = new GregorianCalendar();
		BigDecimal iznos = null, pom = new BigDecimal(0);
		upit = "SELECT IdPorudzbine, Iznos FROM Transakcija WHERE IdTransakcije = " + transactionId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				iznos = rs.getBigDecimal(2);
				if ( n < 0) return null;
				
				if (iznos.doubleValue() > 0) {
					upit = "SELECT DatumNarudzbine FROM Porudzbina WHERE IdPorudzbine = " + n;
					rs = stmt.executeQuery(upit);
					rs.next();
					datum = rs.getDate(1);
				} else {
					upit = "SELECT DatumIsporuke FROM Porudzbina WHERE IdPorudzbine = " + n;
					rs = stmt.executeQuery(upit);
					rs.next();
					datum = rs.getDate(1);
				}
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		kal.setTime(datum);
		return kal;
	}

	@Override
	public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
		
		BigDecimal uk = null;
		upit = "SELECT Iznos FROM Transakcija WHERE IdPorudzbine = " + orderId + " AND Iznos > 0";
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

	@Override
	public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
		
		BigDecimal uk = null;
		upit = "SELECT coalesce( SUM(Iznos) , 0 ) FROM Transakcija WHERE IdProdavnice = " + shopId + " AND IdPorudzbine = " + orderId + " AND Iznos < 0";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			uk = rs.getBigDecimal(1);
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BigDecimal pom = new BigDecimal(-1);
		uk = uk.multiply(pom);
		return uk;
	}

	@Override
	public BigDecimal getTransactionAmount(int transactionId) {
		
		BigDecimal uk = null;
		upit = "SELECT Iznos FROM Transakcija WHERE IdTransakcije = " + transactionId;
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
		if (uk.doubleValue() < 0) {
			BigDecimal pom = new BigDecimal(-1);
			uk = uk.multiply(pom);
		}
		return uk;
	}

	@Override
	public BigDecimal getSystemProfit() {
		
		BigDecimal uk = null;
		BigDecimal platio = null;
		BigDecimal poslato = null;
		upit = "SELECT coalesce( SUM(UkCena) , 0 ) FROM Porudzbina WHERE Stanje = 'arrived'";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				platio = rs.getBigDecimal(1);
				
				upit = "SELECT coalesce( SUM(T.Iznos) , 0 ) FROM Porudzbina P, Transakcija T WHERE T.IdPorudzbine = P.IdPorudzbine AND P.Stanje = 'arrived' AND T.Iznos < 0";
				rs = stmt.executeQuery(upit);
				rs.next();
				poslato = rs.getBigDecimal(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		uk = platio.add(poslato);
		return uk;
	}

}
