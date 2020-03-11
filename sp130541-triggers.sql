CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
    ON [dbo].[Porudzbina]
    FOR UPDATE
    AS
    BEGIN
		DECLARE @MyCursor CURSOR
		DECLARE @IdK int
		DECLARE @IdP int
		DECLARE @IdPr int
		DECLARE @pom int
		DECLARE @Ukupno DECIMAL(10,3)
		DECLARE @St varchar(8)

		SET @MyCursor = CURSOR FOR
		Select IdPorudzbine, IdKupca, Stanje, UkCena
		From inserted

		OPEN @MyCursor

		FETCH NEXT FROM @MyCursor
		INTO @IdP, @IdK, @St, @Ukupno

		WHILE @@FETCH_STATUS = 0 
		BEGIN
			IF @St = 'arrived'
			BEGIN
				INSERT INTO Transakcija (IdPorudzbine, IdKupca, IdProdavnice, Iznos) 
					SELECT P.IdPorudzbine, P.IdKupca, A.IdProdavnice, SUM( -1 * ( 0.95 * S.BrojArtikala * A.Cena * 0.01 * (100 - S.Popust) ) )
					FROM Stavka S, Artikal A, Porudzbina P
					WHERE P.IdPorudzbine = @IdP AND P.IdKupca = @IdK AND S.IdPorudzbine = @IdP AND S.IdArtikla = A.IdArtikla
					GROUP BY P.IdPorudzbine, P.IdKupca, A.IdProdavnice
			END

			IF @St = 'sent'
			BEGIN
				
				SELECT @pom = COUNT(*)
				FROM Transakcija
				WHERE IdPorudzbine = @IdP AND IdKupca = @IdK AND IdProdavnice = @IdPr

				IF @pom = 0
				BEGIN

					SELECT @IdPr = A.IdProdavnice
					FROM Stavka S, Artikal A, Porudzbina P
					WHERE P.IdPorudzbine = @IdP AND S.IdPorudzbine = @IdP AND S.IdArtikla = A.IdArtikla
					GROUP BY A.IdProdavnice

					INSERT INTO Transakcija (IdPorudzbine, IdKupca, IdProdavnice, Iznos) VALUES ( @IdP, @IdK, @IdPr, @Ukupno)

				END

			END

			FETCH NEXT FROM @MyCursor
			INTO @IdP, @IdK, @St, @Ukupno
		END

		CLOSE @MyCursor
		DEALLOCATE @MyCursor


    END