package student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.Date;

import operations.GeneralOperations;

public class sp130541_GeneralOperations implements GeneralOperations {
	
	Connection connection = DB.getInstance().getConnection();
	Statement stmt = null;
	ResultSet rs = null;
	String upit;
	int n;
	
	static Calendar trenutniDatum;
	static java.sql.Date date;
	static long milisek;

	@Override
	public void setInitialTime(Calendar time) {
		
		trenutniDatum = new GregorianCalendar();
		trenutniDatum.setTimeInMillis(time.getTimeInMillis());
		milisek = trenutniDatum.getTimeInMillis();
		date = new Date(milisek);

	}

@Override
public Calendar time(int days) {
		
	trenutniDatum.add(Calendar.DAY_OF_MONTH, days);
	milisek = trenutniDatum.getTimeInMillis();
	date = new Date(milisek);
		
	Date datumKreiranja = null, datumDostave = null;
	int IdP = 0, pocGrad = 0, grOkup = 0, gradDostave = 0, i = 0, poc = 0, kraj = 0, trenGrad = 0;
	Calendar pom = new GregorianCalendar();
	Statement stmt1 = null;
	sp130541_OrderOperation klasa = new sp130541_OrderOperation();
	klasa.inicijalizujNizove();
		
	upit = "SELECT P.IdPorudzbine, P.DatumNarudzbine, P.DatumIsporuke, P.PocetniGrad, P.GradOkupljanja, K.IdGrada, P.TrenutniGrad" +
			" FROM Porudzbina P, Kupac K WHERE P.IdKupca = K.IdKupca AND P.Stanje = 'sent'" + 
			" GROUP BY P.IdPorudzbine, P.DatumNarudzbine, P.DatumIsporuke, P.PocetniGrad, P.GradOkupljanja, K.IdGrada, P.TrenutniGrad";
	try {
		stmt = connection.createStatement();
		stmt1 = connection.createStatement();
		rs = stmt.executeQuery(upit);
		while (rs.next()) {
			IdP = rs.getInt(1);
			datumKreiranja = rs.getDate(2);
			datumDostave = rs.getDate(3);
			pocGrad = rs.getInt(4);
			grOkup = rs.getInt(5);
			gradDostave = rs.getInt(6);
			trenGrad = rs.getInt(7);
			pom.setTime(datumDostave);
			if ( trenutniDatum.after(pom) ) {
				upit = "UPDATE Porudzbina SET Stanje = 'arrived', TrenutniGrad = " + gradDostave + " WHERE IdPorudzbine = " + IdP;
				stmt1.executeUpdate(upit);
			} else {
				
				for ( i = 0; i < klasa.ids.length ; i++ ) {
					if ( klasa.ids[i] == pocGrad ) poc = i;
					if ( klasa.ids[i] == gradDostave ) kraj = i;
				}
				pom.setTime(datumDostave);
				int novi_kraj = 0;
				while ( trenutniDatum.before(pom) ) {
					novi_kraj = klasa.T[poc][kraj];
					n = klasa.D[poc][kraj];
					pom.setTime(datumKreiranja);
					pom.add(Calendar.DAY_OF_MONTH, n);
					trenGrad = klasa.ids[kraj];
					kraj = novi_kraj;
				}
				upit = "UPDATE Porudzbina SET TrenutniGrad = " + trenGrad + " WHERE IdPorudzbine = " + IdP;
				stmt1.executeUpdate(upit);
			}
		}
		rs.close();
		rs = null;
		stmt.close();
		stmt = null;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return trenutniDatum;
}
	
/*	@Override
	public Calendar time(int days) {
		
		trenutniDatum.add(Calendar.DAY_OF_MONTH, days);
		milisek = trenutniDatum.getTimeInMillis();
		date = new Date(milisek);
		
		Date datumKreiranja = null, datumDostave = null;
		int IdP = 0, pocGrad = 0, grOkup = 0, gradDostave = 0, i = 0, poc = 0, kraj = 0, trenGrad = 0;
		Calendar pom = new GregorianCalendar();
		Statement stmt1 = null;
		sp130541_OrderOperation klasa = new sp130541_OrderOperation();
		klasa.inicijalizujNizove();
		
		upit = "SELECT P.IdPorudzbine, P.DatumNarudzbine, P.DatumIsporuke, P.PocetniGrad, P.GradOkupljanja, K.IdGrada, P.TrenutniGrad" +
				" FROM Porudzbina P, Kupac K WHERE P.IdKupca = K.IdKupca AND P.Stanje = 'sent'" + 
				" GROUP BY P.IdPorudzbine, P.DatumNarudzbine, P.DatumIsporuke, P.PocetniGrad, P.GradOkupljanja, K.IdGrada, P.TrenutniGrad";
		try {
			stmt = connection.createStatement();
			stmt1 = connection.createStatement();
			rs = stmt.executeQuery(upit);
			while (rs.next()) {
				IdP = rs.getInt(1);
				datumKreiranja = rs.getDate(2);
				datumDostave = rs.getDate(3);
				pocGrad = rs.getInt(4);
				grOkup = rs.getInt(5);
				gradDostave = rs.getInt(6);
				trenGrad = rs.getInt(7);
				pom.setTime(datumDostave);
				if ( trenutniDatum.after(pom) ) {
					upit = "UPDATE Porudzbina SET Stanje = 'arrived', TrenutniGrad = " + gradDostave + " WHERE IdPorudzbine = " + IdP;
					stmt1.executeUpdate(upit);
				} else {
					pom.setTime(datumKreiranja);
					for ( i = 0; i < klasa.ids.length ; i++ ) {
						if ( klasa.ids[i] == pocGrad ) poc = i;
						if ( klasa.ids[i] == grOkup ) kraj = i;
					}
					n = klasa.D[poc][kraj];
					pom.add(Calendar.DAY_OF_MONTH, n);
					if ( trenutniDatum.after(pom) ) {
						upit = "UPDATE Porudzbina SET TrenutniGrad = " + grOkup + " WHERE IdPorudzbine = " + IdP;
						stmt1.executeUpdate(upit);
					} else {
						pom.setTime(datumDostave);
						int novi_kraj = 0;
						while ( trenutniDatum.before(pom) ) {
							novi_kraj = klasa.T[poc][kraj];
							n = klasa.D[poc][kraj];
							pom.setTime(datumKreiranja);
							pom.add(Calendar.DAY_OF_MONTH, n);
							trenGrad = klasa.ids[novi_kraj];
							kraj = novi_kraj;
						}
						upit = "UPDATE Porudzbina SET TrenutniGrad = " + trenGrad + " WHERE IdPorudzbine = " + IdP;
						stmt1.executeUpdate(upit);
					}
				}
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trenutniDatum;
	}*/

	@Override
	public Calendar getCurrentTime() {
		
		Calendar pom = new GregorianCalendar();
		pom.setTimeInMillis(trenutniDatum.getTimeInMillis());
		return pom;
	}

	@Override
	public void eraseAll() {
		
		upit = "DELETE FROM Linija";
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Transakcija";
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Stavka";
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Artikal";
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Porudzbina";
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Kupac";
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Prodavnica";
			stmt.executeUpdate(upit);
			upit = "DELETE FROM Grad";
			stmt.executeUpdate(upit);
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
