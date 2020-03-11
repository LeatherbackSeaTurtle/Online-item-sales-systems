CREATE PROCEDURE SP_FINAL_PRICE
    @IdP int,
	@TrenutniDatum DATE,
    @UkCena DECIMAL(10,3) OUTPUT 
AS
BEGIN
	DECLARE @Suma DECIMAL(10,3)
	DECLARE @Pom DECIMAL(10,3)
	DECLARE @DodatniPopust int
	DECLARE @IdK int

	SELECT @Suma = SUM(A.Cena * S.BrojArtikala * 0.01 * ( 100 - S.Popust ) )
	FROM Porudzbina P, Artikal A, Stavka S
	WHERE P.IdPorudzbine = @IdP AND S.IdPorudzbine = @IdP AND S.IdArtikla = A.IdArtikla

	SELECT @IdK = IdKupca
	FROM Porudzbina
	WHERE IdPorudzbine = @IdP

	SELECT @Pom = coalesce(SUM(T.Iznos) , 0)
	FROM Transakcija T, Porudzbina P
	WHERE T.IdKupca = @IdK AND P.IdKupca = @IdK AND P.IdPorudzbine = T.IdPorudzbine AND T.Iznos > 0 AND @TrenutniDatum < DATEADD(DAY,30,P.DatumIsporuke)
	
	SET @DodatniPopust = 0

	IF @Pom > 10000
	BEGIN
		SET @DodatniPopust = 2
	END

	SET @UkCena = @Suma * 0.01 * (100 - @DodatniPopust)

END