
CREATE TABLE [Artikal]
( 
	[ImeArtikla]         varchar(100)  NULL ,
	[Cena]               integer  NULL ,
	[IdArtikla]          integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[BrojArtikala]       integer  NULL ,
	[IdProdavnice]       integer  NULL 
)
go

ALTER TABLE [Artikal]
	ADD CONSTRAINT [XPKArtikal] PRIMARY KEY  CLUSTERED ([IdArtikla] ASC)
go

CREATE TABLE [Grad]
( 
	[IdGrada]            integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[ImeGrada]           varchar(100)  NULL 
)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([IdGrada] ASC)
go

CREATE TABLE [Kupac]
( 
	[ImeKupca]           varchar(100)  NULL ,
	[IdKupca]            integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Kredit]             decimal(10,3)  NULL 
	CONSTRAINT [Default_Value_210_1515995540]
		 DEFAULT  0,
	[IdGrada]            integer  NULL 
)
go

ALTER TABLE [Kupac]
	ADD CONSTRAINT [XPKKupac] PRIMARY KEY  CLUSTERED ([IdKupca] ASC)
go

CREATE TABLE [Linija]
( 
	[IdLinije]           integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[IdGrada1]           integer  NULL ,
	[IdGrada2]           integer  NULL ,
	[Rastojanje]         integer  NULL 
)
go

ALTER TABLE [Linija]
	ADD CONSTRAINT [XPKLinija] PRIMARY KEY  CLUSTERED ([IdLinije] ASC)
go

CREATE TABLE [Porudzbina]
( 
	[IdPorudzbine]       integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[IdKupca]            integer  NULL ,
	[Stanje]             varchar(8)  NULL 
	CONSTRAINT [Validation_Rule_260_1029082472]
		CHECK  ( [Stanje]='created' OR [Stanje]='sent' OR [Stanje]='arrived' ),
	[DatumNarudzbine]    datetime  NULL ,
	[DatumIsporuke]      datetime  NULL ,
	[UkCena]             decimal(10,3)  NULL ,
	[PocetniGrad]        integer  NULL ,
	[TrenutniGrad]       integer  NULL ,
	[GradOkupljanja]     integer  NULL 
)
go

ALTER TABLE [Porudzbina]
	ADD CONSTRAINT [XPKPorudzbina] PRIMARY KEY  CLUSTERED ([IdPorudzbine] ASC)
go

CREATE TABLE [Prodavnica]
( 
	[ImeProdavnice]      varchar(100)  NULL ,
	[IdProdavnice]       integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Popust]             integer  NULL ,
	[IdGrada]            integer  NULL ,
	[RacunProdavnice]    decimal(10,3)  NULL 
)
go

ALTER TABLE [Prodavnica]
	ADD CONSTRAINT [XPKProdavnica] PRIMARY KEY  CLUSTERED ([IdProdavnice] ASC)
go

CREATE TABLE [Stavka]
( 
	[IdStavke]           integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[IdArtikla]          integer  NULL ,
	[IdPorudzbine]       integer  NULL ,
	[BrojArtikala]       integer  NULL ,
	[Popust]             integer  NOT NULL 
)
go

ALTER TABLE [Stavka]
	ADD CONSTRAINT [XPKStavka] PRIMARY KEY  CLUSTERED ([IdStavke] ASC)
go

CREATE TABLE [Transakcija]
( 
	[IdTransakcije]      integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[IdProdavnice]       integer  NULL ,
	[IdKupca]            integer  NULL ,
	[Iznos]              decimal(10,3)  NULL ,
	[IdPorudzbine]       integer  NULL 
)
go

ALTER TABLE [Transakcija]
	ADD CONSTRAINT [XPKTransakcija] PRIMARY KEY  CLUSTERED ([IdTransakcije] ASC)
go


ALTER TABLE [Artikal]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdProdavnice]) REFERENCES [Prodavnica]([IdProdavnice])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Kupac]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IdGrada]) REFERENCES [Grad]([IdGrada])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Linija]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdGrada1]) REFERENCES [Grad]([IdGrada])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Linija]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdGrada2]) REFERENCES [Grad]([IdGrada])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Porudzbina]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdKupca]) REFERENCES [Kupac]([IdKupca])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Prodavnica]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdGrada]) REFERENCES [Grad]([IdGrada])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Stavka]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdArtikla]) REFERENCES [Artikal]([IdArtikla])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Stavka]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdPorudzbine]) REFERENCES [Porudzbina]([IdPorudzbine])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transakcija]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdKupca]) REFERENCES [Kupac]([IdKupca])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transakcija]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdProdavnice]) REFERENCES [Prodavnica]([IdProdavnice])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transakcija]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdPorudzbine]) REFERENCES [Porudzbina]([IdPorudzbine])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
