USE tests;
EXEC sys.sp_cdc_enable_db;
GO

create table SourcePricingPublish (
  KeyEMDPricing varchar(255) not null, 
  ActiveRow bit not null, 
  InstrumentDeliveryEnd datetime2, 
  InstrumentDeliveryStart datetime2, 
  InstrumentValueAsOf datetime2, 
  InstrumentValuePublishDate datetime2, 
  InstrumentValue double precision not null, 
  KeyInstrument varchar(255), 
  KeyInstrumentLocation int not null, 
  KeyInstrumentMeasure int not null, 
  KeyInstrumentSource int not null, 
  UpdDate datetime2, 
  UpdOperation int not null, 
primary key (keyemdpricing));
GO
EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = 'SourcePricingPublish', @role_name = NULL, @supports_net_changes = 0;
GO