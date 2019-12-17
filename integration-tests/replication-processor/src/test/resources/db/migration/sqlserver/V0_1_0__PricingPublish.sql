
USE tests;
GO

CREATE TABLE PricingPublish (
KeyInstrumentLocation int NOT NULL,
 InstrumentValueAsOf  datetime2 NOT NULL,
 InstrumentValuePublishDate  datetime2 NOT NULL,
 KeyInstrumentMeasure  int NOT NULL,
 InstrumentDeliveryStart  datetime2 NOT NULL,
 UpdDate  datetime2 NOT NULL,
 InstrumentValue  float NOT NULL,
 KeyInstrumentSource  int NOT NULL,
 KeyInstrument  int NOT NULL,
 ActiveRow  bit NOT NULL,
 UpdOperation  int NOT NULL,
 KeyEMDPricing  varchar(900) NOT NULL,
 InstrumentDeliveryEnd  datetime2 NOT NULL,
PRIMARY KEY(KeyEMDPricing));