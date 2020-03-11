package student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.sql.Date;
import operations.OrderOperations;

public class sp130541_OrderOperation implements OrderOperations {
	
	int [] ids = null;
	int [][] D = null;
	int [][] T = null;
	
	
	Connection connection = DB.getInstance().getConnection();
	Statement stmt = null;
	ResultSet rs = null;
	String upit;
	int n;
	
	public int inicijalizujNizove() {
		
		int i = 0, j = 0, k = 0, grad1 = 0, grad2 = 2;
		upit = "SELECT COUNT(*) FROM Grad";
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				n = rs.getInt(1);
				if (n <= 0) return -1;
				ids = new int[n];
				D = new int[n][n];
				T = new int[n][n];
				
				upit = "SELECT IdGrada FROM Grad";
				rs = stmt.executeQuery(upit);
				i = 0;
				while (rs.next()) {
					ids[i] = rs.getInt(1);
					i++;
				}
				for ( i = 0; i < n ; i++ )
					for (j = 0; j < n; j++ ) {
						T[i][j] = -1;
						if (i == j) D[i][j] = 0;
						else D[i][j] = 500000;
					}
				int rastojanje = 0;
				for ( i = 0; i < n ; i++ )
					for (j = i + 1; j < n; j++ ) {
						grad1 = ids[i];
						grad2 = ids[j];
						upit = "SELECT Rastojanje FROM Linija WHERE ( ( IdGrada1 = " + grad1 + " AND IdGrada2 = " + grad2 +
								" ) OR ( IdGrada1 = " + grad2 + " AND IdGrada2 = " + grad1 + " ) )";
						rs = stmt.executeQuery(upit);
						if ( rs.next() ) {
							rastojanje = rs.getInt(1);
							D[i][j] = rastojanje;
							D[j][i] = rastojanje;
							T[i][j] = i;
							T[j][i] = j;
						}
					}
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for ( k = 0; k < n; k++ )
			for ( i = 0; i < n; i++ )
				for ( j = 0; j < n; j++ ) {
					if ( D[i][j] > D[i][k] + D[k][j]) {
						T[i][j] = T[k][j];
						D[i][j] = D[i][k] + D[k][j];
					}
				}
		return 1;
	}

	@Override
	public int addArticle(int orderId, int articleId, int count) {
		
		int popust = 0;
		upit = "SELECT BrojArtikala FROM Artikal WHERE IdArtikla = " + articleId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			n = rs.getInt(1);
			if (n < count) return -1;
			upit = "SELECT COUNT(*) FROM Stavka WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
			rs = stmt.executeQuery(upit);
			rs.next();
			if (rs.getInt(1) > 0) {
				upit = "SELECT BrojArtikala FROM Stavka WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
				rs = stmt.executeQuery(upit);
				rs.next();
				n = rs.getInt(1);
				n += count;
				
				upit = "SELECT P.Popust FROM Prodavnica P, Artikal A WHERE A.IdArtikla = " + articleId + " AND A.IdProdavnice = P.IdProdavnice";
				rs = stmt.executeQuery(upit);
				rs.next();
				popust = rs.getInt(1);
				
				upit = "UPDATE Stavka SET BrojArtikala = " + n + " , Popust = " + popust + " WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
				stmt.executeUpdate(upit);
				
				upit = "SELECT BrojArtikala FROM Artikal WHERE IdArtikla = " + articleId;
				rs = stmt.executeQuery(upit);
				rs.next();
				n = rs.getInt(1);
				n -= count;
				
				upit = "UPDATE Artikal SET BrojArtikala = " + n + " WHERE IdArtikla = " + articleId;
				stmt.executeUpdate(upit);
				
				upit = "SELECT IdStavke FROM Stavka WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
				rs = stmt.executeQuery(upit);
				rs.next();
				n = rs.getInt(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
				return n;
				
			} else {
				upit = "SELECT P.Popust FROM Prodavnica P, Artikal A WHERE A.IdArtikla = " + articleId + " AND A.IdProdavnice = P.IdProdavnice";
				rs = stmt.executeQuery(upit);
				rs.next();
				popust = rs.getInt(1);
				
				upit = "INSERT INTO Stavka (IdArtikla, IdPorudzbine, BrojArtikala, Popust) VALUES ( " + articleId +
						" , " + orderId + " , " + count +  " , " + popust + " )";
				stmt.executeUpdate(upit);
				
				upit = "SELECT BrojArtikala FROM Artikal WHERE IdArtikla = " + articleId;
				rs = stmt.executeQuery(upit);
				rs.next();
				n = rs.getInt(1);
				n -= count;
				upit = "UPDATE Artikal SET BrojArtikala = " + n + " WHERE IdArtikla = " + articleId;
				stmt.executeUpdate(upit);
				
				upit = "SELECT IdStavke FROM Stavka WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
				rs = stmt.executeQuery(upit);
				rs.next();
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
	public int removeArticle(int orderId, int articleId) {
		
		upit = "SELECT COUNT(*) FROM Stavka WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				if (rs.getInt(1) < 1) return -1;
				
				upit = "SELECT BrojArtikala FROM Stavka WHERE IdArtikla = " + articleId + " AND IdPorudzbine = " + orderId;
				rs = stmt.executeQuery(upit);
				rs.next();
				n = rs.getInt(1);
				
				upit = "SELECT BrojArtikala FROM Artikal WHERE IdArtikla = " + articleId;
				rs = stmt.executeQuery(upit);
				rs.next();
				n += rs.getInt(1);
				
				upit = "UPDATE Artikal SET BrojArtikala = " + n + " WHERE IdArtikla = " + articleId;
				stmt.executeUpdate(upit);
				
				upit = "DELETE FROM Stavka WHERE IdPorudzbine = " + orderId + " AND IdArtikla = " + articleId;
				n = stmt.executeUpdate(upit);
				
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( n > 0 ) return 1;
		return -1;
	}

	@Override
	public List<Integer> getItems(int orderId) {
		
		List<Integer> lista = new ArrayList<Integer>();
		upit = "SELECT IdStavke FROM Stavka WHERE IdPorudzbine = " + orderId;
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
	public int completeOrder(int orderId) {
		
		int pocGrad = 0, trenGrad = 0, grOkup = 0, destinacija = 0;
		Date narudzbina = null, isporuka = null;
		BigDecimal ukCena = null;
		narudzbina = sp130541_GeneralOperations.date;
		CallableStatement cs;
		try {
			cs = connection.prepareCall( "{ call SP_FINAL_PRICE(?,?,?)}" );
			cs.setInt(1, orderId);
			cs.setDate(2, sp130541_GeneralOperations.date);
			cs.registerOutParameter(3, Types.DECIMAL);
			cs.execute();
			ukCena = cs.getBigDecimal(3);
			
			upit = "SELECT K.IdGrada FROM Porudzbina P, Kupac K WHERE P.IdPorudzbine = " + orderId + " AND P.IdKupca = K.IdKupca GROUP BY K.IdGrada";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			rs.next();
			destinacija = rs.getInt(1);
			
			n = inicijalizujNizove();
			if (n == -1) return -1;
			
			int i = 0, j = 0, dan = 0, poc = 0, kraj = 0;
			
			for ( i = 0; i < ids.length; i++) {
				if (ids[i] == destinacija) {
					break;
				}
			}
			
			n = Integer.MAX_VALUE;
			
			for (j = 0; j < ids.length; j++){
				if (D[i][j] < n) {
					upit = "SELECT COUNT (*) FROM Prodavnica WHERE IdGrada = " + ids[j];
					stmt = connection.createStatement();
					rs = stmt.executeQuery(upit);
					rs.next();
					if ( rs.getInt(1) > 0 ) { 
						grOkup = ids[j]; 
						poc = j;
						n = D[i][j];
					}
				}
			}
			
			upit = "SELECT Pr.IdGrada FROM Porudzbina P, Stavka S, Artikal A, Prodavnica Pr WHERE P.IdPorudzbine = " + orderId +
					" AND S.IdPorudzbine = " + orderId + " AND S.IdArtikla = A.IdArtikla AND A.IdProdavnice = Pr.IdProdavnice GROUP BY Pr.IdGrada";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			while (rs.next()) {
				n = rs.getInt(1);
				
				for ( i = 0; i < ids.length; i++) {
					if (ids[i] == n) {
						kraj = i;
						break;
					}
				}

				if ( D[poc][kraj] > dan ) {
					dan = D[poc][kraj];
					pocGrad = n;
				}
			}
			
			trenGrad = pocGrad;
			for ( i = 0; i < ids.length; i++) {
				if (ids[i] == destinacija) {
					kraj = i;
					break;
				}
			}
			
			n = D[poc][kraj];
			if (n < 0) return -1;
			dan += n;
			isporuka = new Date( dan*86400000 + sp130541_GeneralOperations.milisek );
			
			upit = "UPDATE Porudzbina SET UkCena = " + ukCena.toString() + " , DatumNarudzbine = '" + narudzbina + "' , DatumIsporuke = '" 
					+ isporuka + "' , Stanje = 'sent' , PocetniGrad = " + pocGrad +" , TrenutniGrad = " + trenGrad +
					" , GradOkupljanja = " + grOkup + " WHERE IdPorudzbine = " + orderId;
			 n = stmt.executeUpdate(upit);
			 rs.close();
			 rs = null;
			 stmt.close();
			 stmt = null;
			 
			if ( n < 0 ) return -1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public BigDecimal getFinalPrice(int orderId) {
		
		BigDecimal uk = null;
		upit = "SELECT UkCena, Stanje FROM Porudzbina WHERE IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				String stanje = rs.getString(2);
				if (stanje.equals("arrived") || stanje.equals("sent")) {
					uk = rs.getBigDecimal(1);
				} else {
					uk = new BigDecimal(-1);
				}
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uk.setScale(3);
	}

	@Override
	public BigDecimal getDiscountSum(int orderId) {
		
		BigDecimal ukCena = null;
		BigDecimal cena = null;
		upit = "SELECT UkCena, Stanje FROM Porudzbina WHERE IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				String stanje = rs.getString(2);
				if (stanje.equals("arrived") || stanje.equals("sent")) {
					cena = rs.getBigDecimal(1);
				} else {
					return new BigDecimal(-1);
				}
				
				upit = "SELECT coalesce( SUM( S.BrojArtikala * A.Cena ) , 0) FROM Stavka S, Artikal A WHERE S.IdPorudzbine = " + orderId + " AND S.IdArtikla = A.IdArtikla";
				rs = stmt.executeQuery(upit);
				rs.next();
				ukCena = rs.getBigDecimal(1);
				ukCena = ukCena.subtract(cena);
				if ( ukCena.doubleValue() < 0) {
					return new BigDecimal(-1);
				}
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ukCena;
	}

	@Override
	public String getState(int orderId) {

		String stanje = null;
		upit = "SELECT Stanje FROM Porudzbina WHERE IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				stanje = rs.getString(1);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stanje;
	}

	@Override
	public Calendar getSentTime(int orderId) {
		
		Date datum = null;
		String stanje = null;
		upit = "SELECT DatumNarudzbine, Stanje FROM Porudzbina WHERE IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				datum = rs.getDate(1);
				stanje = rs.getString(2);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( !(stanje.equals("sent") || stanje.equals("arrived")) ) return null;
		Calendar kal = new GregorianCalendar();
		kal.setTime(datum);
		return kal;
	}

	@Override
	public Calendar getRecievedTime(int orderId) {
		
		Date datum = null;
		String stanje = null;
		upit = "SELECT DatumIsporuke, Stanje FROM Porudzbina WHERE IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				datum = rs.getDate(1);
				stanje = rs.getString(2);
				rs.close();
				rs = null;
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if ( !(stanje.equals("arrived")) ) return null;
		Calendar kal = new GregorianCalendar();
		kal.setTime(datum);
		return kal;
	}

	@Override
	public int getBuyer(int orderId) {
		
		upit = "SELECT IdKupca From Porudzbina WHERE IdPorudzbine = " + orderId;
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
	public int getLocation(int orderId) {
		
		String stanje = null;
		int grOkup = 0, pocGr = 0, trGr = 0, i = 0, ip = 0, jg = 0, jt = 0;
		upit = "SELECT TrenutniGrad, Stanje, GradOkupljanja, PocetniGrad FROM Porudzbina WHERE IdPorudzbine = " + orderId;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(upit);
			if ( rs.next() ) {
				stanje = rs.getString(2);
				if ( stanje.equals("created") ) return -1;
				trGr = rs.getInt(1);
				grOkup = rs.getInt(3);
				pocGr = rs.getInt(4);
				inicijalizujNizove();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (i = 0; i < ids.length ; i++) {
			if ( ids[i] == trGr) jt = i;
			if ( ids[i] == grOkup) jg = i;
			if ( ids[i] == pocGr) ip = i;
		}
		
		if ( D[ip][jg] > D[ip][jt] ) return grOkup;
		
		return trGr;
	}

}
