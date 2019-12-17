-- Create the test database
CREATE DATABASE tests;
GO
USE tests;
EXEC sys.sp_cdc_enable_db;
GO